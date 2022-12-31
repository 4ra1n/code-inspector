package code.inspector.log;

import code.inspector.form.CodeInspector;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("unused")
public class Log {
    public static void info(String info) {
        if (CodeInspector.instance == null) {
            return;
        }
        info(CodeInspector.instance.getLogArea(), info);
    }

    public static void warn(String info) {
        if (CodeInspector.instance == null) {
            return;
        }
        warn(CodeInspector.instance.getLogArea(), info);
    }

    public static void error(String info) {
        if (CodeInspector.instance == null) {
            return;
        }
        info(CodeInspector.instance.getLogArea(), info);
    }

    private static String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("HH:mm:ss");
        Date date = new Date();
        return sdf.format(date);
    }

    private static void info(JTextArea area, String info) {
        if (area == null) {
            return;
        }
        area.append(String.format("[info] [%s] %s\n", getDate(), info));
        area.setCaretPosition(area.getDocument().getLength());
    }

    private static void warn(JTextArea area, String info) {
        if (area == null) {
            return;
        }
        area.append(String.format("[warn] [%s] %s\n", getDate(), info));
        area.setCaretPosition(area.getDocument().getLength());
    }

    private static void error(JTextArea area, String info) {
        if (area == null) {
            return;
        }
        area.append(String.format("[error] [%s] %s\n", getDate(), info));
        area.setCaretPosition(area.getDocument().getLength());
    }
}