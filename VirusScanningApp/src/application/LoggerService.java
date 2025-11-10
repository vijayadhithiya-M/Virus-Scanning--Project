package application;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggerService {

    private static final String LOG_FILE = "scan_log.csv";
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    public LoggerService() {
        try {
            File file = new File(LOG_FILE);
            if (!file.exists()) {
                try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file, true)))) {
                    pw.println("File Path,Virus Name,Status,Scan DateTime,Time Taken(s)");
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void log(String filePath, String virusName, String status, long timeTakenSeconds) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(LOG_FILE, true)))) {
            String dateTime = dtf.format(LocalDateTime.now());
            pw.println(filePath + "," + (virusName.isEmpty() ? "Clean" : virusName) + "," + status + "," + dateTime + "," + timeTakenSeconds);
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void clearLog() {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(LOG_FILE)))) {
            pw.println("File Path,Virus Name,Status,Scan DateTime,Time Taken(s)");
        } catch (IOException e) { e.printStackTrace(); }
    }
}
