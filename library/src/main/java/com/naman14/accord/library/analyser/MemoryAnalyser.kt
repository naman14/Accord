package com.naman14.accord.library.analyser

import com.naman14.accord.library.*
import java.util.regex.Pattern

/**
 * Analyser for total memory usage at the end of test for the app.
 * This only checks for the total memory usage and not individual memory allocations
 */
class MemoryAnalyser(val verdictListener: TraceVerdictListener) : Analyser {

    companion object {
        val TOTAL_MEMORY_USAGE_PATTERN = Pattern.compile("TOTAL:\\s+(\\d+)\\s+TOTAL SWAP PSS:")
    }

    override fun analyseTrace(traceLine: String, config: AccordConfig) {
        TOTAL_MEMORY_USAGE_PATTERN.matcher(traceLine).apply {
            if (find()) {
                val usage = group(1)
                usage ?: kotlin.run {
                    verdictListener.onTraceVerdict(
                        TraceVerdict(
                            false,
                            "Unable to find memory usage"
                        )
                    )
                    return
                }

                if (usage.toInt() > config.max_memory_usage) {
                    verdictListener.onTraceVerdict(
                        TraceVerdict(
                            false,
                            "Total memory usage exceeded threshold (" +
                                    "threshold ${config.max_memory_usage} kb, found $usage kb)"
                        )
                    )
                } else {
                    verdictListener.onTraceVerdict(
                        TraceVerdict(
                            true,
                            "Memory usage analysis passed (" +
                                    "threshold ${config.max_memory_usage} kb, found $usage kb)"
                        )
                    )
                }
            }
        }
    }
}