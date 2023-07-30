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

    public LeakageType getType() {
        return type;
    }

    public int getCount() {
        return count;
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
