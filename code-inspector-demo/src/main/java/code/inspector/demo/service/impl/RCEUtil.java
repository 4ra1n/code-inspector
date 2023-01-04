package code.inspector.demo.service.impl;

import java.io.IOException;

public class RCEUtil {
    public static Process  exec(String cmd){
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
