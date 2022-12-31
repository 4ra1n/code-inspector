package code.inspector.core.service.system;

import code.inspector.core.asm.system.DiscoveryClassVisitor;
import code.inspector.model.ClassFile;
import code.inspector.model.ClassReference;
import code.inspector.model.MethodReference;
import org.objectweb.asm.ClassReader;

import java.util.List;
import java.util.Map;

public class DiscoveryService {

    public static void start(List<ClassFile> classFileList,
                             List<ClassReference> discoveredClasses,
                             List<MethodReference> discoveredMethods,
                             Map<ClassReference.Handle, ClassReference> classMap,
                             Map<MethodReference.Handle, MethodReference> methodMap,
                             Map<String, ClassFile> classFileByName) {
        for (ClassFile file : classFileList) {
            try {
                DiscoveryClassVisitor dcv = new DiscoveryClassVisitor(discoveredClasses, discoveredMethods);
                ClassReader cr = new ClassReader(file.getFile());
                cr.accept(dcv, ClassReader.EXPAND_FRAMES);
                classFileByName.put(dcv.getName(), file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (ClassReference clazz : discoveredClasses) {
            classMap.put(clazz.getHandle(), clazz);
        }
        for (MethodReference method : discoveredMethods) {
            methodMap.put(method.getHandle(), method);
        }
    }
}
