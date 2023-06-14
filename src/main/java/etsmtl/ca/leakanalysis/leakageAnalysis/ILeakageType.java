package etsmtl.ca.leakanalysis.leakageAnalysis;

public abstract class ILeakageType
{
    public boolean analyze(String[] files)
    {
        boolean result = false;

        for (String file: files) {
            result |= analyze(file);
        }

        return result;
    }

    public abstract boolean analyze(String file);
}
