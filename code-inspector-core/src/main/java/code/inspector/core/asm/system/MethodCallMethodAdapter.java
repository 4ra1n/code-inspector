package code.inspector.core.asm.system;

import code.inspector.model.ClassReference;
import code.inspector.model.MethodReference;
import org.objectweb.asm.MethodVisitor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class MethodCallMethodAdapter extends MethodVisitor {
    private final Set<MethodReference.Handle> calledMethods;

    public MethodCallMethodAdapter(int api, Map<MethodReference.Handle, Set<MethodReference.Handle>> methodCalls,
                                   final MethodVisitor mv, final String owner, String name, String desc) {
        super(api, mv);
        this.calledMethods = new HashSet<>();
        methodCalls.put(new MethodReference.Handle(new ClassReference.Handle(owner), name, desc), calledMethods);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        calledMethods.add(new MethodReference.Handle(new ClassReference.Handle(owner), name, desc));
        super.visitMethodInsn(opcode, owner, name, desc, itf);
    }
}
