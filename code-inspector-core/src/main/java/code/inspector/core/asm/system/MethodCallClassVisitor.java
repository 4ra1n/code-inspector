package code.inspector.core.asm.system;

import code.inspector.model.MethodReference;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.JSRInlinerAdapter;

import java.util.Map;
import java.util.Set;


public class MethodCallClassVisitor extends ClassVisitor {
    private String name = null;
    private final Map<MethodReference.Handle, Set<MethodReference.Handle>> methodCalls;

    public MethodCallClassVisitor(Map<MethodReference.Handle, Set<MethodReference.Handle>> methodCalls) {
        super(Opcodes.ASM6);
        this.methodCalls = methodCalls;
    }

    @Override
    public void visit(int version, int access, String name, String signature,
                      String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        if (this.name != null) {
            throw new IllegalStateException("ClassVisitor already visited a class!");
        }
        this.name = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc,
                                     String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        MethodCallMethodAdapter adapter = new MethodCallMethodAdapter(
                api, this.methodCalls, mv, this.name, name, desc);
        return new JSRInlinerAdapter(adapter, access, name, desc, signature, exceptions);
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }
}
