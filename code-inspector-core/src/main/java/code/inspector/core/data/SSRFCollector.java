package code.inspector.core.data;


import code.inspector.core.Const;
import code.inspector.core.asm.base.BaseClassVisitor;
import code.inspector.log.Log;
import code.inspector.model.ResultInfo;

import java.util.List;

public class SSRFCollector {
    private static final String SSRF = "Server-Side Request Forgery";

    public static void collect(BaseClassVisitor cv, List<String> tempChain, List<ResultInfo> results) {
        if (cv.getPass(Const.SSRF_OKHTTP_TYPE)) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setType(SSRF);
            resultInfo.setVulName("OKHTTP SSRF");
            resultInfo.getChains().addAll(tempChain);
            results.add(resultInfo);
            Log.info(resultInfo.toString());
        }
        if (cv.getPass(Const.SSRF_SOCKET_TYPE)) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setType(SSRF);
            resultInfo.setVulName("Socket SSRF");
            resultInfo.getChains().addAll(tempChain);
            results.add(resultInfo);
            Log.info(resultInfo.toString());
        }
        if (cv.getPass(Const.SSRF_APACHE_TYPE)) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setType(SSRF);
            resultInfo.setVulName("Apache HttpClient SSRF");
            resultInfo.getChains().addAll(tempChain);
            results.add(resultInfo);
            Log.info(resultInfo.toString());
        }
        if (cv.getPass(Const.SSRF_JDK_TYPE)) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setType(SSRF);
            resultInfo.setVulName("HttpUrlConnection SSRF");
            resultInfo.getChains().addAll(tempChain);
            results.add(resultInfo);
            Log.info(resultInfo.toString());
        }
    }
}
