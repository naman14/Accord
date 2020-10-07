package com.naman14.accord.library.analyser

import com.naman14.accord.library.*
import java.util.regex.Pattern

/**
 * Analyser for possible memory leaks. These traces are not guaranteed to be true as a lot of factors
 * can affect the data used here. By default, the traces analysis from here do not fail the tests and only
 * warn for possible memory leaks. should_fail_possible_memory_leaks in AccordConfig can be changed to
 * fail the tests if possible memory leaks are found.
 *
 * App contexts - Number of app contexts in memory. High number of app contexts indicates some contexts
 * are leaking. This can be configured if the high number of contexts is expected.
 *
 * View Roots -  Number of VIewRoots currently in memory. Each window has its own ViewRoot (activities,
 * dialogs)
 *
 * Activities - Number of activities currently in memory. If the number of activities exceed the
 * number of view roots, this probably indicates that some activities are leaking
 */
class MemoryLeakAnalyser(val verdictListener: TraceVerdictListener) : Analyser {

    companion object {
        val VIEW_ROOTS_PATTERN =
            Pattern.compile("Views:\\s+(\\d+)\\s+ViewRootImpl:\\s+(\\d+)")

        val APP_CONTEXT_ACTIVITIES_PATTERN =
            Pattern.compile("AppContexts:\\s+(\\d+)\\s+Activities:\\s+(\\d+)")
    }

    override fun analyseTrace(traceLine: String, config: AccordConfig) {
        var numViewRoots = 0
        var numActivities = 0

        VIEW_ROOTS_PATTERN.matcher(traceLine).apply {
            if (find()) {
                val viewRoots = group(2)
                viewRoots?.let {
                    numViewRoots = viewRoots.toInt()

                } ?: run {
                    warnOrFail(config, "Unable to find current view roots in memory")
                    return
                }

            }
        }

        APP_CONTEXT_ACTIVITIES_PATTERN.matcher(traceLine).apply {
            if (find()) {
                val appContexts = group(1)
                val activities = group(2)
                appContexts?.let {
                    if (appContexts.toInt() > config.max_app_contexts) {
                        warnOrFail(
                            config, "Number of AppContexts in memory exceeding " +
                                    "${config.max_app_contexts}. AppContexts currently in memory " +
                                    "$appContexts. This probably means that some contexts are leaking. " +
                                    "If you think this is expected, increase the max app contexts " +
                                    "limit in AccordConfig or disable MemoryLeakTracker"
                        )
                    } else {
                        verdictListener.onTraceVerdict(
                            TraceVerdict(
                                true,
                                "AppContext leak analysis passed. Found $appContexts AppContexts in memory"
                            )
                        )
                    }
                } ?: warnOrFail(config, "Unable to find current app contexts in memory")

                activities?.let {
                    numActivities = activities.toInt()
                } ?: run {
                    warnOrFail(config, "Unable to find current activities in memory")
                    return
                }
            }
        }


        if (numActivities - numViewRoots > 1) {
            warnOrFail(config, "Counts of ViewRoots and Activities differing by more than 1. " +
                    "$numViewRoots ViewRoots and $numActivities found. " +
                    "Its possible that some activities are leaking and are still retained in memory " +
                    "while the corresponding ViewRoot is no longer in memory. If you think this " +
                    "is expected, disable MemoryLeakTracer")
        }

    }

    private fun warnOrFail(config: AccordConfig, message: String) {
        if (config.should_fail_possible_memory_leaks) {
            verdictListener.onTraceVerdict(
                TraceVerdict(
                    false,
                    "POSSIBLE MEMORY LEAK!! $message"
                )
            )
        } else {
            verdictListener.onTraceVerdict(
                TraceVerdict(
                    true,
                    "POSSIBLE MEMORY LEAK!! $message"
                )
            )
        }
    }
}