package code.inspector.core.util;

import code.inspector.model.ClassFile;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.stream.Stream;

public class JarUtil {

    private static final Set<ClassFile> classFileSet = new HashSet<>();

    public static List<ClassFile> resolveNormalJarFile(String jarPath) {
        try {
            final Path tmpDir = Files.createDirectory(Paths.get(
                    "temp" + File.separator + Paths.get(jarPath).getFileName().toString()));
            resolve(jarPath, tmpDir);
            return new ArrayList<>(classFileSet);
        } catch (Exception ignored) {
        }
        return new ArrayList<>();
    }

    private static void resolve(String jarPath, Path tmpDir) {
        try {
            InputStream is = Files.newInputStream(Paths.get(jarPath));
            JarInputStream jarInputStream = new JarInputStream(is);
            JarEntry jarEntry;
            while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
                Path fullPath = tmpDir.resolve(jarEntry.getName());
                if (!jarEntry.isDirectory()) {
                    if (!jarEntry.getName().endsWith(".class")) {
                        continue;
                    }
                    Path dirName = fullPath.getParent();
                    if (!Files.exists(dirName)) {
                        Files.createDirectories(dirName);
                    }
                    OutputStream outputStream = Files.newOutputStream(fullPath);
                    IOUtil.copy(jarInputStream, outputStream);
                    ClassFile classFile = new ClassFile(jarEntry.getName(), fullPath);
                    classFileSet.add(classFile);
                }
            }
        } catch (Exception ignored) {
        }
    }

    public static List<ClassFile> resolveSpringBootJarFile(String jarPath, boolean useAllLib) {
        try {
            final Path tmpDir = Files.createDirectory(Paths.get(
                    "temp" + File.separator + Paths.get(jarPath).getFileName().toString()));
            resolve(jarPath, tmpDir);
            if (useAllLib) {
                resolveBoot(jarPath, tmpDir);
                Stream<Path> paths = Files.list(tmpDir.resolve("BOOT-INF/lib"));
                paths.forEach(p -> resolveNormalJarFile(p.toFile().getAbsolutePath()));
                paths.close();
            }
            return new ArrayList<>(classFileSet);
        } catch (Exception ignored) {
        }
        return new ArrayList<>();
    }

    private static void resolveBoot(String jarPath, Path tmpDir) {
        try {
            InputStream is = Files.newInputStream(Paths.get(jarPath));
            JarInputStream jarInputStream = new JarInputStream(is);
            JarEntry jarEntry;
            while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
                Path fullPath = tmpDir.resolve(jarEntry.getName());
                if (!jarEntry.isDirectory()) {
                    if (!jarEntry.getName().endsWith(".jar")) {
                        continue;
                    }
                    Path dirName = fullPath.getParent();
                    if (!Files.exists(dirName)) {
                        Files.createDirectories(dirName);
                    }
                    OutputStream outputStream = Files.newOutputStream(fullPath);
                    IOUtil.copy(jarInputStream, outputStream);
                }
            }
        } catch (Exception ignored) {
        }
    }
}