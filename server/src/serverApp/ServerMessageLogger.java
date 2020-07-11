package serverApp;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerMessageLogger {
    private final TextArea textAreaLog;

    public ServerMessageLogger(TextArea textAreaLog) {
        this.textAreaLog = textAreaLog;
    }

    public void displayMessage(String message) {
        String timeStampedMessage = String.format("%s\t%s\n",
                new SimpleDateFormat("HH:mm:ss").format(new Date()), message);

        Platform.runLater(() -> textAreaLog.appendText(timeStampedMessage));
    }
}