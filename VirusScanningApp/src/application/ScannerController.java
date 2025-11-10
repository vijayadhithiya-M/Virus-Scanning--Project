package application;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.awt.Desktop;
import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ScannerController {

    @FXML private TableView<FileResult> tableView;
    @FXML private TableColumn<FileResult, String> fileNameColumn;
    @FXML private TableColumn<FileResult, String> statusColumn;
    @FXML private TableColumn<FileResult, String> virusNameColumn;
    @FXML private ProgressBar progressBar;
    @FXML private Label statusLabel;
    @FXML private Button startScanButton;
    @FXML private Button stopScanButton;
    @FXML private Button selectFolderButton;
    @FXML private Button clearCacheButton;
    @FXML private Button quarantineButton;
    @FXML private Button exitButton;

    private File selectedFolder;
    private boolean stopRequested = false;
    private final String QUARANTINE_DIR = "quarantine";
    private final String LOG_FILE = "scan_log.txt";

    @FXML
    public void initialize() {
        fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        virusNameColumn.setCellValueFactory(new PropertyValueFactory<>("virusName"));
        progressBar.setProgress(0);
        statusLabel.setText("Ready to scan");
    }

    @FXML
    private void handleSelectFolder(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Folder to Scan");
        File folder = chooser.showDialog(new Stage());
        if (folder != null) {
            selectedFolder = folder;
            statusLabel.setText("Selected: " + folder.getAbsolutePath());
        }
    }

    @FXML
    private void handleStartScan(ActionEvent event) {
        if (selectedFolder == null) {
            showAlert("Select a folder first.");
            return;
        }

        tableView.getItems().clear();
        stopRequested = false;

        Task<Void> scanTask = new Task<>() {
            @SuppressWarnings("deprecation")
			@Override
            protected Void call() throws Exception {
                List<File> files = FileUtils.listAllFiles(selectedFolder);
                int total = files.size();
                int processed = 0;

                Files.createDirectories(Paths.get(QUARANTINE_DIR));

                for (File file : files) {
                    if (stopRequested) break;

                    String command = "cmd /c clamdscan --fdpass \"" + file.getAbsolutePath() + "\"";
                    Process process = Runtime.getRuntime().exec(command);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    String virusName = "Clean";

                    while ((line = reader.readLine()) != null) {
                        if (line.contains("FOUND")) {
                            virusName = line.substring(line.indexOf(":") + 1, line.indexOf("FOUND")).trim();
                            quarantineFile(file);
                        }
                    }

                    FileResult result = new FileResult(file.getName(),
                            virusName.equals("Clean") ? "Safe" : "Infected",
                            virusName);
                    Platform.runLater(() -> tableView.getItems().add(result));

                    processed++;
                    updateProgress(processed, total);
                    logResult(file, result);
                }

                Platform.runLater(() -> statusLabel.setText(stopRequested ? "Scan stopped." : "Scan completed."));
                return null;
            }
        };

        progressBar.progressProperty().bind(scanTask.progressProperty());
        new Thread(scanTask).start();
        statusLabel.setText("Scanning...");
    }

    @FXML
    private void handleStopScan(ActionEvent event) {
        stopRequested = true;
        statusLabel.setText("Stopping scan...");
    }

    @FXML
    private void handleClearCache(ActionEvent event) {
        try {
            Path cacheDir = Paths.get(QUARANTINE_DIR);
            if (Files.exists(cacheDir)) {
                Files.walk(cacheDir)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
                showAlert("Quarantine cleared successfully!");
            } else {
                showAlert("No quarantine folder found.");
            }
        } catch (Exception e) {
            showAlert("Error clearing quarantine: " + e.getMessage());
        }
    }

    @FXML
    private void handleQuarantine(ActionEvent event) {
        try {
            File folder = new File(QUARANTINE_DIR);
            if (!folder.exists() || Objects.requireNonNull(folder.list()).length == 0) {
                showAlert("No files in quarantine!");
                return;
            }
            Desktop.getDesktop().open(folder);
        } catch (Exception e) {
            showAlert("Unable to open quarantine folder.");
        }
    }

    @FXML
    private void handleExit(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }

    private void quarantineFile(File infectedFile) {
        try {
            Path quarantinePath = Paths.get(QUARANTINE_DIR, infectedFile.getName());
            Files.move(infectedFile.toPath(), quarantinePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            System.err.println("Error moving to quarantine: " + e.getMessage());
        }
    }

    private void logResult(File file, FileResult result) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            writer.write(timeStamp + " | " + file.getName() + " | " + result.getStatus() + " | " + result.getVirusName());
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing log: " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Virus Detection System");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
