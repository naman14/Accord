package com.naman14.accord.library.tracers

import com.naman14.accord.library.AccordConfig
import com.naman14.accord.library.TraceVerdict

/**
 * Interface for a generic tracer. A tracer can use different methods to trace crucial information
 * for a test and give a verdict if the test was successful or not
 */
interface Tracer {

    /**
     * Begin the trace. Ideal for resetting previous states.
     */
    fun beginTrace()

    /**
     * End the trace
     * @param config containing the different thresholds set for this test
     * Analyse the trace data and compare them with thresholds to reach a verdict.
     */
    fun endTrace(config: AccordConfig): List<TraceVerdict>
}