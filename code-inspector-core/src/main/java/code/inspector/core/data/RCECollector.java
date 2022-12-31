package code.inspector.core.data;

import code.inspector.core.Const;
import code.inspector.core.asm.base.BaseClassVisitor;
import code.inspector.log.Log;
import code.inspector.model.ResultInfo;

import java.util.List;

public class RCECollector {

    private static final String RCE = "Remote Code/Command Execute";

    public static void collect(BaseClassVisitor cv, List<String> tempChain, List<ResultInfo> results) {
        if (cv.getPass(Const.RCE_RUNTIME_TYPE)) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setType(RCE);
            resultInfo.setVulName("Runtime exec RCE");
            resultInfo.getChains().addAll(tempChain);
            results.add(resultInfo);
            Log.info(resultInfo.toString());
        }
        if (cv.getPass(Const.RCE_PROCESS_TYPE)) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setType(RCE);
            resultInfo.setVulName("ProcessBuilder start RCE");
            resultInfo.getChains().addAll(tempChain);
            results.add(resultInfo);
            Log.info(resultInfo.toString());
        }
        if (cv.getPass(Const.RCE_GROOVY_TYPE)) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setType(RCE);
            resultInfo.setVulName("GroovyShell evaluate RCE");
            resultInfo.getChains().addAll(tempChain);
            results.add(resultInfo);
            Log.info(resultInfo.toString());
        }
        if (cv.getPass(Const.RCE_JNDI_TYPE)) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setType(RCE);
            resultInfo.setVulName("JNDI Injection RCE");
            resultInfo.getChains().addAll(tempChain);
            results.add(resultInfo);
            Log.info(resultInfo.toString());
        }
        if (cv.getPass(Const.RCE_SP_EL_TYPE)) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setType(RCE);
            resultInfo.setVulName("Spring EL RCE");
            resultInfo.getChains().addAll(tempChain);
            results.add(resultInfo);
            Log.info(resultInfo.toString());
        }
    }
}
