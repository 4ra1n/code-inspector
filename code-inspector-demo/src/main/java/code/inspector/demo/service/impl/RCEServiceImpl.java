package code.inspector.demo.service.impl;

import code.inspector.demo.model.Obj;
import code.inspector.demo.service.RCEService;
import groovy.lang.GroovyShell;
import org.springframework.stereotype.Service;

import javax.naming.Context;
import javax.naming.InitialContext;

@Service
public class RCEServiceImpl implements RCEService {
    @Override
    public String rce1(String data) {
        try {
            Runtime.getRuntime().exec(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ok";
    }

    @Override
    public String rce2(String data) {
        try {
            new ProcessBuilder(data).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ok";
    }

    @Override
    public String rce3(String data) {
        try {
            GroovyShell shell = new GroovyShell();
            shell.evaluate(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ok";
    }

    @Override
    public String rce4(Obj obj) {
        try {
            Runtime.getRuntime().exec(obj.getCmd());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ok";
    }

    @Override
    public String rce5(String data) {
        try{
            Context context = new InitialContext();
            context.lookup(data);
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return "ok";
    }
}
