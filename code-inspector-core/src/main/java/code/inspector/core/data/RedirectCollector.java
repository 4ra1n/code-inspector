package code.inspector.core.data;

import code.inspector.core.Const;
import code.inspector.core.asm.base.BaseClassVisitor;
import code.inspector.core.spring.SpringMapping;
import code.inspector.log.Log;
import code.inspector.model.ResultInfo;

import java.util.List;

public class RedirectCollector {
    private static final String REDIRECT = "Open Redirect";
    public static void collect(BaseClassVisitor cv, SpringMapping mapping, List<ResultInfo> results) {
        if (cv.getPass(Const.REDIRECT_SEND_RESPONSE_TYPE)) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setType(REDIRECT);
            resultInfo.setVulName("Servlet sendResponse Redirect");
            resultInfo.getChains().add(mapping.getController().getClassReference().getName() + "."
                    + mapping.getMethodReference().getName());
            results.add(resultInfo);
            Log.info(resultInfo.toString());
        }
        if (cv.getPass(Const.REDIRECT_STRING_TYPE)) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setType(REDIRECT);
            resultInfo.setVulName("Spring String Redirect");
            resultInfo.getChains().add(mapping.getController().getClassReference().getName() + "."
                    + mapping.getMethodReference().getName());
            results.add(resultInfo);
            Log.info(resultInfo.toString());
        }
        if (cv.getPass(Const.REDIRECT_MODEL_AND_VIEW_TYPE)) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setType(REDIRECT);
            resultInfo.setVulName("Spring ModelAndView Redirect");
            resultInfo.getChains().add(mapping.getController().getClassReference().getName() + "."
                    + mapping.getMethodReference().getName());
            results.add(resultInfo);
            Log.info(resultInfo.toString());
        }
    }
}
