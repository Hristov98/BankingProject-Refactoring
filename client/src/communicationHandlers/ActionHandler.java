package communicationHandlers;

import clientApp.MessageLogger;
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

    public abstract boolean processResponseFromServer(ObjectInputStream inputStream, MessageLogger logger);

    protected Response getResponseFromServer(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        return (Response) inputStream.readObject();
    }

    protected boolean isResponseValid(Response response, RequestType type) {
        if (isCorrectType(response, type) && isSuccessful(response)) {
            return true;
        } else {
            alertUserForFailedAction(getResponseMessage());
            return false;
        }
    }

    private boolean isCorrectType(Response response, RequestType type) {
        return response.getType() == type;
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
