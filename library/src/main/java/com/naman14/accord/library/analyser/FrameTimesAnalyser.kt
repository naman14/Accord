package com.naman14.accord.library.analyser

import com.naman14.accord.library.*
import java.util.regex.Pattern


/**
 * Parse and analyse frame times data from adb
 */
class FrameTimesAnalyser(private val verdictListener: TraceVerdictListener) : Analyser {

    companion object {
        val FRAME_TIME_50TH_PATTERN = Pattern.compile("\\s*50th percentile: (\\d+)ms")
        val FRAME_TIME_90TH_PATTERN = Pattern.compile("\\s*90th percentile: (\\d+)ms")
        val FRAME_TIME_95TH_PATTERN = Pattern.compile("\\s*95th percentile: (\\d+)ms")
        val FRAME_TIME_99TH_PATTERN = Pattern.compile("\\s*99th percentile: (\\d+)ms")
    }

    override fun analyseTrace(traceLine: String, config: AccordConfig) {
        FRAME_TIME_50TH_PATTERN.matcher(traceLine).apply {
            if (find()) {
                val time = group(1)
                time ?: kotlin.run {
                    verdictListener.onTraceVerdict(
                        TraceVerdict(
                            false,
                            "Unable to find 50th percentile frame times"
                        )
                    )
                    return
                }

                if (time.toInt() > config.frame_time_50th_percentile) {
                    verdictListener.onTraceVerdict(
                        TraceVerdict(
                            false, "50th percentile frame time exceeded threshold (" +
                                    "threshold ${config.frame_time_50th_percentile} ms, found $time ms)"
                        )
                    )
                } else {
                    verdictListener.onTraceVerdict(
                        TraceVerdict(
                            true, "50th percentile frame time analysis passed (" +
                                    "threshold ${config.frame_time_50th_percentile} ms, found $time ms)"
                        )
                    )
                }
            }
        }
        FRAME_TIME_90TH_PATTERN.matcher(traceLine).apply {
            if (find()) {
                val time = group(1)
                time ?: kotlin.run {
                    verdictListener.onTraceVerdict(
                        TraceVerdict(
                            false,
                            "Unable to find 90th percentile frame times"
                        )
                    )
                    return
                }

                if (time.toInt() > config.frame_time_90th_percentile) {
                    verdictListener.onTraceVerdict(
                        TraceVerdict(
                            false, "90th percentile frame time exceeded threshold (" +
                                    "threshold ${config.frame_time_90th_percentile} ms, found $time ms)"
                        )
                    )
                } else {
                    verdictListener.onTraceVerdict(
                        TraceVerdict(
                            true, "90th percentile frame time analysis passed (" +
                                    "threshold ${config.frame_time_90th_percentile} ms %, found $time ms %)"
                        )
                    )
                }
            }
        }
        FRAME_TIME_95TH_PATTERN.matcher(traceLine).apply {
            if (find()) {
                val time = group(1)
                time ?: kotlin.run {
                    verdictListener.onTraceVerdict(
                        TraceVerdict(
                            false,
                            "Unable to find 95th percentile frame times"
                        )
                    )
                    return
                }

                if (time.toInt() > config.frame_time_95th_percentile) {
                    verdictListener.onTraceVerdict(
                        TraceVerdict(
                            false, "95th percentile frame time exceeded threshold (" +
                                    "threshold ${config.frame_time_95th_percentile} ms, found $time ms)"
                        )
                    )
                } else {
                    verdictListener.onTraceVerdict(
                        TraceVerdict(
                            true, "95th percentile frame time analysis passed (" +
                                    "threshold ${config.frame_time_95th_percentile} ms, found $time ms)"
                        )
                    )
                }
            }
        }
        FRAME_TIME_99TH_PATTERN.matcher(traceLine).apply {
            if (find()) {
                val time = group(1)
                time ?: kotlin.run {
                    verdictListener.onTraceVerdict(
                        TraceVerdict(
                            false,
                            "Unable to find 99th percentile frame times"
                        )
                    )
                    return
                }

                if (time.toInt() > config.frame_time_99th_percentile) {
                    verdictListener.onTraceVerdict(
                        TraceVerdict(
                            false,
                            "99th percentile frame time exceeded threshold (" +
                                    "threshold ${config.frame_time_99th_percentile} ms, found $time ms)"
                        )
                    )
                } else {
                    verdictListener.onTraceVerdict(
                        TraceVerdict(
                            true,
                            "99th percentile frame time analysis passed (" +
                                    "threshold ${config.frame_time_99th_percentile} ms, found $time ms)"
                        )
                    )
                }
            }
        }
    }

}