package com.shenghaiyang.pump.gradle.store

import com.shenghaiyang.pump.gradle.NAME_METHOD_INIT
import org.objectweb.asm.*


class StoreClassVisitor(
        api: Int,
        classVisitor: ClassVisitor,
        private val mapping: Map<String, String>
) : ClassVisitor(api, classVisitor) {

    override fun visitMethod(access: Int, name: String?, descriptor: String?, signature: String?, exceptions: Array<out String>?): MethodVisitor {
        val mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        if (NAME_METHOD_INIT == name) {
            return StoreConstructorVisitor(mv, access, name, descriptor, mapping)
        }
        return mv
    }

    companion object {

        @JvmStatic
        fun visitStoreClass(inputBytes: ByteArray, mapping: Map<String, String>): ByteArray {
            val cr = ClassReader(inputBytes)
            val cw = ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
            val cv = StoreClassVisitor(Opcodes.ASM5, cw, mapping)
            cr.accept(cv, ClassReader.EXPAND_FRAMES)
            return cw.toByteArray()
        }
    }
}