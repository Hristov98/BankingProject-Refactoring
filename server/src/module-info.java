module server {
    requires javafx.base;
    requires javafx.fxml;
    requires javafx.controls;
    requires utilities;
    requires client;

    opens serverApp to javafx.fxml;
    exports serverApp to javafx.graphics;
}