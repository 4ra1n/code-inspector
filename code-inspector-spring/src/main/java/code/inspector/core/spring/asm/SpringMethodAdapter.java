package code.inspector.core.spring.asm;

import code.inspector.core.spring.SpringConstant;
import code.inspector.core.spring.SpringController;
import code.inspector.core.spring.SpringMapping;
import code.inspector.core.spring.SpringParam;
import code.inspector.model.ClassReference;
import code.inspector.model.MethodReference;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class SpringMethodAdapter extends MethodVisitor {
    private final Map<MethodReference.Handle, MethodReference> methodMap;
    private final List<SpringParam> requestParam = new ArrayList<>();
    private final SpringMapping currentMapping;
    private final SpringController controller;
    private final String name;
    private final String owner;
    private final String desc;

    private SpringPathAnnoAdapter pathAnnoAdapter;

    public SpringMethodAdapter(String name, String descriptor, String owner,
                               int api, MethodVisitor methodVisitor,
                               SpringController currentController,
                               Map<MethodReference.Handle, MethodReference> methodMap) {
        super(api, methodVisitor);
        this.owner = owner;
        this.desc = descriptor;
        this.name = name;
        this.methodMap = methodMap;
        this.controller = currentController;
        this.currentMapping = new SpringMapping();
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        AnnotationVisitor av = super.visitAnnotation(descriptor, visible);
        if (descriptor.equals(SpringConstant.RequestMappingAnno) ||
                descriptor.equals(SpringConstant.GetMappingAnno) ||
                descriptor.equals(SpringConstant.PostMappingAnno)) {
            currentMapping.setMethodName(new MethodReference.Handle(
                    new ClassReference.Handle(this.owner), this.name, this.desc));
            currentMapping.setController(controller);
            currentMapping.setMethodReference(methodMap.get(currentMapping.getMethodName()));
            currentMapping.setParamMap(this.requestParam);
            pathAnnoAdapter = new SpringPathAnnoAdapter(Opcodes.ASM6, av);
            av = pathAnnoAdapter;
        }
        if (descriptor.equals(SpringConstant.ResponseBodyAnno)) {
            currentMapping.setRest(true);
        }
        return av;
    }

    @Override
    public void visitCode() {
        if (this.currentMapping != null) {
            if (pathAnnoAdapter.getResults().size() > 0) {
                currentMapping.setPath(pathAnnoAdapter.getResults().get(0));
            }
        }
        Type[] argTypes = Type.getArgumentTypes(this.desc);
        for (int i = 0; i < argTypes.length; i++) {
            if (i < this.requestParam.size()) {
                this.requestParam.get(i).setParamType(argTypes[i].getClassName());
                this.requestParam.get(i).setParamIndex(i);
            }
        }
        super.visitCode();
    }

    @Override
    public AnnotationVisitor visitParameterAnnotation(int parameter, String descriptor, boolean visible) {
        AnnotationVisitor av = super.visitParameterAnnotation(parameter, descriptor, visible);
        if (descriptor.equals(SpringConstant.RequestParamAnno)) {
            return new SpringAnnoAdapter(Opcodes.ASM6, av, requestParam, parameter);
        }
        return av;
    }


    @Override
    public void visitParameter(String name, int access) {
        SpringParam param = new SpringParam();
        param.setParamName(name);
        this.requestParam.add(param);
        super.visitParameter(name, access);
    }


    @Override
    public void visitEnd() {
        if (currentMapping != null) {
            this.requestParam.forEach(param -> {
                if (param.getReqName() == null || param.getReqName().equals("")) {
                    param.setReqName(param.getParamName());
                }
            });
            controller.addMapping(currentMapping);
        }
        super.visitEnd();
    }
}
