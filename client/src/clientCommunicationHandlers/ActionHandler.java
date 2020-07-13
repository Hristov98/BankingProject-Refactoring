package clientCommunicationHandlers;

import clientApp.ClientMessageLogger;
import communication.Request;
import communication.Response;
import communication.ResponseStatus;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class ActionHandler {
    protected Request requestToServer;
    protected Response response;

    public abstract boolean processResponseFromServer(ObjectInputStream inputStream, ClientMessageLogger logger);

    public void sendRequestToServer(ObjectOutputStream outputStream) throws IOException {
        outputStream.writeObject(requestToServer);
        outputStream.flush();
    }

    protected Response getResponseFromServer(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        return (Response) inputStream.readObject();
    }

    protected boolean isResponseValid() {
        if (isSuccessful()) {
            return true;
        } else {
            alertUserForFailedAction(getResponseMessage());
            return false;
        }
    }

    private boolean isSuccessful() {
        return response.getStatus() == ResponseStatus.SUCCESS;
    }

    private void alertUserForFailedAction(String failureMessage) {
        Alert failedLoginAlert = new Alert(Alert.AlertType.ERROR);
        failedLoginAlert.setTitle("Error window");
        failedLoginAlert.setHeaderText(failureMessage);
        failedLoginAlert.show();
    }

    public String getResponseMessage() {
        return response.getReturnedMessage();
    }
}