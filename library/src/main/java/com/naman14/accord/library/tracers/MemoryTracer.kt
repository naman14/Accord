package com.naman14.accord.library.tracers

import com.naman14.accord.library.AccordConfig
import com.naman14.accord.library.TraceVerdict
import com.naman14.accord.library.TraceVerdictListener
import com.naman14.accord.library.analyser.MemoryAnalyser
import com.naman14.accord.library.executeShellCommand
import java.io.InputStreamReader

/**
 * Tracer for memory data from adb
 */
class MemoryTracer(private val packageName: String): Tracer {

    private lateinit var pid: String

    override fun beginTrace() {
        val pidStdout = executeShellCommand("pidof $packageName")
        pid = InputStreamReader(pidStdout).buffered().readLine().trimEnd()
        executeShellCommand("dumpsys meminfo $pid reset")
    }

    override fun endTrace(config: AccordConfig): List<TraceVerdict> {
        val verdicts = arrayListOf<TraceVerdict>()
        val analyser = MemoryAnalyser(verdictListener = object: TraceVerdictListener {
            override fun onTraceVerdict(verdict: TraceVerdict) {
                verdicts.add(verdict)
            }
        })
        val stdout = executeShellCommand("dumpsys meminfo $pid")
        InputStreamReader(stdout).buffered().useLines { lines ->
            lines.forEach { line ->
                analyser.analyseTrace(line, config)
            }
        }
        return verdicts

    }

}