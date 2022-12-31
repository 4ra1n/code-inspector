package code.inspector.core.service.base;

import code.inspector.core.asm.base.BaseClassVisitor;
import code.inspector.core.spring.SpringController;
import code.inspector.core.spring.SpringMapping;
import code.inspector.model.CallGraph;
import code.inspector.model.ClassFile;
import code.inspector.model.MethodReference;
import code.inspector.model.ResultInfo;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

public abstract class BaseService {
    private static Map<MethodReference.Handle, Set<CallGraph>> allCalls;
    private static Map<String, ClassFile> classFileMap;
    private static final Set<String> tempChain = new LinkedHashSet<>();
    private static final List<ResultInfo> results = new ArrayList<>();
    private static Class<?> classVisitor;
    private static Class<?> collector;

    public static void start0(Map<String, ClassFile> classFileByName,
                              List<SpringController> controllers,
                              Map<MethodReference.Handle, Set<CallGraph>> discoveredCalls,
                              Class<?> targetClassVisitor, Class<?> targetCollector) {
        results.clear();
        allCalls = discoveredCalls;
        classFileMap = classFileByName;
        classVisitor = targetClassVisitor;
        collector = targetCollector;
        for (SpringController controller : controllers) {
            for (SpringMapping mapping : controller.getMappings()) {
                MethodReference methodReference = mapping.getMethodReference();
                if (methodReference == null) {
                    tempChain.clear();
                    continue;
                }
                Type[] argTypes = Type.getArgumentTypes(methodReference.getDesc());
                Type[] extendedArgTypes = new Type[argTypes.length + 1];
                System.arraycopy(argTypes, 0, extendedArgTypes, 1, argTypes.length);
                argTypes = extendedArgTypes;
                boolean[] vulnerableIndex = new boolean[argTypes.length];
                for (int i = 1; i < argTypes.length; i++) {
                    vulnerableIndex[i] = true;
                }
                Set<CallGraph> calls = allCalls.get(methodReference.getHandle());
                tempChain.add(methodReference.getClassReference().getName() +
                        "." + methodReference.getName());

                ClassFile file = classFileMap.get(methodReference.getClassReference().getName());
                try {
                    if (file == null) {
                        tempChain.clear();
                        continue;
                    }
                    ClassReader cr = new ClassReader(file.getFile());
                    Constructor<?> c = classVisitor.getConstructor(MethodReference.Handle.class, int.class);
                    Object obj = c.newInstance(methodReference.getHandle(), 100);
                    BaseClassVisitor cv = (BaseClassVisitor) obj;
                    cr.accept(cv, ClassReader.EXPAND_FRAMES);
                    if (cv.getPassSize() != 0) {
                        Method method = collector.getMethod("collect",
                                BaseClassVisitor.class, List.class, List.class);
                        method.invoke(null, cv, new ArrayList<>(tempChain), results);
                        tempChain.clear();
                        continue;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    tempChain.clear();
                    continue;
                }
                if (calls == null) {
                    tempChain.clear();
                    continue;
                }
                for (CallGraph callGraph : calls) {
                    int callerIndex = callGraph.getCallerArgIndex();
                    if (callerIndex == -1) {
                        continue;
                    }
                    if (vulnerableIndex[callerIndex]) {
                        tempChain.add(callGraph.getTargetMethod().getClassReference().getName() + "." +
                                callGraph.getTargetMethod().getName());
                        List<MethodReference.Handle> visited = new ArrayList<>();
                        doTask(callGraph.getTargetMethod(), callGraph.getTargetArgIndex(), visited);
                    }
                    tempChain.clear();
                    tempChain.add(methodReference.getClassReference().getName() +
                            "." + methodReference.getName());
                }
                tempChain.clear();
            }
        }
    }

    public static List<ResultInfo> getResults() {
        return results;
    }

    private static void doTask(MethodReference.Handle targetMethod, int targetIndex,
                               List<MethodReference.Handle> visited) {
        if (visited.contains(targetMethod)) {
            return;
        } else {
            visited.add(targetMethod);
        }
        ClassFile file = classFileMap.get(targetMethod.getClassReference().getName());
        try {
            if (file == null) {
                return;
            }
            ClassReader cr = new ClassReader(file.getFile());
            Constructor<?> c = classVisitor.getConstructor(MethodReference.Handle.class, int.class);
            Object obj = c.newInstance(targetMethod, targetIndex);
            BaseClassVisitor cv = (BaseClassVisitor) obj;
            cr.accept(cv, ClassReader.EXPAND_FRAMES);
            if (cv.getPassSize() != 0) {
                Method method = collector.getMethod("collect", BaseClassVisitor.class, List.class, List.class);
                method.invoke(null, cv, new ArrayList<>(tempChain), results);
                tempChain.clear();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Set<CallGraph> calls = allCalls.get(targetMethod);
        if (calls == null || calls.size() == 0) {
            return;
        }
        for (CallGraph callGraph : calls) {
            if (callGraph.getCallerArgIndex() == targetIndex && targetIndex != -1) {
                if (visited.contains(callGraph.getTargetMethod())) {
                    return;
                }
                tempChain.add(callGraph.getTargetMethod().getClassReference().getName() + "." +
                        callGraph.getTargetMethod().getName());
                doTask(callGraph.getTargetMethod(), callGraph.getTargetArgIndex(), visited);
            }
        }
    }
}
