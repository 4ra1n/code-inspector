package code.inspector.core.service;

import code.inspector.core.asm.RedirectClassVisitor;
import code.inspector.core.data.RedirectCollector;
import code.inspector.core.spring.SpringController;
import code.inspector.core.spring.SpringMapping;
import code.inspector.model.ClassFile;
import code.inspector.model.MethodReference;
import code.inspector.model.ResultInfo;
import org.objectweb.asm.ClassReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class RedirectService {

    private static final List<ResultInfo> results = new ArrayList<>();

    public static void start(Map<String, ClassFile> classFileByName,
                             List<SpringController> controllers) {

        for (SpringController controller : controllers) {
            for (SpringMapping mapping : controller.getMappings()) {
                MethodReference methodReference = mapping.getMethodReference();
                if (methodReference == null) {
                    continue;
                }
                ClassFile file = classFileByName.get(mapping.getController().getClassReference().getName());
                try {
                    if (file == null) {
                        continue;
                    }
                    ClassReader cr = new ClassReader(file.getFile());
                    RedirectClassVisitor cv = new RedirectClassVisitor(mapping.getMethodReference());
                    cr.accept(cv, ClassReader.EXPAND_FRAMES);
                    RedirectCollector.collect(cv, mapping, results);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static List<ResultInfo> getResults() {
        return new ArrayList<>(results);
    }
}
