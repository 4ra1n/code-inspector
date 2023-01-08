package code.inspector.graphviz;

import code.inspector.model.MethodReference;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static guru.nidi.graphviz.model.Factory.*;

public class GraphvizCore {
    @SuppressWarnings("all")
    private static String getOutput(MethodReference mr) {
        StringBuilder sb = new StringBuilder();
        sb.append(mr.getClassReference().getName());
        sb.append("\n");
        sb.append(mr.getName());
        return sb.toString();
    }

    @SuppressWarnings("all")
    private static String getFirstOutput(MethodReference mr,int index) {
        StringBuilder sb = new StringBuilder();
        sb.append(mr.getClassReference().getName());
        sb.append("\n");
        sb.append(mr.getName());
        sb.append("\n");
        sb.append("param index: ");
        sb.append(String.valueOf(index));
        return sb.toString();
    }

    public static void work(String outputDir, List<MethodReference> mrList,int pIndex) {
        try {
            Path targetDir = Paths.get(outputDir);
            if (Files.notExists(targetDir)) {
                Files.createDirectories(targetDir);
            }
            String output = String.format("%s%s%s%s", outputDir, File.separator, "test", ".jpg");
            MutableGraph g = mutGraph("code-inspector")
                    .setDirected(true).use((gr, ctx) -> {
                        int index = 0;
                        MutableNode lastMn = null;
                        for (MethodReference mr : mrList) {
                            index++;
                            if (index == 1) {
                                lastMn = mutNode(getFirstOutput(mr,pIndex)).add(Shape.RECTANGLE);
                                continue;
                            }
                            MutableNode newMn = mutNode(getOutput(mr)).add(Shape.RECTANGLE);
                            lastMn.addLink(newMn);
                            lastMn = newMn;
                        }
                    });
            Graphviz.fromGraph(g).width(800).render(Format.PNG).toFile(new File(output));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
