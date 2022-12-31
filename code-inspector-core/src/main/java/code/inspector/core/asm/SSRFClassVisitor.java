package code.inspector.core.asm;


import code.inspector.core.asm.base.BaseClassVisitor;
import code.inspector.model.MethodReference;

public class SSRFClassVisitor extends BaseClassVisitor {
    public SSRFClassVisitor(MethodReference.Handle targetMethod, int targetIndex) {
        super(targetMethod, targetIndex, SSRFMethodAdapter.class);
    }
}
