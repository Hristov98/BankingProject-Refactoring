module client {
    requires javafx.controls;
    requires javafx.fxml;
    requires utilities;

    opens clientApp.client to javafx.fxml;
    exports clientApp.client to javafx.graphics;
}