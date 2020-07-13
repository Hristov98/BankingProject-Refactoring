package serverCommunicationHandlers;

import communication.Request;
import communication.Response;
import userStorage.User;
import userStorage.UserController;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashSet;

public abstract class RequestHandler {
    private final ObjectOutputStream outputStream;
    protected final Request clientRequest;
    protected final UserController userController;
    protected String clientName;

    public RequestHandler(Request clientRequest, ObjectOutputStream outputStream, String clientName) {
        this.clientRequest = clientRequest;
        this.outputStream = outputStream;
        setClientName(clientName);

        userController = new UserController();
        userController.loadUsers();
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientName() {
        return clientName;
    }

    public abstract void processRequest() throws IOException;

    protected User findUserByName(String username) {
        HashSet<User> users = userController.getRegisteredUsers().getUsers();

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