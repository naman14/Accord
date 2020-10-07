package com.naman14.accord.library

import android.app.Instrumentation
import android.os.Bundle
import android.os.ParcelFileDescriptor
import androidx.test.platform.app.InstrumentationRegistry
import java.io.*

/**
 * Extension function to log events to console. Normal logging events are not
 * propagated through the instrumentation process. We create a instrumentation status on each event
 * and send that to the registry to streamline events.
 */
fun log(s: String) {
    val b = Bundle()
    b.putString(Instrumentation.REPORT_KEY_STREAMRESULT, "\n" + s)
    InstrumentationRegistry.getInstrumentation().sendStatus(0, b)
}

/**
 * Extension function to execute commands as a shell user using uiautomation
 */
fun executeShellCommand(command: String): InputStream {
    val stdout = InstrumentationRegistry.getInstrumentation().uiAutomation
        .executeShellCommand(command)
    return ParcelFileDescriptor.AutoCloseInputStream(stdout)
}