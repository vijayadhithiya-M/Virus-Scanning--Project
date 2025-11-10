package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ScannerService {

    public List<FileResult> scanFolder(File folder) {
        List<FileResult> results = new ArrayList<>();

        if (folder == null || !folder.exists()) {
            return results;
        }

        File[] files = folder.listFiles();
        if (files == null) return results;

        try {
            for (File file : files) {
                if (file.isDirectory()) {
                    results.addAll(scanFolder(file)); // recursive scan for subfolders
                } else {
                    String virusName = "None";
                    String status = "Safe";

                    // ✅ Use real ClamAV scan command
                    String cmd = "cmd /c \"C:\\Program Files\\ClamAV\\clamdscan.exe\" --fdpass \"" + file.getAbsolutePath() + "\"";
                    @SuppressWarnings("deprecation")
					Process process = Runtime.getRuntime().exec(cmd);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                    String line;
                    boolean infected = false;

                    while ((line = reader.readLine()) != null) {
                        if (line.contains("FOUND")) {
                            infected = true;
                            virusName = line.substring(line.indexOf(":") + 1, line.indexOf("FOUND")).trim();
                        }
                    }

                    process.waitFor();

                    if (infected) {
                        status = "Infected";
                    }

                    results.add(new FileResult(file.getAbsolutePath(), virusName, status));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return results;
    }
}
