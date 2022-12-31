package code.inspector.core.asm.base;

import code.inspector.model.MethodReference;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.JSRInlinerAdapter;

import java.util.HashMap;
import java.util.Map;

public class BaseClassVisitor extends ClassVisitor {
    protected String name;
    private final MethodReference.Handle methodHandle;
    private final int methodArgIndex;
    private final Class<?> targetMethodAdapter;
    private final Map<String, Boolean> pass;

    public BaseClassVisitor(MethodReference.Handle targetMethod,
                            int targetIndex,Class<?> targetMethodAdapter) {
        super(Opcodes.ASM6);
        this.methodHandle = targetMethod;
        this.methodArgIndex = targetIndex;
        this.pass = new HashMap<>();
        this.targetMethodAdapter = targetMethodAdapter;
    }

    public Boolean getPass(String key) {
        return pass.getOrDefault(key,false);
    }

    public int getPassSize(){
        return pass.size();
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
        try {
            if (this.methodHandle!=null && name.equals(this.methodHandle.getName())) {
                MethodVisitor methodVisitor = (MethodVisitor) targetMethodAdapter
                        .getConstructors()[0].newInstance(this.methodArgIndex, this.pass,
                        Opcodes.ASM6, mv, this.name, access, name, descriptor);
                return new JSRInlinerAdapter(methodVisitor,
                        access, name, descriptor, signature, exceptions);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return mv;
    }
}
