package communicationHandlers;

import clientApp.ClientMessageLogger;
import communication.Request;
import communication.RequestType;
import communication.Response;
import communication.ResponseStatus;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class ActionHandler {
    protected Request requestToServer;
    protected Response response;

    public String getResponseMessage() {
        return response.getReturnedMessage();
    }

    public void sendRequestToServer(ObjectOutputStream outputStream) throws IOException {
        outputStream.writeObject(requestToServer);
        outputStream.flush();
    }

    public abstract boolean processResponseFromServer(ObjectInputStream inputStream, ClientMessageLogger logger);

    protected Response getResponseFromServer(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        return (Response) inputStream.readObject();
    }

    protected boolean isResponseValid(Response response) {
        if (isSuccessful(response)) {
            return true;
        } else {
            alertUserForFailedAction(getResponseMessage());
            return false;
        }
    }

    private boolean isSuccessful(Response response) {
        return response.getStatus() == ResponseStatus.SUCCESS;
    }

    private void alertUserForFailedAction(String failureMessage) {
        Alert failedLoginAlert = new Alert(Alert.AlertType.ERROR);
        failedLoginAlert.setTitle("Error window");
        failedLoginAlert.setHeaderText(failureMessage);
        failedLoginAlert.show();
    }
}