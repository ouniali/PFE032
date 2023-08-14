package ca.etsmtl.leakageanalysisplugin.models.leakage;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LeakageInstanceTest {

    private LeakageInstance leakageInstance;
    private String filePath = "src/test/testData/testNotebook.ipynb";
    private int line = 55;

    @Test
    public void testLeakageInstance(){
        leakageInstance = new LeakageInstance(filePath, line);

        assertEquals(leakageInstance.filePath, filePath);
        assertEquals(leakageInstance.line, line);
    }
}
