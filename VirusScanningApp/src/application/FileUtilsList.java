package application;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtilsList {
    public static List<File> listFiles(File dir) {
        List<File> list = new ArrayList<>();
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) list.addAll(listFiles(f));
                else list.add(f);
            }
        }
        return list;
    }
}
