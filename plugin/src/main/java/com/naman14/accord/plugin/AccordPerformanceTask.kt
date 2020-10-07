package com.naman14.accord.plugin

import com.android.build.gradle.AndroidConfig
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import java.lang.IllegalStateException

/**
 * Task for running the performance tests on individual devices
 */
open class AccordPerformanceTask: DefaultTask() {

    companion object {
        const val DEFAULT_INSTRUMENTATION_RUNNER = "androidx.test.runner.AndroidJUnitRunner"

        // only the tests marked with this annotation are run
        const val ANNOTATION_CLASS = "com.naman14.accord.library.annotation.AccordTest"
    }

    var deviceId: String = ""

    init {
        group = "verification"
        description = "Run performance tests on device"
        outputs.upToDateWhen({false})
    }

    @TaskAction
    fun run() {
        val config = project.extensions.findByName("android") as AndroidConfig

        val extensions = project.extensions.findByName("accord") as AccordExtension

        val instrumentationRunner = config.defaultConfig.testInstrumentationRunner
            ?: DEFAULT_INSTRUMENTATION_RUNNER

        val testApplicationId = extensions.testApplicationId
            ?: config.defaultConfig.testApplicationId
            ?: throw IllegalStateException("Test applicationId not provided in accord configuration" +
                    "or in defaultConfig")

        val instrumentationCommand: List<String> = extensions.instrumentationCommand
            ?: arrayListOf(
                Utils.getAdb(project),
                "shell",
                "am",
                "instrument",
                "-w",
                "-e annotation $ANNOTATION_CLASS",
                "$testApplicationId/$instrumentationRunner"
            )

        with(ProcessBuilder().apply {
            command(instrumentationCommand)
            redirectErrorStream(true)
        }.start()) {

            inputStream.bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    logger.warn(line)

                    // if we find AccordException here, tell to gradle the task has failed
                    if (line.contains("AccordException")) {
                        throw GradleException(line)
                    }
                }
            }
            waitFor()
            if (exitValue() != 0) {
                throw GradleException("Instrumentation return with non zero exit code")
            }
            logger.warn("All tests are successful")
        }
    }
}