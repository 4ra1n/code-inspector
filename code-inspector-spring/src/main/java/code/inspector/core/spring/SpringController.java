package code.inspector.core.spring;


import code.inspector.model.ClassReference;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class SpringController {
    private boolean isRest;
    private ClassReference.Handle className;
    private ClassReference classReference;
    private final List<SpringMapping> mappings = new ArrayList<>();

    public boolean isRest() {
        return isRest;
    }

    public void setRest(boolean rest) {
        isRest = rest;
    }

    public ClassReference.Handle getClassName() {
        return className;
    }

    public void setClassName(ClassReference.Handle className) {
        this.className = className;
    }

    public ClassReference getClassReference() {
        return classReference;
    }

    public void setClassReference(ClassReference classReference) {
        this.classReference = classReference;
    }

    public List<SpringMapping> getMappings() {
        return mappings;
    }

    public void addMapping(SpringMapping mapping) {
        this.mappings.add(mapping);
    }
}
