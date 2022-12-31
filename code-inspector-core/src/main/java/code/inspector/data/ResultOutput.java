package code.inspector.data;


import code.inspector.log.Log;
import code.inspector.model.ResultInfo;
import code.inspector.render.Render;
import code.inspector.render.RenderData;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ResultOutput {
    public static void write(String path, List<ResultInfo> results) {
        try {
            Set<ResultInfo> newResults = new LinkedHashSet<>(results);
            Log.info("total results: " + newResults.size());
            List<RenderData> rd = new ArrayList<>();
            for (ResultInfo r : newResults) {
                RenderData d = new RenderData();
                d.setType(r.getType());
                d.setCategory(r.getVulName());
                StringBuilder sb = new StringBuilder();
                for (String c : r.getChains()) {
                    sb.append(c);
                    sb.append("<br>");
                }
                String detail = sb.toString();
                d.setDetails(detail.substring(0, detail.length() - 4));
                rd.add(d);
            }
            Render.renderHtml(Paths.get(path), rd);
        } catch (Exception ignored) {
        }
    }
}
