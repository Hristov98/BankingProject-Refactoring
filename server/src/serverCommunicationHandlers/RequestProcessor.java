package serverCommunicationHandlers;

import communication.Request;
import communication.Response;
import serverApp.ServerMessageLogger;
import userStorage.User;
import userStorage.UserLoader;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashSet;

public abstract class RequestProcessor {
    protected Request clientRequest;
    protected String clientName;
    protected UserLoader userLoader;
    private ObjectOutputStream outputStream;
    protected ServerMessageLogger logger;

    public RequestProcessor(Request clientRequest, UserLoader userLoader,
                            ObjectOutputStream outputStream, ServerMessageLogger logger, String clientName) {
        this.clientRequest = clientRequest;
        this.userLoader = userLoader;
        this.outputStream = outputStream;
        this.logger = logger;
        setClientName(clientName);
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientName() {
        return clientName;
    }

    public abstract void processRequest() throws IOException;

    protected User findUserByName(String username) {
        HashSet<User> users = userLoader.getRegisteredUsers().getUsers();

        for (User user : users) {
            if (username.equals(user.getUsername())) {
                return user;
            }
        }

        return null;
    }

    protected void sendResponseToClient(Response response) throws IOException {
        outputStream.writeObject(response);
        outputStream.flush();
    }
}