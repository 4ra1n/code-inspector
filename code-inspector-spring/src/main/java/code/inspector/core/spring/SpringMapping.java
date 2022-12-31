package code.inspector.core.spring;


import code.inspector.model.MethodReference;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class SpringMapping {
    private boolean isRest;
    private SpringController controller;
    private MethodReference.Handle methodName;
    private MethodReference methodReference;
    private String path;
    private List<SpringParam> paramMap = new ArrayList<>();

    public List<SpringParam> getParamMap() {
        return paramMap;
    }

    public void setParamMap(List<SpringParam> paramMap) {
        this.paramMap = paramMap;
    }

    public boolean isRest() {
        return isRest;
    }

    public void setRest(boolean rest) {
        isRest = rest;
    }

    public SpringController getController() {
        return controller;
    }

    public void setController(SpringController controller) {
        this.controller = controller;
    }

    public MethodReference.Handle getMethodName() {
        return methodName;
    }

    public void setMethodName(MethodReference.Handle methodName) {
        this.methodName = methodName;
    }

    public MethodReference getMethodReference() {
        return methodReference;
    }

    public void setMethodReference(MethodReference methodReference) {
        this.methodReference = methodReference;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
