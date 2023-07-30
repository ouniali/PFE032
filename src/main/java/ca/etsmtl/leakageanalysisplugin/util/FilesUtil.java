package ca.etsmtl.leakageanalysisplugin.util;

import java.io.File;
import java.util.Arrays;

public class FilesUtil {

    public static final String[] SUPPORTED_EXTS = {"ipynb"};

    public static boolean isFileSupported(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        }
        String extension = file.getPath().substring(file.getPath().lastIndexOf(".") + 1);
        return isExtensionSupported(extension);
    }

    public static boolean isExtensionSupported(String extension) {
        return Arrays.asList(SUPPORTED_EXTS).contains(extension);
    }

    public static String getFileName(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        return file.getName();
    }
}
