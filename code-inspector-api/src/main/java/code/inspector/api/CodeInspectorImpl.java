package code.inspector.api;

import code.inspector.core.Application;
import code.inspector.core.Command;
import code.inspector.core.Const;
import code.inspector.model.ResultInfo;

import java.util.HashMap;
import java.util.List;

public class CodeInspectorImpl implements CodeInspector {
    @Override
    public List<ResultInfo> analyzeAll(String jarPath,
                                       boolean isDebug, boolean useRt, boolean allLib) {
        Command cmd = new Command();
        HashMap<String, Boolean> option = new HashMap<>();
        option.put(Const.RCE_MODULE, true);
        option.put(Const.RCE_RUNTIME_TYPE, true);
        option.put(Const.RCE_PROCESS_TYPE, true);
        option.put(Const.RCE_GROOVY_TYPE, true);
        option.put(Const.RCE_JNDI_TYPE, true);
        option.put(Const.RCE_SP_EL_TYPE, true);
        option.put(Const.SSRF_MODULE, true);
        option.put(Const.SSRF_JDK_TYPE, true);
        option.put(Const.SSRF_APACHE_TYPE, true);
        option.put(Const.SSRF_SOCKET_TYPE, true);
        option.put(Const.SSRF_OKHTTP_TYPE, true);
        option.put(Const.REDIRECT_MODULE, true);
        option.put(Const.REDIRECT_MODEL_AND_VIEW_TYPE, true);
        option.put(Const.REDIRECT_STRING_TYPE, true);
        option.put(Const.REDIRECT_SEND_RESPONSE_TYPE, true);
        option.put(Const.SQL_MODULE, true);
        option.put(Const.SQL_JDBC_TEMPLATE_EXECUTE, true);
        option.put(Const.SQL_JDBC_TEMPLATE_UPDATE, true);
        option.put(Const.SQL_JDBC_TEMPLATE_QUERY_ANY, true);
        option.put(Const.SQL_EXECUTE_QUERY, true);
        option.put(Const.SQL_EXECUTE_UPDATE, true);
        option.put(Const.SQL_EXECUTE, true);
        option.put(Const.DESERIALIZATION_MODULE, true);
        option.put(Const.DESERIALIZATION_JDK, true);
        option.put(Const.DESERIALIZATION_FASTJSON, true);
        option.put(Const.DESERIALIZATION_JACKSON, true);
        option.put(Const.DESERIALIZATION_SNAKEYAML, true);
        option.put(Const.DESERIALIZATION_HESSIAN, true);
        option.put(Const.DESERIALIZATION_XML_DECODER, true);
        option.put(Const.DOS_MODULE, true);
        option.put(Const.DOS_FOR_TYPE, true);
        option.put(Const.DOS_ARRAY_TYPE, true);
        option.put(Const.DOS_REGEX_TYPE, true);
        option.put(Const.DOS_LIST_TYPE, true);
        cmd.springBoot = true;
        cmd.jars.add(jarPath);
        cmd.isDebug = isDebug;
        cmd.jdk = useRt;
        cmd.lib = allLib;
        cmd.setOptions(option);
        return Application.start(cmd, false);
    }

    @Override
    public List<ResultInfo> analyzeRCE(String jarPath,
                                       boolean isDebug, boolean useRt, boolean allLib) {
        Command cmd = new Command();
        HashMap<String, Boolean> option = new HashMap<>();
        option.put(Const.RCE_MODULE, true);
        option.put(Const.RCE_RUNTIME_TYPE, true);
        option.put(Const.RCE_PROCESS_TYPE, true);
        option.put(Const.RCE_GROOVY_TYPE, true);
        option.put(Const.RCE_JNDI_TYPE, true);
        option.put(Const.RCE_SP_EL_TYPE, true);
        cmd.springBoot = true;
        cmd.jars.add(jarPath);
        cmd.isDebug = isDebug;
        cmd.jdk = useRt;
        cmd.lib = allLib;
        cmd.setOptions(option);
        return Application.start(cmd, false);
    }

