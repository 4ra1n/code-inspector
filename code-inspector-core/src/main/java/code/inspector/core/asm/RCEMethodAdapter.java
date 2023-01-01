package code.inspector.core.asm;

import code.inspector.core.Application;
import code.inspector.core.Const;
import code.inspector.core.Taint;
import code.inspector.core.asm.base.ParamTaintMethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Map;

public class RCEMethodAdapter extends ParamTaintMethodAdapter {
    private final Map<String, Boolean> pass;

    private static int arrayPos = -1;

    public RCEMethodAdapter(int methodArgIndex, Map<String, Boolean> pass, int api, MethodVisitor mv,
                            String owner, int access, String name, String desc) {
        super(methodArgIndex, api, mv, owner, access, name, desc);
        this.pass = pass;
        arrayPos = -1;
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        if (opcode == Opcodes.ASTORE) {
            if (operandStack.get(0).contains(Taint.LIST_INIT)) {
                arrayPos = var;
                super.visitVarInsn(opcode, var);
                localVariables.set(var, Taint.LIST_INIT);
                return;
            }
        }
        super.visitVarInsn(opcode, var);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        boolean buildStrCondition = owner.equals("java/lang/StringBuilder") &&
                name.equals("append") &&
                desc.equals("(Ljava/lang/String;)Ljava/lang/StringBuilder;");

        boolean toStringCondition = owner.equals("java/lang/StringBuilder") &&
                name.equals("toString") &&
                desc.equals("()Ljava/lang/String;");

        boolean runtimeCondition = owner.equals("java/lang/Runtime") && name.equals("exec");
        boolean processInitCondition = owner.equals("java/lang/ProcessBuilder") && name.equals("<init>");
        boolean processStartCondition = owner.equals("java/lang/ProcessBuilder") && name.equals("start") &&
                desc.equals("()Ljava/lang/Process;");
        boolean groovyCondition = owner.equals("groovy/lang/GroovyShell") && name.equals("evaluate") &&
                desc.equals("(Ljava/lang/String;)Ljava/lang/Object;");
        boolean jndiCondition = owner.equals("javax/naming/Context") && name.equals("lookup");

        boolean spELOption = Application.globalOptions.getOrDefault(Const.RCE_SP_EL_TYPE, false);
        boolean spELStandard = spELOption && owner.equals(
                "org/springframework/expression/spel/support/StandardEvaluationContext") &&
                name.equals("<init>");
        boolean spELParse = spELOption && owner.equals("org/springframework/expression/ExpressionParser") &&
                name.equals("parseExpression");
        boolean spELGetValue = spELOption && owner.equals("org/springframework/expression/Expression") &&
                name.equals("getValue");

        boolean listInit = owner.equals("java/util/ArrayList") && name.equals("<init>");
        boolean listAdd = owner.equals("java/util/List") && name.equals("add");

        if (listInit) {
            super.visitMethodInsn(opcode, owner, name, desc, itf);
            operandStack.set(0, Taint.LIST_INIT);
            return;
        }

        if (listAdd) {
            if (operandStack.get(0).contains(Taint.PARAM_TAINT) ||
                    operandStack.get(0).contains(Taint.TO_STRING)) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                if (arrayPos != -1) {
                    localVariables.set(arrayPos, Taint.PARAM_TAINT);
                }
                return;
            }
        }

        if (spELStandard) {
            super.visitMethodInsn(opcode, owner, name, desc, itf);
            operandStack.set(0, Taint.SPRING_STANDARD);
            return;
        }

        if (spELParse) {
            if (operandStack.get(0).contains(Taint.PARAM_TAINT)) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                operandStack.set(0, Taint.PARAM_TAINT);
                return;
            }
        }

        if (spELGetValue) {
            if (operandStack.get(0).contains(Taint.SPRING_STANDARD)) {
                if (operandStack.size() > 1 &&
                        operandStack.get(1).contains(Taint.PARAM_TAINT)) {
                    pass.put(Const.RCE_SP_EL_TYPE, true);
                }
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                return;
            }
        }

        if (buildStrCondition) {
            if (operandStack.get(0).contains(Taint.PARAM_TAINT) ||
                    operandStack.get(1).contains(Taint.PARAM_TAINT) ||
                    operandStack.get(0).contains(Taint.BUILD_STRING) ||
                    operandStack.get(1).contains(Taint.BUILD_STRING)) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                operandStack.set(0, Taint.BUILD_STRING);
                return;
            }
        }

        if (toStringCondition) {
            if (operandStack.get(0).contains(Taint.BUILD_STRING)) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                operandStack.set(0, Taint.TO_STRING);
                return;
            }
        }

        if (Application.globalOptions.getOrDefault(
                Const.RCE_PROCESS_TYPE, false) && processInitCondition) {
            if (operandStack.get(0).contains(Taint.PARAM_TAINT)) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                operandStack.set(0, Taint.PROCESS_INIT);
                return;
            }
        }

        if (Application.globalOptions.getOrDefault(
                Const.RCE_RUNTIME_TYPE, false) && runtimeCondition) {
            if (operandStack.get(0).contains(Taint.PARAM_TAINT) ||
                    operandStack.get(0).contains(Taint.TO_STRING)) {
                pass.put(Const.RCE_RUNTIME_TYPE, true);
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                return;
            }
        }

        if (Application.globalOptions.getOrDefault(
                Const.RCE_JNDI_TYPE, false) && jndiCondition) {
            if (operandStack.get(0).contains(Taint.PARAM_TAINT) ||
                    operandStack.get(0).contains(Taint.TO_STRING)) {
                pass.put(Const.RCE_JNDI_TYPE, true);
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                return;
            }
        }

        if (Application.globalOptions.getOrDefault(
                Const.RCE_PROCESS_TYPE, false) && processStartCondition) {
            if (operandStack.get(0).contains(Taint.PROCESS_INIT)) {
                pass.put(Const.RCE_PROCESS_TYPE, true);
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                return;
            }
        }

        if (Application.globalOptions.getOrDefault(
                Const.RCE_GROOVY_TYPE, false) && groovyCondition) {
            if (operandStack.get(0).contains(Taint.PARAM_TAINT) ||
                    operandStack.get(0).contains(Taint.TO_STRING)) {
                pass.put(Const.RCE_GROOVY_TYPE, true);
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                return;
            }
        }

        super.visitMethodInsn(opcode, owner, name, desc, itf);
    }
}
