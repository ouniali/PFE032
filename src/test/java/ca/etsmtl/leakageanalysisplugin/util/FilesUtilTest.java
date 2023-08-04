package ca.etsmtl.leakageanalysisplugin.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FilesUtilTest {

    private String filePath = "src/test/testData/testNotebook.ipynb";

    @Test
    public void testIsFileSupported(){
        assertEquals(true, FilesUtil.isFileSupported(filePath));
    }

    @Test
    public void testIsExtensionSupported(){
        assertEquals(true, FilesUtil.isExtensionSupported("ipynb"));
    }

    @Test
    public void testGetFileName(){
        assertEquals("testNotebook.ipynb", FilesUtil.getFileName(filePath));
    }
}
