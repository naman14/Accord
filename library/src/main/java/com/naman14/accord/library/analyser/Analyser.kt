package com.naman14.accord.library.analyser

import com.naman14.accord.library.AccordConfig

interface Analyser {

    fun analyseTrace(traceLine: String, config: AccordConfig)
}