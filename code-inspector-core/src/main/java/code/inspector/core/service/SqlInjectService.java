package code.inspector.core.service;

import code.inspector.core.asm.SqlInjectClassVisitor;
import code.inspector.core.data.SqlCollector;
import code.inspector.core.service.base.BaseService;
import code.inspector.core.spring.SpringController;
import code.inspector.model.CallGraph;
import code.inspector.model.ClassFile;
import code.inspector.model.MethodReference;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SqlInjectService extends BaseService {
    public static void start(Map<String, ClassFile> classFileByName,
                             List<SpringController> controllers,
                             Map<MethodReference.Handle, Set<CallGraph>> discoveredCalls) {
        start0(classFileByName, controllers, discoveredCalls,
                SqlInjectClassVisitor.class, SqlCollector.class);

    }
}