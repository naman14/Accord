package com.naman14.accord.plugin

import org.gradle.api.Project
import java.io.File
import java.util.*

object Utils {

    fun getConnectedDevices(project: Project): List<String> {
        val devices = arrayListOf<String>()
        with(ProcessBuilder().command(getAdb(project), "devices", "-l").start()) {
            waitFor()
            if (exitValue() != 0) {
                throw Exception("Error using adb")
            } else {
                inputStream.bufferedReader().useLines { lines ->
                    lines.iterator().forEach {
                        if (!it.startsWith("List of devices attached") && it != "") {
                            devices.add(it.split("\\s".toRegex())[0])
                        }
                    }
                }
            }
        }
        return devices
    }

    fun getAdb(project: Project): String {
        return getSdkDir(project) + File.separator + "platform-tools" + File.separator + "adb"
    }

    fun getSdkDir(project: Project): String {
        val property =  Properties().apply {
            load(File(project.rootDir, "local.properties").inputStream())
        }
        return property.getProperty("sdk.dir")
    }
}