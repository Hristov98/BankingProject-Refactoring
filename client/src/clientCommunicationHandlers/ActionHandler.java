package clientCommunicationHandlers;

import communication.Request;
import communication.Response;
import communication.ResponseStatus;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class ActionHandler {
    protected Request requestToServer;
    protected Response response;

    public abstract boolean processResponseFromServer(ObjectInputStream inputStream);

    public void sendRequestToServer(ObjectOutputStream outputStream) throws IOException {
        outputStream.writeObject(requestToServer);
        outputStream.flush();
    }

    protected Response getResponseFromServer(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        return (Response) inputStream.readObject();
    }

    public boolean responseIsSuccessful() {
        return response.getStatus() == ResponseStatus.SUCCESS;
    }

    public String getResponseMessage() {
        return response.getReturnedMessage();
    }
}