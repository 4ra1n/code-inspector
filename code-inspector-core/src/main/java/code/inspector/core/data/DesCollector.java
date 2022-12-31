package code.inspector.core.data;


import code.inspector.core.Const;
import code.inspector.core.asm.base.BaseClassVisitor;
import code.inspector.log.Log;
import code.inspector.model.ResultInfo;

import java.util.List;

public class DesCollector {
    private static final String DES = "Deserialization";

    public static void collect(BaseClassVisitor cv, List<String> tempChain, List<ResultInfo> results) {
        if (cv.getPass(Const.DESERIALIZATION_JDK)) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setType(DES);
            resultInfo.setVulName("JDK readObject");
            resultInfo.getChains().addAll(tempChain);
            results.add(resultInfo);
            Log.info(resultInfo.toString());
        }
        if (cv.getPass(Const.DESERIALIZATION_FASTJSON)) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setType(DES);
            resultInfo.setVulName("Fastjson");
            resultInfo.getChains().addAll(tempChain);
            results.add(resultInfo);
            Log.info(resultInfo.toString());
        }
        if (cv.getPass(Const.DESERIALIZATION_SNAKEYAML)) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setType(DES);
            resultInfo.setVulName("SnakeYAML load");
            resultInfo.getChains().addAll(tempChain);
            results.add(resultInfo);
            Log.info(resultInfo.toString());
        }
        if (cv.getPass(Const.DESERIALIZATION_JACKSON)) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setType(DES);
            resultInfo.setVulName("Jackson");
            resultInfo.getChains().addAll(tempChain);
            results.add(resultInfo);
            Log.info(resultInfo.toString());
        }
        if (cv.getPass(Const.DESERIALIZATION_HESSIAN)) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setType(DES);
            resultInfo.setVulName("Hessian2 readObject");
            resultInfo.getChains().addAll(tempChain);
            results.add(resultInfo);
            Log.info(resultInfo.toString());
        }
        if (cv.getPass(Const.DESERIALIZATION_XML_DECODER)) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setType(DES);
            resultInfo.setVulName("XMLDecoder readObject");
            resultInfo.getChains().addAll(tempChain);
            results.add(resultInfo);
            Log.info(resultInfo.toString());
        }
    }
}
