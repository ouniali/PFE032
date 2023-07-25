package ca.etsmtl.leakageanalysisplugin.models.leakage;

import java.util.Arrays;

public enum LeakageType {
    OVERLAP("overlap leakage"),
    PREPROCESSING("pre-processing leakage"),
    TESTDATA("no independence test data");

    private final String name;

    LeakageType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static LeakageType getLeakageType(String name) {
        return Arrays.stream(LeakageType.values()).filter(l -> l.getName().equals(name)).findFirst().orElse(null);
    }

    @Override
    public String toString() {
        return "LeakageType{" +
                "name='" + name + '\'' +
                '}';
    }
}
