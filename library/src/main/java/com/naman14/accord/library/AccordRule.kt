package com.naman14.accord.library

import com.naman14.accord.library.annotation.PerfTest
import com.naman14.accord.library.annotation.TimeTest
import com.naman14.accord.library.tracers.*
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.lang.Exception

/**
 * JUnit rule that initialises the different performance tracers before each test run,
 * and analyses the trace data after each test run
 * @param packageName: used in tracers to get info from adb for the package
 * @param config: optional config for setting the thresholds against which the analysis will be done
 * @param tracers: the tracers to be added for this rule
 * @see AccordConfig
 */
class AccordRule(
    private val packageName: String,
    private val config: AccordConfig? = AccordConfig(),
    private val tracers: List<Tracer> = arrayListOf(
        JankyFramesTracer(packageName),
        FrameTimesTracer(packageName),
        MemoryTracer(packageName),
        MemoryLeakTracer(packageName)
    )
) : TestRule {

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                // run performance tracers on tests marked with PerfTest
                description.getAnnotation(PerfTest::class.java)?.let {
                    performanceTest(base)
                }
                // run time logging for tests marked with TimeTest
                description.getAnnotation(TimeTest::class.java)?.let { timeTest ->
                    val threshold = timeTest.threshold
                    val repeatCount = timeTest.repeatCount
                    if (threshold == -1) throw AccordException(
                        "Threshold must be provided" +
                                "for TimeTest"
                    )
                    timeTest(base, threshold, repeatCount)
                }
            }
        }
    }

    private fun performanceTest(statement: Statement) {
        val testVerdicts = arrayListOf<TraceVerdict>()
        tracers.forEach { tracer ->
            tracer.beginTrace()
        }

        try {
            statement.evaluate()
        } catch (e: Exception) {
            /**
             * wrap all exceptions in AccordException for us to identify them and fail the test tasks
             * @see AccordException
             */
            throw AccordException(e.message!!)
        }

        tracers.forEach { tracer ->
            testVerdicts.addAll(tracer.endTrace(config!!))
        }

        val failedLogs = arrayListOf<String>()
        val successLogs = arrayListOf<String>()

        testVerdicts.forEach { verdict ->
            if (verdict.passed) {
                successLogs.add(verdict.message)
            } else {
                failedLogs.add(verdict.message)
            }
        }

        if (successLogs.isNotEmpty()) {
            log(successLogs.toString())
        }

        if (failedLogs.isNotEmpty()) {
            throw AccordException("Performance tests failed $failedLogs")
        }

    }

    private fun timeTest(statement: Statement, threshold: Int, repeatCount: Int) {
        val executionTimes = arrayListOf<Long>()
        for (i in 1 until repeatCount + 1) {
            log("Starting TimeTest iteration $i/$repeatCount")
            val startTime = System.currentTimeMillis()
            try {
                statement.evaluate()
            } catch (e: Exception) {
                throw AccordException(e.message!!)
            } finally {
                val endTime = System.currentTimeMillis()
                val time = endTime - startTime
                executionTimes.add(time)
            }
        }
        val averageTime = executionTimes.average()
        if (averageTime > threshold) {
            throw AccordException(
                "TimeTest failed. (" +
                        "threshold $threshold ms, actual average time $averageTime)." +
                        "Iteration count $repeatCount. Individual times $executionTimes"
            )
        } else {
            log(
                "TimeTest passed. (" +
                        "threshold $threshold ms, actual average time $averageTime)"
            )
        }
    }
}