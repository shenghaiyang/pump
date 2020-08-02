package com.shenghaiyang.pump.gradle

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.util.concurrent.ConcurrentHashMap

/**
 * Pump plugin
 *
 * @author shenghaiyang
 */
class PumpPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val appExtension = project.extensions.findByType(AppExtension::class.java)
        requireNotNull(appExtension) { "Could not get app extension" }
        // store binding and inflater mapping
        val mapping = ConcurrentHashMap<String, String>()
        appExtension.registerTransform(InflaterTransform(mapping))
        appExtension.registerTransform(StoreTransform(mapping))
    }
}