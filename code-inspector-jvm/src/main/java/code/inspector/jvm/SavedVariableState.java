package code.inspector.jvm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SavedVariableState<T> {
    List<Set<T>> localVars;
    List<Set<T>> stackVars;

    public SavedVariableState() {
        localVars = new ArrayList<>();
        stackVars = new ArrayList<>();
    }

    public SavedVariableState(SavedVariableState<T> copy) {
        this.localVars = new ArrayList<>(copy.localVars.size());
        this.stackVars = new ArrayList<>(copy.stackVars.size());

        for (Set<T> original : copy.localVars) {
            this.localVars.add(new HashSet<>(original));
        }
        for (Set<T> original : copy.stackVars) {
            this.stackVars.add(new HashSet<>(original));
        }
    }

    public void combine(SavedVariableState<T> copy) {
        for (int i = 0; i < copy.localVars.size(); i++) {
            while (i >= this.localVars.size()) {
                this.localVars.add(new HashSet<T>());
            }
            this.localVars.get(i).addAll(copy.localVars.get(i));
        }
        for (int i = 0; i < copy.stackVars.size(); i++) {
            while (i >= this.stackVars.size()) {
                this.stackVars.add(new HashSet<T>());
            }
            this.stackVars.get(i).addAll(copy.stackVars.get(i));
        }
    }
}