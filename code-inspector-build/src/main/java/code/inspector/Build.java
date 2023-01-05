package code.inspector;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Build {
    static String oldVersion = "0.1-beta";
    static String newVersion = "0.2-beta";

    public static void main(String[] args) throws Exception {
        Path rootPom = Paths.get("pom.xml");
        Path apiPom = Paths.get("code-inspector-api/pom.xml");
        Path buildPom = Paths.get("code-inspector-build/pom.xml");
        Path constPom = Paths.get("code-inspector-const/pom.xml");
        Path corePom = Paths.get("code-inspector-core/pom.xml");
        Path demoPom = Paths.get("code-inspector-demo/pom.xml");
        Path jvmPom = Paths.get("code-inspector-jvm/pom.xml");
        Path modelPom = Paths.get("code-inspector-model/pom.xml");
        Path renderPom = Paths.get("code-inspector-render/pom.xml");
        Path springPom = Paths.get("code-inspector-spring/pom.xml");
        Path starterPom = Paths.get("code-inspector-starter/pom.xml");
        Path utilPom = Paths.get("code-inspector-util/pom.xml");
        Path starterCode = Paths.get("code-inspector-starter/src/main/java/" +
                "code/inspector/start/Application.java");

        replace(rootPom);
        replace(apiPom);
        replace(buildPom);
        replace(constPom);
        replace(corePom);
        replace(demoPom);
        replace(jvmPom);
        replace(modelPom);
        replace(renderPom);
        replace(springPom);
        replace(starterPom);
        replace(utilPom);
        replace(starterCode);
    }

    private static void replace(Path path) throws Exception {
        byte[] data = Files.readAllBytes(path);
        String newData = new String(data).replace(oldVersion, newVersion);
        Files.write(path, newData.getBytes());
    }
}
