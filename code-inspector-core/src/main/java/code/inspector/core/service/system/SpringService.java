package code.inspector.core.service.system;

import code.inspector.core.spring.SpringController;
import code.inspector.core.spring.asm.SpringClassVisitor;
import code.inspector.model.ClassFile;
import code.inspector.model.ClassReference;
import code.inspector.model.MethodReference;
import org.objectweb.asm.ClassReader;

import java.util.List;
import java.util.Map;

public class SpringService {
    public static void start(List<ClassFile> classFileList,
                             List<SpringController> controllers,
                             Map<ClassReference.Handle, ClassReference> classMap,
                             Map<MethodReference.Handle, MethodReference> methodMap) {
        for (ClassFile file : classFileList) {
            try {
                SpringClassVisitor mcv = new SpringClassVisitor(controllers, classMap, methodMap);
                ClassReader cr = new ClassReader(file.getFile());
                cr.accept(mcv, ClassReader.EXPAND_FRAMES);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}