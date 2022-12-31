package code.inspector.demo.service;

import code.inspector.demo.model.Obj;

public interface DesService {
    String jdk(Obj obj);
    String fastjson(Obj obj);
    String yaml(Obj obj);
    String jackson(Obj obj);
    String hessian(Obj obj);
    String xml(Obj obj);
}
