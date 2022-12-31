package code.inspector.core.asm;


import code.inspector.core.asm.base.BaseClassVisitor;
import code.inspector.model.MethodReference;

public class DesClassVisitor extends BaseClassVisitor {
    public DesClassVisitor(MethodReference.Handle targetMethod, int targetIndex) {
        super(targetMethod, targetIndex, DesMethodAdapter.class);
    }
}
