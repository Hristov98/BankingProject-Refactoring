package communicationHandlers;

import clientApp.MessageLogger;
import communication.DecryptionRequest;
import communication.RequestType;

import java.io.IOException;
import java.io.ObjectInputStream;

public class DecryptionHandler extends ActionHandler {
    public DecryptionHandler(String cardNumber) {
        requestToServer = new DecryptionRequest(cardNumber);
    }

    @Override
    public boolean processResponseFromServer(ObjectInputStream inputStream, MessageLogger logger) {
        try {
            response = getResponseFromServer(inputStream);
            return isResponseValid(response, RequestType.DECRYPTION);
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
