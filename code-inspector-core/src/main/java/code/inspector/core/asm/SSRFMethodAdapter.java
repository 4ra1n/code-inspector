package code.inspector.core.asm;

import code.inspector.core.Application;
import code.inspector.core.Const;
import code.inspector.core.Taint;
import code.inspector.core.asm.base.ParamTaintMethodAdapter;
import org.objectweb.asm.MethodVisitor;

import java.util.Map;

public class SSRFMethodAdapter extends ParamTaintMethodAdapter {
    private final Map<String, Boolean> pass;

    private final boolean jdkOption;
    private final boolean apacheOption;
    private final boolean socketOption;
    private final boolean okhttpOption;

    public SSRFMethodAdapter(int methodArgIndex, Map<String, Boolean> pass, int api, MethodVisitor mv,
                             String owner, int access, String name, String desc) {
        super(methodArgIndex, api, mv, owner, access, name, desc);
        this.pass = pass;
        this.jdkOption = Application.globalOptions.getOrDefault(Const.SSRF_JDK_TYPE, false);
        this.apacheOption = Application.globalOptions.getOrDefault(Const.SSRF_APACHE_TYPE, false);
        this.socketOption = Application.globalOptions.getOrDefault(Const.SSRF_SOCKET_TYPE, false);
        this.okhttpOption = Application.globalOptions.getOrDefault(Const.SSRF_OKHTTP_TYPE, false);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        boolean urlCondition = jdkOption && owner.equals("java/net/URL") && name.equals("<init>") &&
                desc.equals("(Ljava/lang/String;)V");
        boolean urlOpenCondition = jdkOption && owner.equals("java/net/URL") &&
                name.equals("openConnection") &&
                desc.equals("()Ljava/net/URLConnection;");
        boolean urlOpenStream = jdkOption && owner.equals("java/net/URL") && name.equals("openStream");

        boolean apacheHttpInitCondition = apacheOption && owner.equals("org/apache/http/client/methods/HttpGet") &&
                name.equals("<init>") && desc.equals("(Ljava/lang/String;)V");
        boolean apacheHttpExecuteCondition = apacheOption &&
                owner.equals("org/apache/http/impl/client/CloseableHttpClient") &&
                name.equals("execute") && desc.equals("(Lorg/apache/http/client/methods/HttpUriRequest;)" +
                "Lorg/apache/http/client/methods/CloseableHttpResponse;");
        boolean apacheHttpGet = owner.equals("org/apache/http/client/methods/HttpGet") &&
                name.equals("<init>");
        boolean apacheHttpGetExec = owner.equals("org/apache/http/impl/client/DefaultHttpClient") &
                name.equals("execute");

        boolean socketInitCondition = socketOption && owner.equals("java/net/Socket") &&
                name.equals("<init>") && desc.equals("(Ljava/lang/String;I)V");
        boolean socketInputCondition = socketOption && owner.equals("java/net/Socket") &&
                (name.equals("getInputStream") || name.equals("getOutputStream"));

        boolean okhttpUrlCondition = okhttpOption && (owner.equals("okhttp3/Request$Builder") ||
                owner.equals("okhttp/Request$Builder")) &&
                name.equals("url") && (desc.equals("(Ljava/lang/String;)Lokhttp3/Request$Builder;") ||
                desc.equals("(Ljava/lang/String;)Lokhttp/Request$Builder;"));
        boolean okhttpBuildCondition = okhttpOption && (owner.equals("okhttp3/Request$Builder") ||
                owner.equals("okhttp/Request$Builder")) &&
                name.equals("build") && (desc.equals("()Lokhttp3/Request;") ||
                desc.equals("()Lokhttp/Request;"));
        boolean okhttpNewCallCondition = okhttpOption && (owner.equals("okhttp3/OkHttpClient") ||
                owner.equals("okhttp/OkHttpClient")) &&
                name.equals("newCall") && (desc.equals("(Lokhttp3/Request;)Lokhttp3/Call;") ||
                desc.equals("(Lokhttp/Request;)Lokhttp/Call;"));
        boolean okhttpExecuteCondition = okhttpOption && (owner.equals("okhttp3/Call") ||
                owner.equals("okhttp/Call")) && name.equals("execute") &&
                (desc.equals("()Lokhttp3/Response;") || desc.equals("()Lokhttp/Response;"));

        if (urlCondition || apacheHttpInitCondition || apacheHttpGet ||
                okhttpUrlCondition || okhttpBuildCondition || okhttpNewCallCondition) {
            if (operandStack.get(0).contains(Taint.PARAM_TAINT)) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                operandStack.set(0, Taint.PARAM_TAINT);
                return;
            }
        }
        if (urlOpenCondition || urlOpenStream) {
            if (operandStack.get(0).contains(Taint.PARAM_TAINT)) {
                pass.put(Const.SSRF_JDK_TYPE, true);
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                return;
            }
        }
        if (apacheHttpExecuteCondition || apacheHttpGetExec) {
            if (operandStack.get(0).contains(Taint.PARAM_TAINT)) {
                pass.put(Const.SSRF_APACHE_TYPE, true);
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                return;
            }
        }

        if (socketInitCondition) {
            if (operandStack.get(0).contains(Taint.PARAM_TAINT) ||
                    operandStack.get(1).contains(Taint.PARAM_TAINT)) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                operandStack.set(0, Taint.PARAM_TAINT);
                return;
            }
        }
        if (socketInputCondition) {
            if (operandStack.get(0).contains(Taint.PARAM_TAINT)) {
                pass.put(Const.SSRF_SOCKET_TYPE, true);
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                return;
            }
        }
        if (okhttpExecuteCondition) {
            if (operandStack.get(0).contains(Taint.PARAM_TAINT)) {
                pass.put(Const.SSRF_OKHTTP_TYPE, true);
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                return;
            }
        }

        super.visitMethodInsn(opcode, owner, name, desc, itf);
    }
}
