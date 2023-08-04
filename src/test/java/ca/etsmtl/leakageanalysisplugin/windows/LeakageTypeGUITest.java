package ca.etsmtl.leakageanalysisplugin.windows;

import ca.etsmtl.leakageanalysisplugin.models.leakage.LeakageInstance;
import ca.etsmtl.leakageanalysisplugin.models.leakage.LeakageType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LeakageTypeGUITest {

    private LeakageTypeGUI leakageTypeGUI;
    private List<LeakageInstance> instances;
    private String filePath1 = "src/test/testData/testNotebook.ipynb";
    private String filePath2 = "src/test/testData/testNotebook.ipynb";
    private int line1 = 55;
    private int line2 = 66;

    @Test
    public void testLeakageTypeGUI(){
        leakageTypeGUI = new LeakageTypeGUI(LeakageType.OVERLAP);
        assertEquals(LeakageType.OVERLAP, leakageTypeGUI.getType());
        assertNotNull(leakageTypeGUI.getMainPanel());
        assertNotNull(leakageTypeGUI.leakageInstancesPanel);
    }

    @Test
    public void testUpdate(){
        leakageTypeGUI = new LeakageTypeGUI(LeakageType.OVERLAP);
        instances = new ArrayList<>();
        LeakageInstance leakage1 = new LeakageInstance(filePath1, line1);
        LeakageInstance leakage2 = new LeakageInstance(filePath2, line2);
        instances.add(leakage1);
        instances.add(leakage2);
        leakageTypeGUI.update(instances);
        assertEquals(2, leakageTypeGUI.leakageInstancesPanel.getComponentCount());
    }

    @Test
    public void testReset(){
        leakageTypeGUI = new LeakageTypeGUI(LeakageType.OVERLAP);
        assertEquals(0, leakageTypeGUI.leakageInstancesPanel.getComponentCount());
    }
}
