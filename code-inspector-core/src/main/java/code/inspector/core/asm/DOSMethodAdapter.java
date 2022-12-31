package code.inspector.core.asm;

import code.inspector.core.Application;
import code.inspector.core.Const;
import code.inspector.core.Taint;
import code.inspector.core.asm.base.ParamTaintMethodAdapter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DOSMethodAdapter extends ParamTaintMethodAdapter {
    private final Map<String, Boolean> pass;
    private static List<Boolean> flag = new ArrayList<>();
    private final List<Label> labelList = new ArrayList<>();
    private boolean forFlag;

    public DOSMethodAdapter(int methodArgIndex, Map<String, Boolean> pass, int api, MethodVisitor mv,
                            String owner, int access, String name, String desc) {
        super(methodArgIndex, api, mv, owner, access, name, desc);
        this.pass = pass;
    }

    @Override
    public void visitLabel(Label label) {
        this.labelList.add(label);
        super.visitLabel(label);
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        if (opcode == Opcodes.GOTO) {
            if (labelList.contains(label)) {
                if (Application.globalOptions.getOrDefault(
                        Const.DOS_FOR_TYPE,false) && this.forFlag) {
                    pass.put(Const.DOS_FOR_TYPE, true);
                    this.forFlag = false;
                }
            }
        }
        if (opcode == Opcodes.IF_ICMPGE) {
            if (operandStack.get(0).contains(Taint.PARAM_TAINT)) {
                this.forFlag = true;
            }
        }
        super.visitJumpInsn(opcode, label);
    }

    @Override
    public void visitIntInsn(int opcode, int operand) {
        if (opcode == Opcodes.NEWARRAY) {
            if (Application.globalOptions.getOrDefault(Const.DOS_ARRAY_TYPE,false) &&
                    operandStack.get(0).contains(Taint.PARAM_TAINT)) {
                pass.put(Const.DOS_ARRAY_TYPE, true);
            }
        }
        super.visitIntInsn(opcode, operand);
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        if (opcode == Opcodes.ANEWARRAY) {
            if (Application.globalOptions.getOrDefault(Const.DOS_ARRAY_TYPE,false) &&
                    operandStack.get(0).contains(Taint.PARAM_TAINT)) {
                pass.put(Const.DOS_ARRAY_TYPE, true);
            }
        }
        super.visitTypeInsn(opcode, type);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        boolean patternMatches = (opcode == Opcodes.INVOKESTATIC) &&
                (owner.equals("java/util/regex/Pattern")) &&
                (name.equals("matches")) &&
                (desc.equals("(Ljava/lang/String;Ljava/lang/CharSequence;)Z"));

        if (Application.globalOptions.getOrDefault(
                Const.DOS_REGEX_TYPE,false) && patternMatches) {
            if (operandStack.get(0).contains(Taint.PARAM_TAINT)) {
                flag.add(true);
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                return;
            }
            if (operandStack.get(1).contains(Taint.PARAM_TAINT)) {
                flag.add(true);
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                return;
            }
        }

        if (Application.globalOptions.getOrDefault(
                Const.DOS_REGEX_TYPE,false)
                && flag.size() == 2 && !flag.contains(false)) {
            pass.put(Const.DOS_REGEX_TYPE, true);
            flag = new ArrayList<>();
            super.visitMethodInsn(opcode, owner, name, desc, itf);
            return;
        }

        boolean listInit = (opcode == Opcodes.INVOKESPECIAL) &&
                (owner.equals("java/util/ArrayList")) &&
                (name.equals("<init>")) &&
                (desc.equals("(I)V"));
        if (Application.globalOptions.getOrDefault(
                Const.DOS_LIST_TYPE,false) && listInit) {
            if (operandStack.get(0).contains(Taint.PARAM_TAINT)) {
                pass.put(Const.DOS_LIST_TYPE, true);
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                return;
            }
        }

        super.visitMethodInsn(opcode, owner, name, desc, itf);
    }
}
