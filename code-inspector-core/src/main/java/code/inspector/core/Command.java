package code.inspector.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Command {
    public List<String> jars;
    public String module;
    public boolean isDebug;
    public boolean jdk;
    public boolean springBoot;
    public boolean lib;
    public String path;
    public String packageName;
    private HashMap<String,Boolean> options = new HashMap<>();

    public void setOptions(HashMap<String, Boolean> options) {
        this.options = options;
    }

    public HashMap<String,Boolean>  getOptions(){
        return this.options;
    }
    public Command(){
        this.jars = new ArrayList<>();
        this.module = "";
        this.isDebug = false;
        this.jdk = false;
        this.springBoot = true;
        this.lib = false;
        this.path = "result.html";
        this.packageName = "org.sec";
    }
}
