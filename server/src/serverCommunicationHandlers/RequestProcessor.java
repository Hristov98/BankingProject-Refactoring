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

    public RequestProcessor(Request clientRequest) {
        this.clientRequest = clientRequest;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientName() {
        return clientName;
    }

    public void initialiseUserLoader(UserLoader userLoader) {
        this.userLoader = userLoader;
    }

    public void initialiseOutputStream(ObjectOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void initialiseLogger(ServerMessageLogger logger) {
        this.logger = logger;
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