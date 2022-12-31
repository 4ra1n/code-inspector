package code.inspector.core.inherit;

import code.inspector.model.ClassReference;

import java.util.*;


@SuppressWarnings("all")
public class InheritanceMap {
    /**
     * 子类 -> 所有父类
     */
    private final Map<ClassReference.Handle, Set<ClassReference.Handle>> inheritanceMap;
    /**
     * 父类 -> 所有子类
     */
    private final Map<ClassReference.Handle, Set<ClassReference.Handle>> subClassMap;

    public InheritanceMap(Map<ClassReference.Handle, Set<ClassReference.Handle>> inheritanceMap) {
        this.inheritanceMap = inheritanceMap;
        subClassMap = new HashMap<>();
        for (Map.Entry<ClassReference.Handle, Set<ClassReference.Handle>> entry : inheritanceMap.entrySet()) {
            ClassReference.Handle child = entry.getKey();
            for (ClassReference.Handle parent : entry.getValue()) {
                if (!subClassMap.containsKey(parent)) {
                    Set<ClassReference.Handle> tempSet = new HashSet<>();
                    tempSet.add(child);
                    subClassMap.put(parent, tempSet);
                } else {
                    subClassMap.get(parent).add(child);
                }
            }
        }
    }

    public Set<Map.Entry<ClassReference.Handle, Set<ClassReference.Handle>>> entrySet() {
        return inheritanceMap.entrySet();
    }

    public Set<ClassReference.Handle> getSuperClasses(ClassReference.Handle clazz) {
        Set<ClassReference.Handle> parents = inheritanceMap.get(clazz);
        if (parents == null) {
            return null;
        }
        return Collections.unmodifiableSet(parents);
    }

    public boolean isSubclassOf(ClassReference.Handle clazz, ClassReference.Handle superClass) {
        Set<ClassReference.Handle> parents = inheritanceMap.get(clazz);
        if (parents == null) {
            return false;
        }
        return parents.contains(superClass);
    }

    public Set<ClassReference.Handle> getSubClasses(ClassReference.Handle clazz) {
        Set<ClassReference.Handle> subClasses = subClassMap.get(clazz);
        if (subClasses == null) {
            return null;
        }
        return Collections.unmodifiableSet(subClasses);
    }
}
