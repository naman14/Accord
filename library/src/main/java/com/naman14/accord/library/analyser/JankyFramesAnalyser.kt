package com.naman14.accord.library.analyser

import com.naman14.accord.library.*
import java.util.regex.Pattern


/**
 * Parse and analyse janky frames data from adb
 */
class JankyFramesAnalyser(private val verdictListener: TraceVerdictListener) : Analyser {

    companion object {
        val JANKY_FRAMES_PATTERN = Pattern.compile("Janky frames: (\\d+) \\(([\\d\\.]+)%\\)")
    }

    override fun analyseTrace(traceLine: String, config: AccordConfig) {
        JANKY_FRAMES_PATTERN.matcher(traceLine).apply {
            if (find()) {
                val jankPercent = group(2)
                jankPercent ?: kotlin.run {
                    verdictListener.onTraceVerdict(
                        TraceVerdict(
                            false,
                            "Unable to find jank percent"
                        )
                    )
                    return
                }

                if (jankPercent.toDouble() > config.max_jank_percent) {
                    verdictListener.onTraceVerdict(
                        TraceVerdict(
                            false, "Janky frames percent exceeded threshold (" +
                                    "threshold ${config.max_jank_percent} %, found $jankPercent %)"
                        )
                    )
                } else {
                    verdictListener.onTraceVerdict(
                        TraceVerdict(
                            true, "Janky frames analysis passed (" +
                                    "threshold ${config.max_jank_percent} %, found $jankPercent %)"
                        )
                    )
                }
            }
        }
    }

}