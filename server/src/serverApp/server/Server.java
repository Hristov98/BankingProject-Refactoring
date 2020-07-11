package serverApp.server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Server extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent parent = FXMLLoader.load(getClass().getResource("server.fxml"));
        primaryStage.setTitle("Server interface");
        primaryStage.setScene(new Scene(parent, 800, 450));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}