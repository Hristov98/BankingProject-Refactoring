package clientCommunicationHandlers;

import clientApp.ClientMessageLogger;
import communication.DecryptionRequest;

import java.io.IOException;
import java.io.ObjectInputStream;

public class DecryptionHandler extends ActionHandler {
    public DecryptionHandler(String cardNumber) {
        requestToServer = new DecryptionRequest(cardNumber);
    }

    @Override
    public boolean processResponseFromServer(ObjectInputStream inputStream,
                                             ClientMessageLogger logger) {
        try {
            response = getResponseFromServer(inputStream);
            return isResponseValid();
        } catch (IOException ioException) {
            logger.displayMessageOnServer("Input/Output error during card decryption.");
            ioException.printStackTrace();
        } catch (ClassNotFoundException unknownClassException) {
            logger.displayMessageOnServer("Unknown object received during card decryption.");
            unknownClassException.printStackTrace();
        }

        return false;
    }
}