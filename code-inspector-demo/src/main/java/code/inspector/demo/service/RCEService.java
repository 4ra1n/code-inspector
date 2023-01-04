package code.inspector.demo.service;

import code.inspector.demo.model.Obj;

public interface RCEService {
    String rce1(String data);
    String rce2(String data);
    String rce3(String data);
    String rce4(Obj obj);
    String rce5(String data);
    String rce6(String data);
}
