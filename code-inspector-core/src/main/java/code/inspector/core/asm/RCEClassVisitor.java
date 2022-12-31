package code.inspector.core.asm;


import code.inspector.core.asm.base.BaseClassVisitor;
import code.inspector.model.MethodReference;

public class RCEClassVisitor extends BaseClassVisitor {
    public RCEClassVisitor(MethodReference.Handle targetMethod, int targetIndex) {
        super(targetMethod, targetIndex, RCEMethodAdapter.class);
    }
}
