package serverCommunicationHandlers;

import communication.LoginRequest;
import communication.Request;
import communication.Response;
import communication.ResponseStatus;
import userStorage.User;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class LoginRequestHandler extends RequestHandler {
    private boolean requestIsSuccessful;

    @Override
    public void processRequest(Request clientRequest, ObjectOutputStream outputStream) throws IOException {
        String username = ((LoginRequest) clientRequest).getUsername();
        String password = ((LoginRequest) clientRequest).getPassword();

        requestIsSuccessful = userExists(username, password);
        if (requestIsSuccessful) {
            returnUsernameToClient(username, outputStream);
        } else {
            notifyClientForFailedLogin(outputStream);
        }
    }

    private boolean userExists(String username, String password) {
        User user = findUserByName(username);

        if (user != null) {
            return password.equals(user.getPassword());
        } else {
            return false;
        }
    }

    private void returnUsernameToClient(String username, ObjectOutputStream outputStream) throws IOException {
        Response result = new Response(ResponseStatus.SUCCESS, username);
        sendResponseToClient(result, outputStream);
    }

    private void notifyClientForFailedLogin(ObjectOutputStream outputStream) throws IOException {
        String errorMessage = "You have entered an incorrect name and/or password.";
        Response result = new Response(ResponseStatus.FAILURE, errorMessage);
        sendResponseToClient(result, outputStream);
    }

    public boolean isRequestSuccessful() {
        return requestIsSuccessful;
    }
}