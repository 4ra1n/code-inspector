package code.inspector.api;

import code.inspector.model.ResultInfo;

import java.util.List;

public interface CodeInspector {
    List<ResultInfo> analyzeAll(String jarPath, boolean isDebug, boolean useRt, boolean allLib);

    List<ResultInfo> analyzeRCE(String jarPath, boolean isDebug, boolean useRt, boolean allLib);

    List<ResultInfo> analyzeSSRF(String jarPath, boolean isDebug, boolean useRt, boolean allLib);

    List<ResultInfo> analyzeRedirect(String jarPath, boolean isDebug, boolean useRt, boolean allLib);

    List<ResultInfo> analyzeSQLInjection(String jarPath, boolean isDebug, boolean useRt, boolean allLib);

    List<ResultInfo> analyzeDeserialization(String jarPath, boolean isDebug, boolean useRt, boolean allLib);

    List<ResultInfo> analyzeDoS(String jarPath, boolean isDebug, boolean useRt, boolean allLib);
}

