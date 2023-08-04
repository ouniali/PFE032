package ca.etsmtl.leakageanalysisplugin.models.analysis;

import ca.etsmtl.leakageanalysisplugin.models.leakage.LeakageInstance;
import ca.etsmtl.leakageanalysisplugin.models.leakage.LeakageType;
import ca.etsmtl.leakageanalysisplugin.services.HttpClientLeakageService;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class AnalysisResultTest {

    private AnalysisResult analysisResult;
    private HashMap<LeakageType, List<LeakageInstance>> leakages;
    private LeakageType type;
    private List<LeakageInstance> instances;
    private String filePath = "../testData/testNotebook.ipynb";
    private List<Exception> errors;
    private String analysisToString;

    @Before
    public void setup(){
        instances = new ArrayList();
        leakages = new HashMap<>();
        errors = new ArrayList<>();

        type = LeakageType.OVERLAP;
        instances.add(new LeakageInstance(filePath, 55));
        leakages.put(type, instances);
        errors.add(new Exception("error"));
        analysisToString = "AnalysisResult{" +
                "filePath='" + filePath + '\'' +
                ", leakages=" + leakages +
                ", status=" + AnalysisStatus.SUCCESS +
                ", errors=" + null +
                '}';
    }

    @Test
    public void testAnalysisSuccess(){
        analysisResult = new AnalysisResult(filePath, leakages);

        assertEquals(analysisResult.getFilePath(), filePath);
        assertEquals(analysisResult.getLeakages(type), leakages.get(type));
        assertEquals(analysisResult.getStatus(), AnalysisStatus.SUCCESS);
        assertEquals(true, analysisResult.isSuccessful());
    }

    @Test
    public void testAnalysisFailure(){
        analysisResult = new AnalysisResult(filePath, errors);

        assertEquals(analysisResult.getFilePath(), filePath);
        assertEquals(analysisResult.getErrors(), errors);
        assertEquals(analysisResult.getStatus(), AnalysisStatus.FAILED);
        assertEquals(false, analysisResult.isSuccessful());
    }

    @Test
    public void testAnalysisToString(){
        analysisResult = new AnalysisResult(filePath, leakages);

        assertEquals(analysisToString, analysisResult.toString());
    }
}
