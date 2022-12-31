package code.inspector.model;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ClassReference {
    private final String name;
    private final String superClass;
    private final List<String> interfaces;
    private final boolean isInterface;
    private final List<Member> members;
    private final Set<String> annotations;

    public static class Member {
        private final String name;
        private final int modifiers;
        private final Handle type;

        public Member(String name, int modifiers, Handle type) {
            this.name = name;
            this.modifiers = modifiers;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public int getModifiers() {
            return modifiers;
        }

        public Handle getType() {
            return type;
        }
    }

    public ClassReference(String name, String superClass, List<String> interfaces,
                          boolean isInterface, List<Member> members, Set<String> annotations) {
        this.name = name;
        this.superClass = superClass;
        this.interfaces = interfaces;
        this.isInterface = isInterface;
        this.members = members;
        this.annotations = annotations;
    }

    public String getName() {
        return name;
    }

    public String getSuperClass() {
        return superClass;
    }

    public List<String> getInterfaces() {
        return interfaces;
    }

    public boolean isInterface() {
        return isInterface;
    }

    public List<Member> getMembers() {
        return members;
    }

    public Handle getHandle() {
        return new Handle(name);
    }

    public Set<String> getAnnotations() {
        return annotations;
    }

    public static class Handle {
        private final String name;

        public Handle(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Handle handle = (Handle) o;
            return Objects.equals(name, handle.name);
        }

        @Override
        public int hashCode() {
            return name != null ? name.hashCode() : 0;
        }
    }
}