    @Override
    public List<ResultInfo> analyzeSSRF(String jarPath,
                                        boolean isDebug, boolean useRt, boolean allLib) {
        Command cmd = new Command();
        HashMap<String, Boolean> option = new HashMap<>();
        option.put(Const.SSRF_MODULE, true);
        option.put(Const.SSRF_JDK_TYPE, true);
        option.put(Const.SSRF_APACHE_TYPE, true);
        option.put(Const.SSRF_SOCKET_TYPE, true);
        option.put(Const.SSRF_OKHTTP_TYPE, true);
        cmd.springBoot = true;
        cmd.jars.add(jarPath);
        cmd.isDebug = isDebug;
        cmd.jdk = useRt;
        cmd.lib = allLib;
        cmd.setOptions(option);
        return Application.start(cmd, false);
    }

    @Override
    public List<ResultInfo> analyzeRedirect(String jarPath,
                                            boolean isDebug, boolean useRt, boolean allLib) {
        Command cmd = new Command();
        HashMap<String, Boolean> option = new HashMap<>();
        option.put(Const.REDIRECT_MODULE, true);
        option.put(Const.REDIRECT_MODEL_AND_VIEW_TYPE, true);
        option.put(Const.REDIRECT_STRING_TYPE, true);
        option.put(Const.REDIRECT_SEND_RESPONSE_TYPE, true);
        cmd.springBoot = true;
        cmd.jars.add(jarPath);
        cmd.isDebug = isDebug;
        cmd.jdk = useRt;
        cmd.lib = allLib;
        cmd.setOptions(option);
        return Application.start(cmd, false);
    }

    @Override
    public List<ResultInfo> analyzeSQLInjection(String jarPath,
                                                boolean isDebug, boolean useRt, boolean allLib) {
        Command cmd = new Command();
        HashMap<String, Boolean> option = new HashMap<>();
        option.put(Const.SQL_MODULE, true);
        option.put(Const.SQL_JDBC_TEMPLATE_EXECUTE, true);
        option.put(Const.SQL_JDBC_TEMPLATE_UPDATE, true);
        option.put(Const.SQL_JDBC_TEMPLATE_QUERY_ANY, true);
        option.put(Const.SQL_EXECUTE_QUERY, true);
        option.put(Const.SQL_EXECUTE_UPDATE, true);
        option.put(Const.SQL_EXECUTE, true);
        cmd.springBoot = true;
        cmd.jars.add(jarPath);
        cmd.isDebug = isDebug;
        cmd.jdk = useRt;
        cmd.lib = allLib;
        cmd.setOptions(option);
        return Application.start(cmd, false);
    }

    @Override
    public List<ResultInfo> analyzeDeserialization(String jarPath,
                                                   boolean isDebug, boolean useRt, boolean allLib) {
        Command cmd = new Command();
        HashMap<String, Boolean> option = new HashMap<>();
        option.put(Const.DESERIALIZATION_MODULE, true);
        option.put(Const.DESERIALIZATION_JDK, true);
        option.put(Const.DESERIALIZATION_FASTJSON, true);
        option.put(Const.DESERIALIZATION_JACKSON, true);
        option.put(Const.DESERIALIZATION_SNAKEYAML, true);
        option.put(Const.DESERIALIZATION_HESSIAN, true);
        option.put(Const.DESERIALIZATION_XML_DECODER, true);
        cmd.springBoot = true;
        cmd.jars.add(jarPath);
        cmd.isDebug = isDebug;
        cmd.jdk = useRt;
        cmd.lib = allLib;
        cmd.setOptions(option);
        return Application.start(cmd, false);
    }

    @Override
    public List<ResultInfo> analyzeDoS(String jarPath,
                                       boolean isDebug, boolean useRt, boolean allLib) {
        Command cmd = new Command();
        HashMap<String, Boolean> option = new HashMap<>();
        option.put(Const.DOS_MODULE, true);
        option.put(Const.DOS_FOR_TYPE, true);
        option.put(Const.DOS_ARRAY_TYPE, true);
        option.put(Const.DOS_REGEX_TYPE, true);
        option.put(Const.DOS_LIST_TYPE, true);
        cmd.springBoot = true;
        cmd.jars.add(jarPath);
        cmd.isDebug = isDebug;
        cmd.jdk = useRt;
        cmd.lib = allLib;
        cmd.setOptions(option);
        return Application.start(cmd, false);
    }
}
