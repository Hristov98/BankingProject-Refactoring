package clientCommunicationHandlers;

import clientApp.ClientMessageLogger;
import communication.LoginRequest;

import java.io.IOException;
import java.io.ObjectInputStream;

public class LoginHandler extends ActionHandler {
    public LoginHandler(String username, String password) {
        requestToServer = new LoginRequest(username, password);
    }

    @Override
    public boolean processResponseFromServer(ObjectInputStream inputStream,
                                             ClientMessageLogger logger) {
        try {
            response = getResponseFromServer(inputStream);
            return isResponseValid();
        } catch (IOException ioException) {
            logger.displayMessageOnServer("Input/Output error during client login.");
            ioException.printStackTrace();
        } catch (ClassNotFoundException unknownClassException) {
            logger.displayMessageOnServer("Unknown object received during client login.");
            unknownClassException.printStackTrace();
        }

        return false;
    }
}