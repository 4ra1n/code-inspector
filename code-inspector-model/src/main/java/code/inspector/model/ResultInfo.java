package code.inspector.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ResultInfo {
    private String vulName;
    private String type;
    private final List<String> chains = new ArrayList<>();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVulName() {
        return vulName;
    }

    public void setVulName(String vulName) {
        this.vulName = vulName;
    }

    public List<String> getChains() {
        return chains;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.vulName);
        sb.append("\n");
        for (String s : chains) {
            sb.append("\t");
            sb.append(s);
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResultInfo that = (ResultInfo) o;
        boolean first = Objects.equals(vulName, that.vulName);
        boolean second = Objects.equals(type, that.type);
        boolean third = true;
        if (chains.size() == that.chains.size()) {
            for (int i = 0; i < chains.size(); i++) {
                if (!chains.get(i).equals(that.chains.get(i))) {
                    third = false;
                    break;
                }
            }
            return first & second & third;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(vulName, type, chains);
    }
}
