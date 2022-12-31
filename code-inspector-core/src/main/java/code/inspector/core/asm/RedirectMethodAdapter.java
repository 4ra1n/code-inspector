package code.inspector.core.asm;

import code.inspector.core.Application;
import code.inspector.core.Const;
import code.inspector.core.Taint;
import code.inspector.jvm.CoreMethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.Map;

public class RedirectMethodAdapter extends CoreMethodAdapter<String> {
    private final int access;
    private final String desc;
    private final Map<String, Boolean> pass;
    private boolean flag;

    public RedirectMethodAdapter(Map<String, Boolean> pass, int api, MethodVisitor mv,
                                 String owner, int access, String name, String desc) {
        super(api, mv, owner, access, name, desc);
        this.access = access;
        this.desc = desc;
        this.pass = pass;
    }

    @Override
    public void visitCode() {
        super.visitCode();
        int localIndex = 0;
        if ((this.access & Opcodes.ACC_STATIC) == 0) {
            localIndex += 1;
        }
        for (Type argType : Type.getArgumentTypes(desc)) {
            localVariables.set(localIndex, Taint.PARAM_TAINT);
            localIndex += argType.getSize();
        }
    }

    @Override
    public void visitInsn(int opcode) {
        if (opcode == Opcodes.ARETURN) {
            if (operandStack.get(0).contains(Taint.PARAM_TAINT) ||
                    operandStack.get(0).contains(Taint.TO_STRING)) {
                if (Application.globalOptions.getOrDefault(
                        Const.REDIRECT_STRING_TYPE,false) && flag) {
                    pass.put(Const.REDIRECT_STRING_TYPE, true);
                }
            }
            if(operandStack.get(0).contains(Taint.MODEL_AND_VIEW)){
                if (Application.globalOptions.getOrDefault(
                        Const.REDIRECT_MODEL_AND_VIEW_TYPE,false) && flag) {
                    pass.put(Const.REDIRECT_MODEL_AND_VIEW_TYPE, true);
                }
            }
        }
        super.visitInsn(opcode);
    }

    @Override
    public void visitLdcInsn(Object cst) {
        if (cst instanceof String && ((String) cst).contains("redirect")) {
            flag = true;
        }
        super.visitLdcInsn(cst);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        boolean getParamCondition = owner.equals("javax/servlet/http/HttpServletRequest") &&
                name.equals("getParameter") && desc.equals("(Ljava/lang/String;)Ljava/lang/String;");

        boolean sendRedirectCondition = owner.equals("javax/servlet/http/HttpServletResponse") &&
                name.equals("sendRedirect") && desc.equals("(Ljava/lang/String;)V");

        boolean buildStrCondition = owner.equals("java/lang/StringBuilder") &&
                name.equals("append") &&
                desc.equals("(Ljava/lang/String;)Ljava/lang/StringBuilder;");

        boolean toStringCondition = owner.equals("java/lang/StringBuilder") &&
                name.equals("toString") &&
                desc.equals("()Ljava/lang/String;");
        boolean modelAndViewCondition = owner.equals("org/springframework/web/servlet/ModelAndView") &&
                name.equals("<init>") && desc.equals("(Ljava/lang/String;)V");

        if (getParamCondition) {
            if (operandStack.get(1).contains(Taint.PARAM_TAINT)) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                operandStack.set(0, Taint.PARAM_TAINT);
                return;
            }
        }

        if (Application.globalOptions.getOrDefault(
                Const.REDIRECT_SEND_RESPONSE_TYPE,false) && sendRedirectCondition) {
            if (operandStack.get(0).contains(Taint.PARAM_TAINT) ||
                    operandStack.get(0).contains(Taint.TO_STRING)) {
                pass.put(Const.REDIRECT_SEND_RESPONSE_TYPE, true);
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
                Const.REDIRECT_MODEL_AND_VIEW_TYPE,false) && modelAndViewCondition) {
            if (operandStack.get(0).contains(Taint.PARAM_TAINT) ||
                    operandStack.get(0).contains(Taint.TO_STRING)) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                operandStack.set(0, Taint.MODEL_AND_VIEW);
                return;
            }
        }

        super.visitMethodInsn(opcode, owner, name, desc, itf);
    }
}
