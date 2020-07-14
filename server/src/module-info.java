module server {
    requires javafx.base;
    requires javafx.fxml;
    requires javafx.controls;
    requires utilities;
    requires client;
    requires org.junit.jupiter.api;

    opens serverApp to javafx.fxml;
    exports serverApp to javafx.graphics;
}