package code.inspector.core;

import code.inspector.core.inherit.InheritanceMap;
import code.inspector.core.inherit.InheritanceUtil;
import code.inspector.core.service.*;
import code.inspector.core.service.system.*;
import code.inspector.core.spring.SpringController;
import code.inspector.core.spring.SpringMapping;
import code.inspector.core.util.ClassUtil;
import code.inspector.core.util.DirUtil;
import code.inspector.data.Output;
import code.inspector.data.ResultOutput;
import code.inspector.graphviz.GraphvizCore;
import code.inspector.log.Log;
import code.inspector.model.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Application {
    /**
     * CLASS FILE LIST
     */
    private static final List<ClassFile> classFileList = new ArrayList<>();
    /**
     * ALL CLASS INFO
     */
    private static final List<ClassReference> discoveredClasses = new ArrayList<>();
    /**
     * ALL METHOD INFO
     */
    private static final List<MethodReference> discoveredMethods = new ArrayList<>();
    /**
     * CLASS NAME -> CLASS INFO
     */
    private static final Map<ClassReference.Handle, ClassReference> classMap = new HashMap<>();
    /**
     * METHOD NAME -> METHOD INFO
     */
    private static final Map<MethodReference.Handle, MethodReference> methodMap = new HashMap<>();
    /**
     * METHOD NAME -> CALL METHOD NAME
     */
    private static final Map<MethodReference.Handle, Set<MethodReference.Handle>> methodCalls = new HashMap<>();
    /**
     * CLASS NAME -> CLASS FILE
     */
    private static final Map<String, ClassFile> classFileByName = new HashMap<>();
    /**
     * METHOD NAME -> ALL METHOD IMPLS
     */
    private static final Map<MethodReference.Handle, Set<MethodReference.Handle>> methodImpls = new HashMap<>();
    /**
     * SORTED METHODS
     */
    private static List<MethodReference.Handle> sortedMethods = new ArrayList<>();
    /**
     * CALL GRAPHS
     */
    private static final Set<CallGraph> discoveredCalls = new HashSet<>();
    /**
     * METHOD NAME -> CALL GRAPHS
     */
    private static final Map<MethodReference.Handle, Set<CallGraph>> graphCallMap = new HashMap<>();
    /**
     * SPRING CONTROLLERS
     */
    private static final List<SpringController> controllers = new ArrayList<>();
    /**
     * RESULTS
     */
    private static final List<ResultInfo> resultInfoList = new ArrayList<>();
    /**
     * GLOBAL OPTIONS
     */
    public static final HashMap<String, Boolean> globalOptions = new HashMap<>();

    private static void clear() {
        classFileList.clear();
        discoveredCalls.clear();
        discoveredMethods.clear();
        classMap.clear();
        methodMap.clear();
        methodCalls.clear();
        classFileByName.clear();
        methodImpls.clear();
        sortedMethods.clear();
        discoveredCalls.clear();
        graphCallMap.clear();
        controllers.clear();
        resultInfoList.clear();
        DirUtil.removeDir(Paths.get("temp").toFile());
    }

    public static List<ResultInfo> start(Command command, boolean output) {
        if (command.jars.size() == 0) {
            Log.warn("no jar file");
            return null;
        }
        if (command.isDebug) {
            Log.info("use debug mode");
        }
        clear();
        globalOptions.clear();
        globalOptions.putAll(command.getOptions());
        getClassFileList(command);
        discovery();
        inherit();
        methodCall();
        sort(command);
        parseSpring(command);
        buildCallGraphs(command);
        if (globalOptions.getOrDefault(Const.SSRF_MODULE, false)) {
            SSRFService.start(classFileByName, controllers, graphCallMap);
            resultInfoList.addAll(SSRFService.getResults());
        }
        if (globalOptions.getOrDefault(Const.SQL_MODULE, false)) {
            SqlInjectService.start(classFileByName, controllers, graphCallMap);
            resultInfoList.addAll(SqlInjectService.getResults());
        }
        if (globalOptions.getOrDefault(Const.RCE_MODULE, false)) {
            RCEService.start(classFileByName, controllers, graphCallMap);
            resultInfoList.addAll(RCEService.getResults());
        }
        if (globalOptions.getOrDefault(Const.DOS_MODULE, false)) {
            DOSService.start(classFileByName, controllers, graphCallMap);
            resultInfoList.addAll(DOSService.getResults());
        }
        if (globalOptions.getOrDefault(Const.REDIRECT_MODULE, false)) {
            RedirectService.start(classFileByName, controllers);
            resultInfoList.addAll(RedirectService.getResults());
        }
        if (globalOptions.getOrDefault(Const.DESERIALIZATION_MODULE, false)) {
            DesService.start(classFileByName, controllers, graphCallMap);
            resultInfoList.addAll(DesService.getResults());
        }
        LinkedHashSet<ResultInfo> temp = new LinkedHashSet<>(resultInfoList);
        resultInfoList.clear();
        resultInfoList.addAll(new ArrayList<>(temp));
        if (output) {
            ResultOutput.write(command.path, resultInfoList);
        }
        return resultInfoList;
    }

    private static void getClassFileList(Command command) {
        try {
            Path path = Paths.get("temp");
            Files.deleteIfExists(path);
            Files.createDirectories(path);
        } catch (Exception ex) {
            Log.error("error make temp dir");
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
                DirUtil.removeDir(Paths.get("temp").toFile())));
        if (command.springBoot) {
            classFileList.addAll(ClassUtil.getAllClassesFromBoots(command.jars, command.jdk, command.lib));
        } else {
            classFileList.addAll(ClassUtil.getAllClassesFromJars(command.jars, command.jdk));
        }
    }

    public static void graphvizWork(String ccn, String mmn, int index) {
        MethodReference.Handle mh = null;
        for (SpringController c : controllers) {
            if (c.getClassName().getName().equals(ccn)) {
                for (SpringMapping mapping : c.getMappings()) {
                    if (mapping.getMethodReference().getName().equals(mmn)) {
                        mh = mapping.getMethodName();
                    }
                }
            }
        }
        if (mh == null) {
            Log.warn("graphviz no result");
            return;
        }
        List<MethodReference> mrList = new ArrayList<>();
        MethodReference mr = methodMap.get(mh);
        if (mr != null) {
            mrList.add(mr);
        }
        Set<CallGraph> calls = graphCallMap.get(mh);
        for (CallGraph call : calls) {
            if (call.getTargetArgIndex() == index) {
                MethodReference m = methodMap.get(call.getTargetMethod());
                if (mr != null) {
                    mrList.add(m);
                }
                List<MethodReference.Handle> visited = new ArrayList<>();
                doTask(call.getTargetMethod(), call.getTargetArgIndex(), visited, mrList);
            }
        }
        GraphvizCore.work("graphviz-code-inspector", mrList, index);
    }

    private static void doTask(MethodReference.Handle targetMethod, int targetIndex,
                               List<MethodReference.Handle> visited, List<MethodReference> mrList) {
        if (visited.contains(targetMethod)) {
            return;
        } else {
            visited.add(targetMethod);
        }
        Set<CallGraph> calls = graphCallMap.get(targetMethod);
        if (calls == null || calls.size() == 0) {
            return;
        }
        for (CallGraph callGraph : calls) {
            if (callGraph.getCallerArgIndex() == targetIndex && targetIndex != -1) {
                if (visited.contains(callGraph.getTargetMethod())) {
                    return;
                }
                MethodReference mr = methodMap.get(callGraph.getTargetMethod());
                if (mr != null) {
                    mrList.add(mr);
                }
                doTask(callGraph.getTargetMethod(), callGraph.getTargetArgIndex(), visited, mrList);
            }
        }
    }

    private static void discovery() {
        DiscoveryService.start(classFileList, discoveredClasses, discoveredMethods,
                classMap, methodMap, classFileByName);
        Log.info("total classes: " + discoveredClasses.size());
        Log.info("total methods: " + discoveredMethods.size());
    }

    private static void inherit() {
        InheritanceMap inheritanceMap = InheritanceService.start(classMap);
        methodImpls.putAll(InheritanceUtil.getAllMethodImplementations(inheritanceMap, methodMap));
    }

    private static void methodCall() {
        MethodCallService.start(classFileList, methodCalls);
    }

    private static void sort(Command command) {
        sortedMethods = SortService.start(methodCalls);
        if (command.isDebug) {
            Output.writeSortedMethod(sortedMethods);
        }
    }

    private static void buildCallGraphs(Command command) {
        CallGraphService.start(discoveredCalls, sortedMethods, classFileByName,
                classMap, graphCallMap, methodMap, methodImpls);
        if (command.isDebug) {
            if (controllers.size() > 0) {
                SpringController controller = controllers.get(1);
                command.packageName = controller.getClassName().getName();
            }
            Output.writeTargetCallGraphs(graphCallMap, command.packageName);
        }
    }

    private static void parseSpring(Command command) {
        if (command.springBoot) {
            SpringService.start(classFileList, controllers, classMap, methodMap);
            if (command.isDebug) {
                Output.writeControllers(controllers);
            }
        }
    }
}
