package com.shenghaiyang.pump.gradle

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.TransformInvocation
import com.android.build.api.transform.TransformOutputProvider
import org.apache.commons.io.FileUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream

/**
 * Inflater store transform, insert view binding inflater mapping
 *
 * @author shenghaiyang
 */
class StoreTransform(private val mCache: ConcurrentHashMap<String, String>) : AbsPumpTransform() {

    /** mark store class is found */
    private var mFound = false

    override fun transform(transformInvocation: TransformInvocation) {
        mFound = false
        super.transform(transformInvocation)
    }

    override fun transformDir(dirInput: DirectoryInput, outputProvider: TransformOutputProvider) {
        val dest = outputProvider.getContentLocation(dirInput.name, dirInput.contentTypes, dirInput.scopes, Format.DIRECTORY)

        FileUtils.copyDirectory(dirInput.file, dest)

        if (mFound) {
            return
        }

        val classFiles = FileUtils.listFiles(dirInput.file, arrayOf(CLASS_FILE_SUFFIX), true)
        classFiles.forEach { classFile ->
            if (mFound) {
                return
            }
            classFile.inputStream().use { inputStream ->
                val cr = ClassReader(inputStream)
                if (NAME_STORE == cr.className) {
                    val storePath = classFile.absolutePath.replace(dirInput.file.absolutePath, dest.absolutePath)
                    val storeFile = File(storePath)
                    val storeContent = visitStoreClass(cr)
                    FileUtils.writeByteArrayToFile(storeFile, storeContent)
                    mFound = true
                }
            }
        }
    }

    override fun transformJarEntry(jarEntry: JarEntry, content: ByteArray, jos: JarOutputStream) {
        if (!jarEntry.isClassEntry()) {
            // not class file
            jos.putNewEntry(jarEntry.name, content)
            return
        }
        val cr = ClassReader(content)
        if (NAME_STORE != cr.className) {
            // not the store class
            jos.putNewEntry(jarEntry.name, content)
            return
        }
        // transform store class
        val storeContent = visitStoreClass(cr)
        jos.putNewEntry(jarEntry.name, storeContent)
        mFound = true
    }

    private fun visitStoreClass(cr: ClassReader): ByteArray {
        val cw = ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
        val cv = StoreVisitor(Opcodes.ASM5, cw, mCache)
        cr.accept(cv, ClassReader.EXPAND_FRAMES)
        return cw.toByteArray()
    }

}