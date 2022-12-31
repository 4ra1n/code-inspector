package code.inspector.core.data;


import code.inspector.core.Const;
import code.inspector.core.asm.base.BaseClassVisitor;
import code.inspector.log.Log;
import code.inspector.model.ResultInfo;

import java.util.List;

public class DoSCollector {
    private static final String DOS = "Denial of Service";

    public static void collect(BaseClassVisitor cv, List<String> tempChain, List<ResultInfo> results) {
        if (cv.getPass(Const.DOS_REGEX_TYPE)) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setType(DOS);
            resultInfo.setVulName("Pattern matches ReDoS");
            resultInfo.getChains().addAll(tempChain);
            results.add(resultInfo);
            Log.info(resultInfo.toString());
        }
        if (cv.getPass(Const.DOS_ARRAY_TYPE)) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setType(DOS);
            resultInfo.setVulName("Array init memory DoS");
            resultInfo.getChains().addAll(tempChain);
            results.add(resultInfo);
            Log.info(resultInfo.toString());
        }
        if (cv.getPass(Const.DOS_FOR_TYPE)) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setType(DOS);
            resultInfo.setVulName("For loop controllable DoS");
            resultInfo.getChains().addAll(tempChain);
            results.add(resultInfo);
            Log.info(resultInfo.toString());
        }
        if ( cv.getPass(Const.DOS_LIST_TYPE)) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setType(DOS);
            resultInfo.setVulName("ArrayList init memory DoS");
            resultInfo.getChains().addAll(tempChain);
            results.add(resultInfo);
            Log.info(resultInfo.toString());
        }
    }
}
