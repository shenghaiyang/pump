package com.shenghaiyang.pump.gradle

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project


class PumpPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val appExtension = project.extensions.findByType(AppExtension::class.java)
        requireNotNull(appExtension) { "Could not get app extension" }
        appExtension.registerTransform(PumpTransform())
    }
}