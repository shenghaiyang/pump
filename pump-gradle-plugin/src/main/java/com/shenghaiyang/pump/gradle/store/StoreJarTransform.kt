package com.shenghaiyang.pump.gradle.store

import com.shenghaiyang.pump.gradle.putNewEntry
import org.apache.commons.io.FileUtils
import java.io.File
import java.util.jar.JarFile
import java.util.jar.JarOutputStream


class StoreJarTransform(
        private val jarEntryName: String,
        private val destFile: File
) : StoreTransform {

    override fun transform(mapping: Map<String, String>) {
        // TODO avoid use tmp file
        val tmpFile = File(destFile.absolutePath.replace(destFile.name, "${destFile.name}-tmp"))
        FileUtils.deleteQuietly(tmpFile)
        val jarFile = JarFile(destFile)
        val entries = jarFile.entries()
        tmpFile.outputStream().use { fos ->
            JarOutputStream(fos).use { jos ->
                while (entries.hasMoreElements()) {
                    val jarEntry = entries.nextElement()
                    jarFile.getInputStream(jarEntry).use { jis ->
                        val entryContent = jis.readBytes()
                        val newEntryContent = if (jarEntry.name == jarEntryName) {
                            StoreClassVisitor.visitStoreClass(entryContent, mapping)
                        } else {
                            entryContent
                        }
                        jos.putNewEntry(jarEntry.name, newEntryContent)
                    }
                }
            }
        }
        FileUtils.copyFile(tmpFile, destFile)
        FileUtils.forceDelete(tmpFile)
    }

}