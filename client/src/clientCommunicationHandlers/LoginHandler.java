package clientCommunicationHandlers;

import communication.LoginRequest;

import java.io.IOException;
import java.io.ObjectInputStream;

public class LoginHandler extends ActionHandler {
    public LoginHandler(String username, String password) {
        requestToServer = new LoginRequest(username, password);
    }

    @Override
    public boolean processResponseFromServer(ObjectInputStream inputStream) {
        try {
            response = getResponseFromServer(inputStream);
            return responseIsSuccessful();
        } catch (IOException ioException) {
            System.err.println("Input/Output error during client login.");
            ioException.printStackTrace();
        } catch (ClassNotFoundException unknownClassException) {
            System.err.println("Unknown object received during client login.");
            unknownClassException.printStackTrace();
        }

        return false;
    }
}