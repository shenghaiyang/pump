package com.shenghaiyang.pump.gradle

import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter
import org.slf4j.LoggerFactory

/**
 * Inflater store constructor visitor
 *
 * @author shenghaiyang
 */
class StoreConstructorVisitor(
        methodVisitor: MethodVisitor?,
        access: Int,
        name: String?,
        descriptor: String?,
        private val mMapping: Map<String, String>
) : AdviceAdapter(Opcodes.ASM5, methodVisitor, access, name, descriptor) {

    private val mLogger = LoggerFactory.getLogger(StoreConstructorVisitor::class.java)

    /** count the method last line number */
    private var mLastLineNum = 0

    override fun visitLineNumber(line: Int, start: Label?) {
        super.visitLineNumber(line, start)
        mLastLineNum = line
    }

    override fun onMethodExit(opcode: Int) {
        // insert code at the end of constructor
        var lineNum = mLastLineNum + 1
        mMapping.forEach { (bindingName, inflaterName) ->
            mLogger.debug("binding:$bindingName, inflater:$inflaterName")

            lineNum++
            val label = Label()
            mv.visitLabel(label)
            mv.visitLineNumber(lineNum, label)
            mv.visitVarInsn(Opcodes.ALOAD, 0)
            mv.visitFieldInsn(Opcodes.GETFIELD, NAME_STORE, "mInflaters", "L$NAME_MAP;")
            mv.visitLdcInsn(Type.getType("L$bindingName;"))
            mv.visitTypeInsn(Opcodes.NEW, inflaterName)
            mv.visitInsn(Opcodes.DUP)
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, inflaterName, NAME_METHOD_INIT, "()V", false)
            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, NAME_MAP, "put",
                    "(L$NAME_OBJECT;L$NAME_OBJECT;)L$NAME_OBJECT;", true)
            mv.visitInsn(Opcodes.POP)
        }

        super.onMethodExit(opcode)
    }

}