package com.shenghaiyang.pump.gradle.store

import org.apache.commons.io.FileUtils
import java.io.File


class StoreFileTransform(
        private val destFile: File
) : StoreTransform {

    override fun transform(mapping: Map<String, String>) {
        val inputBytes = destFile.readBytes()
        val newContent = StoreClassVisitor.visitStoreClass(inputBytes, mapping)
        FileUtils.writeByteArrayToFile(destFile, newContent)
    }


}