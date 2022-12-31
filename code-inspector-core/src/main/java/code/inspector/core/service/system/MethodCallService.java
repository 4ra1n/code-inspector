package code.inspector.core.service.system;

import code.inspector.core.asm.system.MethodCallClassVisitor;
import code.inspector.model.ClassFile;
import code.inspector.model.MethodReference;
import org.objectweb.asm.ClassReader;

import java.util.List;
import java.util.Map;
import java.util.Set;


public class MethodCallService {

    public static void start(List<ClassFile> classFileList,
                             Map<MethodReference.Handle, Set<MethodReference.Handle>> methodCalls) {
        for (ClassFile file : classFileList) {
            try {
                MethodCallClassVisitor mcv = new MethodCallClassVisitor(methodCalls);
                ClassReader cr = new ClassReader(file.getFile());
                cr.accept(mcv, ClassReader.EXPAND_FRAMES);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
