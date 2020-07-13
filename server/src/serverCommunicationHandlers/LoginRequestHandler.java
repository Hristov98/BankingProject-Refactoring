package serverCommunicationHandlers;

import communication.LoginRequest;
import communication.Request;
import communication.Response;
import communication.ResponseStatus;
import userStorage.User;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class LoginRequestHandler extends RequestHandler {
    private boolean successfulRequest;

    public LoginRequestHandler(Request clientRequest, ObjectOutputStream outputStream,
                               String clientName) {
        super(clientRequest, outputStream, clientName);
    }

    @Override
    public void processRequest() throws IOException {
        String username = ((LoginRequest) clientRequest).getUsername();
        String password = ((LoginRequest) clientRequest).getPassword();

        if (userExists(username, password)) {
            notifyClientForSuccessfulLogin(username);
        } else {
            notifyClientForFailedLogin();
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

    private void notifyClientForSuccessfulLogin(String username) throws IOException {
        setClientName(username);
        Response result = new Response(ResponseStatus.SUCCESS, username);
        sendResponseToClient(result);

        successfulRequest = true;
    }

    private void notifyClientForFailedLogin() throws IOException {
        String errorMessage = "You have entered an incorrect name and/or password.";
        Response result = new Response(ResponseStatus.FAILURE, errorMessage);
        sendResponseToClient(result);

        successfulRequest = false;
    }

    public boolean isSuccessfulRequest() {
        return successfulRequest;
    }
}