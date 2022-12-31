package code.inspector.core.service.system;


import code.inspector.core.inherit.InheritanceMap;
import code.inspector.core.inherit.InheritanceUtil;
import code.inspector.model.ClassReference;

import java.util.Map;

public class InheritanceService {

    public static InheritanceMap start(Map<ClassReference.Handle, ClassReference> classMap) {
        return InheritanceUtil.derive(classMap);
    }
}
