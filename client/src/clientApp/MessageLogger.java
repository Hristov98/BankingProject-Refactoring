package clientApp;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class MessageLogger {
    ObjectOutputStream outputStream;

    MessageLogger(ObjectOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void displayMessageOnServer(String message) {
        try {
            sendMessageToServer(message);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void sendMessageToServer(String message) throws IOException {
        outputStream.writeObject(message);
        outputStream.flush();
    }
}
