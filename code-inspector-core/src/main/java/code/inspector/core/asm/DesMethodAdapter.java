package code.inspector.core.asm;

import code.inspector.core.Application;
import code.inspector.core.Const;
import code.inspector.core.Taint;
import code.inspector.core.asm.base.ParamTaintMethodAdapter;
import org.objectweb.asm.MethodVisitor;

import java.util.Map;

public class DesMethodAdapter extends ParamTaintMethodAdapter {
    private final Map<String, Boolean> pass;
    private static boolean jacksonEnable = false;

    private final boolean jdkOption;
    private final boolean fjOption;
    private final boolean yamlOption;
    private final boolean jacksonOption;
    private final boolean hessianOption;
    private final boolean xmlOption;

    public DesMethodAdapter(int methodArgIndex, Map<String, Boolean> pass, int api, MethodVisitor mv,
                            String owner, int access, String name, String desc) {
        super(methodArgIndex, api, mv, owner, access, name, desc);
        this.pass = pass;
        jacksonEnable = false;
        this.jdkOption = Application.globalOptions.getOrDefault(Const.DESERIALIZATION_JDK, false);
        this.fjOption = Application.globalOptions.getOrDefault(Const.DESERIALIZATION_FASTJSON, false);
        this.yamlOption = Application.globalOptions.getOrDefault(Const.DESERIALIZATION_SNAKEYAML, false);
        this.jacksonOption = Application.globalOptions.getOrDefault(Const.DESERIALIZATION_JACKSON, false);
        this.hessianOption = Application.globalOptions.getOrDefault(Const.DESERIALIZATION_HESSIAN, false);
        this.xmlOption = Application.globalOptions.getOrDefault(Const.DESERIALIZATION_XML_DECODER, false);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        boolean baInputStreamInit = owner.equals("java/io/ByteArrayInputStream") && name.equals("<init>");

        boolean jdkInitCondition = jdkOption && owner.equals("java/io/ObjectInputStream")
                && name.equals("<init>");
        boolean jdkReadObjCondition = jdkOption && owner.equals("java/io/ObjectInputStream")
                && (name.equals("readObject") || name.equals("readUnshared"));

        boolean fjParseCondition = fjOption && owner.equals("com/alibaba/fastjson/JSON") && name.equals("parse");
        boolean fjPACondition = fjOption && owner.equals("com/alibaba/fastjson/JSON") && name.equals("parseArray");
        boolean fjPOCondition = fjOption && owner.equals("com/alibaba/fastjson/JSON") && name.equals("parseObject");

        boolean yamlInitCondition = yamlOption && owner.equals("org/yaml/snakeyaml/Yaml") &&
                name.equals("<init>") && desc.equals("()V");
        boolean yamlLoadCondition = yamlOption && owner.equals("org/yaml/snakeyaml/Yaml") && name.equals("load");

        boolean jacksonEnableCondition = jacksonOption &&
                owner.equals("com/fasterxml/jackson/databind/ObjectMapper") &&
                name.equals("enableDefaultTyping");
        boolean jacksonReadCondition = jacksonOption &&
                owner.equals("com/fasterxml/jackson/databind/ObjectMapper") &&
                name.equals("readValue");

        boolean hessianInitCondition = hessianOption &&
                owner.equals("com/alibaba/com/caucho/hessian/io/Hessian2Input") &&
                name.equals("<init>");
        boolean hessianReadObjectCondition = hessianOption &&
                owner.equals("com/alibaba/com/caucho/hessian/io/Hessian2Input") &&
                name.equals("readObject");

        boolean xmlDecodeInit = xmlOption &&
                owner.equals("java/beans/XMLDecoder") && name.equals("<init>");
        boolean xmlDecodeRead = xmlOption &&
                owner.equals("java/beans/XMLDecoder") && name.equals("readObject");
        if (baInputStreamInit) {
            if (operandStack.get(0).contains(Taint.PARAM_TAINT)) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                operandStack.set(0, Taint.PARAM_TAINT);
                return;
            }
        }

        if (jdkInitCondition) {
            if (operandStack.get(0).contains(Taint.PARAM_TAINT)) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                operandStack.set(0, Taint.PARAM_TAINT);
                return;
            }
        }

        if (jdkReadObjCondition) {
            if (operandStack.get(0).contains(Taint.PARAM_TAINT)) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                pass.put(Const.DESERIALIZATION_JDK, true);
                return;
            }
        }

        if (fjPACondition || fjPOCondition || fjParseCondition) {
            if (operandStack.get(0).contains(Taint.PARAM_TAINT)) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                pass.put(Const.DESERIALIZATION_FASTJSON, true);
                return;
            }
        }

        if (fjPOCondition) {
            if (operandStack.size() > 1 && operandStack.get(1).contains(Taint.PARAM_TAINT)) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                pass.put(Const.DESERIALIZATION_FASTJSON, true);
                return;
            }
        }

        if (yamlInitCondition) {
            super.visitMethodInsn(opcode, owner, name, desc, itf);
            operandStack.set(0, Taint.YAML_INIT);
            return;
        }

        if (yamlLoadCondition) {
            if (operandStack.get(0).contains(Taint.PARAM_TAINT)) {
                if (operandStack.size() > 1 && operandStack.get(1).contains(Taint.YAML_INIT)) {
                    super.visitMethodInsn(opcode, owner, name, desc, itf);
                    pass.put(Const.DESERIALIZATION_SNAKEYAML, true);
                    return;
                }
            }
        }

        if (jacksonEnableCondition) {
            jacksonEnable = true;
        }

        if (jacksonReadCondition && jacksonEnable) {
            if (operandStack.get(1).contains(Taint.PARAM_TAINT)) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                pass.put(Const.DESERIALIZATION_JACKSON, true);
                return;
            }
        }

        if (hessianInitCondition) {
            if (operandStack.get(0).contains(Taint.PARAM_TAINT)) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                operandStack.set(0, Taint.PARAM_TAINT);
                return;
            }
        }

        if (hessianReadObjectCondition) {
            if (operandStack.get(0).contains(Taint.PARAM_TAINT)) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                pass.put(Const.DESERIALIZATION_HESSIAN, true);
                return;
            }
        }

        if (xmlDecodeInit) {
            if (operandStack.get(0).contains(Taint.PARAM_TAINT)) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                operandStack.set(0, Taint.PARAM_TAINT);
                return;
            }
        }

        if (xmlDecodeRead) {
            if (operandStack.get(0).contains(Taint.PARAM_TAINT)) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                pass.put(Const.DESERIALIZATION_XML_DECODER, true);
                return;
            }
        }

        super.visitMethodInsn(opcode, owner, name, desc, itf);
    }
}