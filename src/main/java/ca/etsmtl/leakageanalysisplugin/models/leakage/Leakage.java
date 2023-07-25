package ca.etsmtl.leakageanalysisplugin.models.leakage;

import java.util.List;

public class Leakage {

    private LeakageType type;
    private List<Integer> locations;
    private int detected;

    public Leakage(LeakageType type, List<Integer> locations, int detected) {
        this.type = type;
        this.locations = locations;
        this.detected = detected;
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

    public int getDetected() {
        return detected;
    }

    public void setDetected(int detected) {
        this.detected = detected;
    }

    @Override
    public String toString() {
        return "Leakage{" +
                "type=" + type +
                ", locations=" + locations +
                ", detected=" + detected +
                '}';
    }
}
