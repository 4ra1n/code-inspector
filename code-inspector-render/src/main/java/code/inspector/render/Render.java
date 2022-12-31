package code.inspector.render;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Render {
    public static void renderHtml(Path savePath, List<RenderData> dataList) {
        byte[] prefix = IOUtil.readBytesFromIs(Render.class.getClassLoader().getResourceAsStream("prefix"));
        byte[] suffix = IOUtil.readBytesFromIs(Render.class.getClassLoader().getResourceAsStream("suffix"));
        if (prefix == null || prefix.length == 0 || suffix == null || suffix.length == 0) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        int index = 0;
        if (dataList == null) {
            return;
        }
        for (RenderData renderData : dataList) {
            index++;
            String temp = String.format("<tr>\n" +
                            "<th scope=\"row\">%s</th>\n" +
                            "<td><span class=\"badge badge-danger\">%s</span></td>\n" +
                            "<td><span class=\"badge badge-warning\">%s</span></td>\n" +
                            "<td>%s</td></tr>", index, renderData.getType(),
                    renderData.getCategory(), renderData.getDetails());
            sb.append(temp);
        }
        String data = sb.toString();
        byte[] dataBytes = data.getBytes();
        byte[] total = new byte[prefix.length + suffix.length + dataBytes.length];
        System.arraycopy(prefix, 0, total, 0, prefix.length);
        System.arraycopy(dataBytes, 0, total, prefix.length, dataBytes.length);
        System.arraycopy(suffix, 0, total, prefix.length + dataBytes.length, suffix.length);
        try {
            Files.write(savePath, total);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
