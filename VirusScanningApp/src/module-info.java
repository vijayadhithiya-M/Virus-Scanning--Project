module VirusScanningApp {
    requires javafx.controls;
    requires javafx.fxml;
	requires javafx.graphics;
	requires java.desktop;

    opens application to javafx.fxml;
    exports application;
}
