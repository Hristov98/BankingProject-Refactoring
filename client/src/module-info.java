module client {
    requires javafx.controls;
    requires javafx.fxml;
    requires utilities;

    opens clientApp to javafx.fxml;
    exports clientApp to javafx.graphics;
}