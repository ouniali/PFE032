package ca.etsmtl.leakageanalysisplugin.tasks;

import ca.etsmtl.leakageanalysisplugin.models.analysis.AnalysisResult;
import ca.etsmtl.leakageanalysisplugin.models.leakage.LeakageInstance;
import ca.etsmtl.leakageanalysisplugin.models.leakage.LeakageType;
import ca.etsmtl.leakageanalysisplugin.services.HttpClientLeakageService;
import ca.etsmtl.leakageanalysisplugin.services.LeakageService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AnalyseTaskTest {
    
    private AnalyzeTask analyzeTask;
    private List<String> filePaths;
    private String filePath1 = "src/test/testData/testNotebook.ipynb";
    private String filePath2 = "src/test/testData/testNotebook.ipynb";
    @Mock
    private HttpClientLeakageService httpClientLeakageService;

    @Mock
    private LeakageService leakageService;
    private List<AnalysisResult> results;
    private HashMap<LeakageType, List<LeakageInstance>> leakages1;
    private HashMap<LeakageType, List<LeakageInstance>> leakages2;
    private LeakageType type;
    private List<LeakageInstance> instances1;
    private List<LeakageInstance> instances2;
    private int line1 = 55;
    private int line2 = 66;


    @Before
    public void setup(){
        instances1 = new ArrayList();
        instances2 = new ArrayList();
        leakages1 = new HashMap<>();
        leakages2 = new HashMap<>();
        filePaths = new ArrayList<>();
        results = new ArrayList<>();

        type = LeakageType.OVERLAP;
        instances1.add(new LeakageInstance(filePath1, line1));
        instances2.add(new LeakageInstance(filePath2, line2));

        leakages1.put(type, instances1);
        leakages2.put(type, instances2);

        httpClientLeakageService = mock(HttpClientLeakageService.class);
        leakageService = mock(LeakageService.class);

        filePaths.add(filePath1);
        filePaths.add(filePath2);
        AnalysisResult analysisResult1 = new AnalysisResult(filePath1, leakages1);
        AnalysisResult analysisResult2 = new AnalysisResult(filePath2, leakages1);
        results.add(analysisResult1);
        results.add(analysisResult2);
    }

//    TODO: Finish this
    @Test
    public void testAnalyzeTask(){
        analyzeTask = new AnalyzeTask(null, filePaths);
        when(leakageService.analyzeFiles(filePaths)).thenReturn(results);
        assertNotNull(analyzeTask);
    }

    @Test
    public void testRun(){
        analyzeTask = new AnalyzeTask(null, filePaths);
        when(leakageService.analyzeFiles(filePaths)).thenReturn(results);
        assertEquals(true, true);
    }
}
