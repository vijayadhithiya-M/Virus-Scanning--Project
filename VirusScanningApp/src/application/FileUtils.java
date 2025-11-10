package application;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    public static List<File> listAllFiles(File folder) {
        List<File> fileList = new ArrayList<>();
        if (folder.isFile()) fileList.add(folder);
        else {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    fileList.addAll(listAllFiles(file));
                }
            }
        }
        return fileList;
    }
}
