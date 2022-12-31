package code.inspector.core.asm.base;

import code.inspector.core.Taint;
import code.inspector.jvm.CoreMethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class ParamTaintMethodAdapter extends CoreMethodAdapter<String> {
    private final int access;
    private final String desc;
    private final int methodArgIndex;

    public ParamTaintMethodAdapter(int methodArgIndex, int api, MethodVisitor mv,
                                   String owner, int access, String name, String desc) {
        super(api, mv, owner, access, name, desc);
        this.access = access;
        this.desc = desc;
        this.methodArgIndex = methodArgIndex;
    }

    @Override
    public void visitCode() {
        super.visitCode();
        int localIndex = 0;
        int argIndex = 0;
        if ((this.access & Opcodes.ACC_STATIC) == 0) {
            localIndex += 1;
            argIndex += 1;
        }
        for (Type argType : Type.getArgumentTypes(desc)) {
            if (this.methodArgIndex == 100) {
                localVariables.set(localIndex, Taint.PARAM_TAINT);
            }
            if (argIndex == this.methodArgIndex) {
                localVariables.set(localIndex, Taint.PARAM_TAINT);
            }
            localIndex += argType.getSize();
            argIndex += 1;
        }
    }

    @Override
    public void visitInsn(int opcode) {
        if (opcode == Opcodes.AASTORE) {
            if (operandStack.get(0).contains(Taint.PARAM_TAINT) ||
                    operandStack.get(0).contains(Taint.TO_STRING)) {
                super.visitInsn(opcode);
                operandStack.set(0, Taint.PARAM_TAINT);
                return;
            }
        }
        super.visitInsn(opcode);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        boolean base64 = owner.equals("java/util/Base64$Decoder") && name.equals("decode");
        boolean replace = owner.equals("java/lang/String") && name.equals("replace");
        boolean fileInit = owner.equals("java/io/File") && name.equals("<init>");
        boolean fis = owner.equals("java/io/FileInputStream") && name.equals("<init>");
        boolean bufferedIs = owner.equals("java/io/BufferedInputStream") && name.equals("<init>");
        boolean stringFormat = owner.equals("java/lang/String") && name.equals("format");

        if (replace) {
            if (operandStack.get(2).contains(Taint.PARAM_TAINT)) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                operandStack.set(0, Taint.PARAM_TAINT);
                return;
            }
        }

        if (base64) {
            if (operandStack.get(0).contains(Taint.PARAM_TAINT)) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                operandStack.set(0, Taint.PARAM_TAINT);
                return;
            }
        }

        if (fileInit) {
            if (operandStack.get(0).contains(Taint.PARAM_TAINT)) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                operandStack.set(0, Taint.PARAM_TAINT);
                return;
            }
        }

        if (fis) {
            if (operandStack.get(0).contains(Taint.PARAM_TAINT)) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                operandStack.set(0, Taint.PARAM_TAINT);
                return;
            }
        }

        if (bufferedIs) {
            if (operandStack.get(0).contains(Taint.PARAM_TAINT)) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                operandStack.set(0, Taint.PARAM_TAINT);
                return;
            }
        }

        if(stringFormat){
            if (operandStack.get(0).contains(Taint.PARAM_TAINT)) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                operandStack.set(0, Taint.PARAM_TAINT);
                return;
            }
        }

        if (operandStack.size() > 0 &&
                operandStack.get(0).contains(Taint.PARAM_TAINT) &&
                opcode != Opcodes.INVOKESTATIC) {
            Type[] argTypes = Type.getArgumentTypes(desc);
            Type[] extendedArgTypes = new Type[argTypes.length + 1];
            System.arraycopy(argTypes, 0, extendedArgTypes, 1, argTypes.length);
            extendedArgTypes[0] = Type.getObjectType(owner);
            argTypes = extendedArgTypes;
            super.visitMethodInsn(opcode, owner, name, desc, itf);
            if (operandStack.size() > 0) {
                // 非静态方法且以get开头只有一个参数
                // 当栈顶为污点时可以确定是getter方法
                // getter方法应该进行污点传递
                if (argTypes.length == 1 && name.startsWith("get")) {
                    operandStack.set(0, Taint.PARAM_TAINT);
                }
            }
            return;
        }
        super.visitMethodInsn(opcode, owner, name, desc, itf);
    }
}
