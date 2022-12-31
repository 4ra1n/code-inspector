package code.inspector.core.util;

public class ExecUtil {
    public static void exec(String[] cmdArray) {
        try {
            new ProcessBuilder(cmdArray).start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void execOpen(String outputFilePath) {
        if (OSUtil.isWindows()) {
            String cmd = String.format("start %s", outputFilePath);
            String[] xrayCmd = new String[]{"cmd.exe", "/c", String.format("%s", cmd)};
            exec(xrayCmd);
        } else {
            String cmd = String.format("open %s", outputFilePath);
            String[] xrayCmd = new String[]{"/bin/bash", "-c", String.format("%s", cmd)};
            exec(xrayCmd);
        }
    }
}
