package serverCommunicationHandlers;

import communication.Request;
import communication.Response;
import userStorage.User;
import userStorage.UserController;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public abstract class RequestHandler {
    protected final Request clientRequest;
    protected final UserController userController;
    protected String clientName;

    public RequestHandler(Request clientRequest, String clientName) {
        this.clientRequest = clientRequest;
        setClientName(clientName);

        userController = new UserController("users.ser");
        userController.loadUsers();
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientName() {
        return clientName;
    }

    public abstract void processRequest(ObjectOutputStream outputStream) throws IOException;

    protected User findUserByName(String username) {
        ArrayList<User> users = userController.getRegisteredUsers().getUsers();

        for (User user : users) {
            if (username.equals(user.getUsername())) {
                return user;
            }
        }

        return null;
    }

    protected void sendResponseToClient(Response response, ObjectOutputStream outputStream) throws IOException {
        outputStream.writeObject(response);
        outputStream.flush();
    }
}