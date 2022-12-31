package code.inspector.core.service;

import code.inspector.core.asm.DesClassVisitor;
import code.inspector.core.data.DesCollector;
import code.inspector.core.service.base.BaseService;
import code.inspector.core.spring.SpringController;
import code.inspector.model.CallGraph;
import code.inspector.model.ClassFile;
import code.inspector.model.MethodReference;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class DesService extends BaseService {
    public static void start(Map<String, ClassFile> classFileByName,
                             List<SpringController> controllers,
                             Map<MethodReference.Handle, Set<CallGraph>> discoveredCalls) {
        start0(classFileByName,controllers,discoveredCalls,
                DesClassVisitor.class, DesCollector.class);

    }
}
