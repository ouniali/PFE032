package ca.etsmtl.leakageanalysisplugin.util;

import java.io.File;
import java.util.Arrays;

public class AnalysisUtil {

    public static final String[] SUPPORTED_EXTS = {"ipynb"};

    public static boolean isFileSupported(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        }
        String ext = file.getPath().substring(file.getPath().lastIndexOf(".") + 1);
        return Arrays.asList(SUPPORTED_EXTS).contains(ext);
    }
}
