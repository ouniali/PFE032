package ca.etsmtl.leakageanalysisplugin.models.leakage;

import java.util.List;

public class Leakage {

    private LeakageType type;
    private List<Integer> locations;
    private int count;

    public Leakage(LeakageType type, List<Integer> locations, int count) {
        this.type = type;
        this.locations = locations;
        this.count = count;
    }

    public List<Integer> getLocations() {
        return locations;
    }

    public void setLocations(List<Integer> locations) {
        this.locations = locations;
    }

    public LeakageType getType() {
        return type;
    }

    public void setType(LeakageType type) {
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "Leakage{" +
                "type=" + type +
                ", locations=" + locations +
                ", detected=" + count +
                '}';
    }
}
