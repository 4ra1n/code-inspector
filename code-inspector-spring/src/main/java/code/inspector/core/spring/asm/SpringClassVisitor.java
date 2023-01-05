package code.inspector.core.spring.asm;

import code.inspector.core.spring.SpringConstant;
import code.inspector.core.spring.SpringController;
import code.inspector.model.ClassReference;
import code.inspector.model.MethodReference;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SpringClassVisitor extends ClassVisitor {
    private final Map<ClassReference.Handle, ClassReference> classMap;
    private final Map<MethodReference.Handle, MethodReference> methodMap;
    private final List<SpringController> controllers;
    private boolean isSpring;
    private SpringController currentController;
    private String name;

    public SpringClassVisitor(List<SpringController> controllers,
                              Map<ClassReference.Handle, ClassReference> classMap,
                              Map<MethodReference.Handle, MethodReference> methodMap) {
        super(Opcodes.ASM6);
        this.methodMap = methodMap;
        this.controllers = controllers;
        this.classMap = classMap;
    }

    @Override
    public void visit(int version, int access, String name, String signature,
                      String superName, String[] interfaces) {
        this.name = name;
        Set<String> annotations = classMap.get(new ClassReference.Handle(name)).getAnnotations();
        if (annotations.contains(SpringConstant.ControllerAnno) ||
                annotations.contains(SpringConstant.RestControllerAnno)) {
            this.isSpring = true;
            currentController = new SpringController();
            currentController.setClassReference(classMap.get(new ClassReference.Handle(name)));
            currentController.setClassName(new ClassReference.Handle(name));
            currentController.setRest(!annotations.contains(SpringConstant.ControllerAnno));
        } else {
            this.isSpring = false;
        }
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor,
                                     String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (this.isSpring) {
            if (this.name.equals("<init>") || this.name.equals("<clinit>")) {
                return mv;
            }
            return new SpringMethodAdapter(name, descriptor, this.name, Opcodes.ASM6, mv,
                    currentController, this.methodMap);
        } else {
            return mv;
        }
    }

    @Override
    public void visitEnd() {
        if (isSpring) {
            controllers.add(currentController);
        }
        super.visitEnd();
    }
}
