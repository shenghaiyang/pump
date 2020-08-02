package com.shenghaiyang.pump.gradle

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Label
import org.objectweb.asm.Opcodes


/**
 * View binding inflater class adapter
 *
 * @author shenghaiyang
 */
object InflaterClassAdapter {

    @JvmStatic
    fun visit(classWriter: ClassWriter, bindingName: String, inflaterName: String): ByteArray {

        classWriter.visit(Opcodes.V1_7, Opcodes.ACC_PUBLIC or Opcodes.ACC_SUPER,
                inflaterName,
                null, NAME_OBJECT, arrayOf(NAME_VIEW_BINDING_INFLATER))

        visitConstructor(classWriter, inflaterName)
        visitInflate(classWriter, bindingName, inflaterName)

        classWriter.visitEnd()
        return classWriter.toByteArray()
    }

    private fun visitInflate(classWriter: ClassWriter, name: String, inflaterName: String) {

        val methodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, INFLATER_METHOD_NAME,
                "(L$NAME_LAYOUT_INFLATER;L$NAME_VIEW_GROUP;Z)L$NAME_VIEW_BINDING;", null, null)

        methodVisitor.visitAnnotableParameterCount(3, false)

        val annotationVisitor0 = methodVisitor.visitParameterAnnotation(0, "L$NAME_NON_NULL;", false)
        annotationVisitor0.visitEnd()
        val annotationVisitor1 = methodVisitor.visitParameterAnnotation(1, "L$NAME_NULLABLE;", false)
        annotationVisitor1.visitEnd()

        methodVisitor.visitCode()
        val label0 = Label()
        methodVisitor.visitLabel(label0)
        methodVisitor.visitLineNumber(20, label0)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 1)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 2)
        methodVisitor.visitVarInsn(Opcodes.ILOAD, 3)
        methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, name, VIEW_BINDING_INFLATE_METHOD_NAME,
                "(L$NAME_LAYOUT_INFLATER;L$NAME_VIEW_GROUP;Z)L$name;", false)
        methodVisitor.visitInsn(Opcodes.ARETURN)
        val label1 = Label()
        methodVisitor.visitLabel(label1)
        methodVisitor.visitLocalVariable("this", "L$inflaterName;", null, label0, label1, 0)
        methodVisitor.visitLocalVariable("inflater", "L$NAME_LAYOUT_INFLATER;", null, label0, label1, 1)
        methodVisitor.visitLocalVariable("root", "L$NAME_VIEW_GROUP;", null, label0, label1, 2)
        methodVisitor.visitLocalVariable("attachToRoot", "Z", null, label0, label1, 3)
        methodVisitor.visitMaxs(3, 4)
        methodVisitor.visitEnd()
    }

    private fun visitConstructor(classWriter: ClassWriter, inflaterName: String) {
        val methodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, NAME_METHOD_INIT, "()V", null, null)
        methodVisitor.visitCode()
        val label0 = Label()
        methodVisitor.visitLabel(label0)
        methodVisitor.visitLineNumber(16, label0)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
        methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, NAME_OBJECT, NAME_METHOD_INIT, "()V", false)
        methodVisitor.visitInsn(Opcodes.RETURN)
        val label1 = Label()
        methodVisitor.visitLabel(label1)
        methodVisitor.visitLocalVariable("this", "L$inflaterName;", null, label0, label1, 0)
        methodVisitor.visitMaxs(1, 1)
        methodVisitor.visitEnd()
    }


}