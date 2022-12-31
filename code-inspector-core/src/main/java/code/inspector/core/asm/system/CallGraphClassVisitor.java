package code.inspector.core.asm.system;

import code.inspector.model.CallGraph;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.JSRInlinerAdapter;

import java.util.Set;


public class CallGraphClassVisitor extends ClassVisitor {
    private final Set<CallGraph> discoveredCalls;
    private String name;

    public CallGraphClassVisitor(Set<CallGraph> discoveredCalls) {
        super(Opcodes.ASM6);
        this.discoveredCalls = discoveredCalls;
    }

    @Override
    public void visit(int version, int access, String name, String signature,
                      String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.name = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc,
                                     String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        CallGraphMethodAdapter callGraphMethodVisitor = new CallGraphMethodAdapter(api, discoveredCalls,
                mv, this.name, access, name, desc);
        return new JSRInlinerAdapter(callGraphMethodVisitor, access, name, desc, signature, exceptions);
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }
}
