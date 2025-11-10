package application;

public class FileResult {
    private String fileName;
    private String status;
    private String virusName;

    public FileResult(String fileName, String status, String virusName) {
        this.fileName = fileName;
        this.status = status;
        this.virusName = virusName;
    }

    public String getFileName() { return fileName; }
    public String getStatus() { return status; }
    public String getVirusName() { return virusName; }
}
