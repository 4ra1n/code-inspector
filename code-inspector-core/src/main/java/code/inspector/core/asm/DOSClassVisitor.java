package code.inspector.core.asm;


import code.inspector.core.asm.base.BaseClassVisitor;
import code.inspector.model.MethodReference;

public class DOSClassVisitor extends BaseClassVisitor {
    public DOSClassVisitor(MethodReference.Handle targetMethod, int targetIndex) {
        super(targetMethod, targetIndex, DOSMethodAdapter.class);
    }
}
