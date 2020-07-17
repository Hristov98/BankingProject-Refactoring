package clientCommunicationHandlers;

import communication.DecryptionRequest;

import java.io.IOException;
import java.io.ObjectInputStream;

public class DecryptionHandler extends ActionHandler {
    public DecryptionHandler(String cardNumber, String userSendingRequest) {
        requestToServer = new DecryptionRequest(cardNumber, userSendingRequest);
    }

    @Override
    public boolean processResponseFromServer(ObjectInputStream inputStream) {
        try {
            response = getResponseFromServer(inputStream);
            return responseIsSuccessful();
        } catch (IOException ioException) {
            System.err.println("Input/Output error during card decryption.");
            ioException.printStackTrace();
        } catch (ClassNotFoundException unknownClassException) {
            System.err.println("Unknown object received during card decryption.");
            unknownClassException.printStackTrace();
        }

        return false;
    }
}