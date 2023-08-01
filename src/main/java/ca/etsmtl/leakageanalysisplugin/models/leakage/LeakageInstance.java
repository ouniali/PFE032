package ca.etsmtl.leakageanalysisplugin.models.leakage;

public class LeakageInstance {
    public String filePath;
    public int line;

    public LeakageInstance(String filePath, int line)
    {
        this.filePath = filePath;
        this.line = line;
    }
}
