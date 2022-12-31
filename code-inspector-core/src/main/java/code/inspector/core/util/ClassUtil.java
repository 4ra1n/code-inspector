package code.inspector.core.util;

import code.inspector.model.ClassFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClassUtil {

    public static List<ClassFile> getAllClassesFromJars(List<String> jarPathList,
                                                        boolean runtime) {
        Set<ClassFile> classFileSet = new HashSet<>();
        if (runtime) {
            getRuntime(classFileSet);
        }
        for (String jarPath : jarPathList) {
            classFileSet.addAll(JarUtil.resolveNormalJarFile(jarPath));
        }
        return new ArrayList<>(classFileSet);
    }

    public static List<ClassFile> getAllClassesFromBoots(List<String> bootPathList,
                                                         boolean runtime,
                                                         boolean useAllLib) {
        Set<ClassFile> classFileSet = new HashSet<>();
        if (runtime) {
            getRuntime(classFileSet);
        }
        for (String jarPath : bootPathList) {
            classFileSet.addAll(JarUtil.resolveSpringBootJarFile(jarPath, useAllLib));
        }
        return new ArrayList<>(classFileSet);
    }

    private static void getRuntime(Set<ClassFile> classFileSet) {
        String rtJarPath = System.getenv("JAVA_HOME") +
                File.separator + "jre" +
                File.separator + "lib" +
                File.separator + "rt.jar";
        Path rtPath = Paths.get(rtJarPath);
        if (!Files.exists(rtPath)) {
            throw new RuntimeException("rt.jar not exists");
        }
        classFileSet.addAll(JarUtil.resolveNormalJarFile(rtJarPath));
    }
}
