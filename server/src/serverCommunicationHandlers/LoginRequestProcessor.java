package serverCommunicationHandlers;

import communication.LoginRequest;
import communication.Request;
import communication.Response;
import communication.ResponseStatus;
import serverApp.ServerMessageLogger;
import userStorage.User;
import userStorage.UserLoader;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class LoginRequestProcessor extends RequestProcessor {
    private boolean successfulRequest;

    public LoginRequestProcessor(Request clientRequest, UserLoader userLoader,
                                 ObjectOutputStream outputStream, ServerMessageLogger logger, String clientName) {
        super(clientRequest, userLoader, outputStream, logger, clientName);
    }

    @Override
    public void processRequest() throws IOException {
        String username = ((LoginRequest) clientRequest).getUsername();
        String password = ((LoginRequest) clientRequest).getPassword();

        if (userExists(username, password)) {
            notifyClientForSuccessfulLogin(username);
        } else {
            notifyClientForFailedLogin(username);
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
        logger.displayMessage(String.format("Logging in user %s.", username));
        setClientName(username);

        Response result = new Response(ResponseStatus.SUCCESS, username);
        sendResponseToClient(result);

        successfulRequest = true;
    }

    private void notifyClientForFailedLogin(String username) throws IOException {
        logger.displayMessage(String.format("User %s could not be found.", username));
        String errorMessage = "You have entered an incorrect name and/or password.";

        Response result = new Response(ResponseStatus.FAILURE, errorMessage);
        sendResponseToClient(result);

        successfulRequest = false;
    }

    public boolean isSuccessfulRequest() {
        return successfulRequest;
    }
}