package code.inspector.core.asm;

import code.inspector.core.asm.base.BaseClassVisitor;
import code.inspector.model.MethodReference;

public class SqlInjectClassVisitor extends BaseClassVisitor {
    public SqlInjectClassVisitor(MethodReference.Handle targetMethod, int targetIndex) {
        super(targetMethod, targetIndex, SqlInjectMethodAdapter.class);
    }
}
