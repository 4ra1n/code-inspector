package code.inspector.core.asm;

import code.inspector.core.asm.base.BaseClassVisitor;
import code.inspector.model.MethodReference;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.JSRInlinerAdapter;

import java.util.HashMap;
import java.util.Map;

public class RedirectClassVisitor extends BaseClassVisitor {
    private String name;
    private final Map<String, Boolean> pass;
    private final MethodReference methodReference;

    public RedirectClassVisitor(MethodReference methodReference) {
        super(null,0,null);
        this.methodReference = methodReference;
        this.pass = new HashMap<>();
    }

    public Boolean getPass(String key) {
        return pass.getOrDefault(key,false);
    }

    @Override
    public void visit(int version, int access, String name, String signature,
                      String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.name = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor,
                                     String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (methodReference.getName().equals(name)) {
            RedirectMethodAdapter redirectMethodAdapter = new RedirectMethodAdapter(
                    this.pass, Opcodes.ASM6, mv, this.name, access, name, descriptor);
            return new JSRInlinerAdapter(redirectMethodAdapter,
                    access, name, descriptor, signature, exceptions);
        }
        return mv;
    }
}
