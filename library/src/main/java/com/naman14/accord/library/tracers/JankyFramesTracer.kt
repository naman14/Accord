package com.naman14.accord.library.tracers

import com.naman14.accord.library.AccordConfig
import com.naman14.accord.library.TraceVerdict
import com.naman14.accord.library.TraceVerdictListener
import com.naman14.accord.library.analyser.JankyFramesAnalyser
import com.naman14.accord.library.executeShellCommand
import java.io.InputStreamReader

/**
 * Tracer for janky frames from adb
 */
class JankyFramesTracer(private val packageName: String): Tracer {

    override fun beginTrace() {
        // reset the data from last run
        executeShellCommand("dumpsys gfxinfo $packageName reset")
    }

    override fun endTrace(config: AccordConfig): List<TraceVerdict> {
        val verdicts = arrayListOf<TraceVerdict>()
        val analyser = JankyFramesAnalyser(verdictListener = object: TraceVerdictListener {
            override fun onTraceVerdict(verdict: TraceVerdict) {
                verdicts.add(verdict)
            }
        })
        val stdout = executeShellCommand("dumpsys gfxinfo $packageName")
        InputStreamReader(stdout).buffered().useLines { lines ->
            lines.forEach { line ->
                analyser.analyseTrace(line, config)
            }
        }
        return verdicts
    }
}