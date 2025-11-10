package application;

import java.io.File;
import java.io.IOException;

public class virusScanner {

    // Example ClamAV scan integration
    public static boolean scanFile(File file) {
        try {
            ProcessBuilder pb = new ProcessBuilder("clamdscan", "--no-summary", file.getAbsolutePath());
            Process process = pb.start();
            int exitCode = process.waitFor();
            return exitCode != 0; // infected if exit code is non-zero
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
}
