package com.shenghaiyang.pump.gradle

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import java.io.File
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream


abstract class AbsPumpTransform : Transform() {

    override fun getName(): String = javaClass.name

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> = TransformManager.CONTENT_CLASS

    override fun isIncremental(): Boolean = false

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> = TransformManager.SCOPE_FULL_PROJECT

    override fun transform(transformInvocation: TransformInvocation) {
        super.transform(transformInvocation)

        val inputs = transformInvocation.inputs
        val outputProvider = transformInvocation.outputProvider
        inputs.forEach {
            transformJarInputs(it.jarInputs, outputProvider)
            transformDirectoryInputs(it.directoryInputs, outputProvider)
        }
    }

    private fun transformJarInputs(jarInputs: Collection<JarInput>, outputProvider: TransformOutputProvider) {
        jarInputs.forEach { jarInput ->
            transformJar(jarInput, outputProvider)
        }
    }

    private fun transformJar(jarInput: JarInput, outputProvider: TransformOutputProvider) {
        val dest = outputProvider.getContentLocation(jarInput.name, jarInput.contentTypes, jarInput.scopes, Format.JAR)
        val jarFile = JarFile(jarInput.file)
        val entries = jarFile.entries()
        dest.outputStream().use { fos ->
            JarOutputStream(fos).use { jos ->
                while (entries.hasMoreElements()) {
                    val jarEntry = entries.nextElement()

                    jarFile.getInputStream(jarEntry).use { jis ->
                        val entryContent = jis.readBytes()
                        transformJarEntry(jarEntry, entryContent, jos, dest)
                    }
                }
            }
        }
    }

    /**
     * transform the jar entry
     *
     * @param jarEntry the jar entry
     * @param content content of the jar entry
     * @param jos dest jar output stream, implements should not close it
     */
    abstract fun transformJarEntry(jarEntry: JarEntry, content: ByteArray, jos: JarOutputStream, dest: File)

    private fun transformDirectoryInputs(directoryInputs: Collection<DirectoryInput>, outputProvider: TransformOutputProvider) {
        directoryInputs.forEach { dirInput ->
            transformDir(dirInput, outputProvider)
        }
    }

    abstract fun transformDir(dirInput: DirectoryInput, outputProvider: TransformOutputProvider)


}