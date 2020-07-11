module server {
    requires javafx.base;
    requires javafx.fxml;
    requires javafx.controls;
    requires utilities;
    requires client;

    opens serverApp.server to javafx.fxml;
    exports serverApp.server to javafx.graphics;
}