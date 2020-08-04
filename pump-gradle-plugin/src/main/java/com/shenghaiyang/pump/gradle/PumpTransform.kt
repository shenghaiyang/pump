package com.shenghaiyang.pump.gradle

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.TransformInvocation
import com.android.build.api.transform.TransformOutputProvider
import com.shenghaiyang.pump.gradle.inflater.InflaterClassAdapter
import com.shenghaiyang.pump.gradle.store.StoreFileTransform
import com.shenghaiyang.pump.gradle.store.StoreJarTransform
import com.shenghaiyang.pump.gradle.store.StoreTransform
import org.apache.commons.io.FileUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.File
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream


class PumpTransform : AbsPumpTransform() {

    private val mapping = hashMapOf<String, String>()
    private var storeTransform: StoreTransform? = null

    override fun transform(transformInvocation: TransformInvocation) {
        mapping.clear()

        super.transform(transformInvocation)

        storeTransform?.transform(mapping)
    }

    override fun transformDir(dirInput: DirectoryInput, outputProvider: TransformOutputProvider) {
        val dest = outputProvider.getContentLocation(dirInput.name, dirInput.contentTypes, dirInput.scopes, Format.DIRECTORY)
        FileUtils.copyDirectory(dirInput.file, dest)

        val classFiles = FileUtils.listFiles(dirInput.file, arrayOf(CLASS_FILE_SUFFIX), true)
        classFiles.forEach { classFile ->
            classFile.inputStream().use { inputStream ->
                val cr = ClassReader(inputStream)
                if (cr.isViewBindingClass()) {
                    val destClassPath = classFile.absolutePath.replace(dirInput.file.absolutePath, dest.absolutePath)
                    val bindingName = cr.className
                    val inflaterName = generateInflaterName(bindingName)
                    mapping[bindingName] = inflaterName

                    val inflaterPath = destClassPath.replace(bindingName, inflaterName)
                    val inflaterFile = File(inflaterPath)
                    val inflaterContent = visitInflaterContent(bindingName, inflaterName)
                    FileUtils.writeByteArrayToFile(inflaterFile, inflaterContent)
                } else if (cr.isStoreClass()) {
                    val destPath = classFile.absolutePath.replace(dirInput.file.absolutePath, dest.absolutePath)
                    storeTransform = StoreFileTransform(File(destPath))
                }
            }
        }
    }

    override fun transformJarEntry(jarEntry: JarEntry, content: ByteArray, jos: JarOutputStream, dest: File) {
        jos.putNewEntry(jarEntry.name, content)
        if (!jarEntry.isClassEntry()) {
            // not class file
            return
        }
        val cr = ClassReader(content)
        if (cr.isViewBindingClass()) {
            val bingingName = cr.className
            val inflaterName = generateInflaterName(bingingName)
            mapping[bingingName] = inflaterName

            val inflaterContent = visitInflaterContent(cr.className, inflaterName)
            val inflaterEntryName = jarEntry.name.replace(bingingName, inflaterName)
            jos.putNewEntry(inflaterEntryName, inflaterContent)
        } else if (cr.isStoreClass()) {
            storeTransform = StoreJarTransform(jarEntry.name, dest)
        }
    }

    private fun visitInflaterContent(bindingName: String, inflaterName: String): ByteArray {
        val cw = ClassWriter(ClassWriter.COMPUTE_MAXS)
        return InflaterClassAdapter.visit(cw, bindingName, inflaterName)
    }

}
