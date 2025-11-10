package application;

import java.awt.Desktop;
import java.io.File;

public class QuarantineManager {

    private static final String QUARANTINE_PATH = "C:\\VirusQuarantine";

    public static void openQuarantineFolder() {
        try {
            File folder = new File(QUARANTINE_PATH);
            if (!folder.exists()) folder.mkdirs();
            Desktop.getDesktop().open(folder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
