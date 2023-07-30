package ca.etsmtl.leakageanalysisplugin.models.leakage;

import java.util.Arrays;

public enum LeakageType {
    OVERLAP("Overlap Leakage", "overlap leakage"),
    PREPROCESSING("Preprocessing Leakage", "pre-processing leakage"),
    MULTITEST("Multi-Test Leakage", "no independence test data");

    private final String name;
    private final String key;

    LeakageType(String name, String key) {
        this.name = name;
        this.key = key;
    }

    public String getName() { return name; }
    public String getKey() {
        return key;
    }

    public static LeakageType getLeakageType(String key) {
        return Arrays.stream(LeakageType.values())
                .filter(l -> l.getKey().equals(key))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String toString() {
        return "LeakageType{" +
                "name='" + key + '\'' +
                '}';
    }
}
