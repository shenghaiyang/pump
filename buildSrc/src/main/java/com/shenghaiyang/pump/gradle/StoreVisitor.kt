package com.shenghaiyang.pump.gradle

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor

/**
 * Inflater store class visitor
 *
 * @author shenghaiyang
 */
class StoreVisitor(
        api: Int,
        classVisitor: ClassVisitor,
        private val mMapping: Map<String, String>
) : ClassVisitor(api, classVisitor) {

    override fun visitMethod(access: Int, name: String?, descriptor: String?, signature: String?, exceptions: Array<out String>?): MethodVisitor {
        val mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        if (NAME_METHOD_INIT == name) {
            return StoreConstructorVisitor(mv, access, name, descriptor, mMapping)
        }
        return mv
    }
}