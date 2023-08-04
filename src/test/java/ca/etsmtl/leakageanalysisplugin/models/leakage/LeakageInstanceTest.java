package ca.etsmtl.leakageanalysisplugin.models.leakage;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LeakageInstanceTest {

    private LeakageInstance leakageInstance;
    private String filePath = "../testData/testNotebook.ipynb";

    @Test
    public void testLeakageInstance(){
        leakageInstance = new LeakageInstance(filePath, 55);

        assertEquals(leakageInstance.filePath, filePath);
        assertEquals(leakageInstance.line, 55);
    }

}
