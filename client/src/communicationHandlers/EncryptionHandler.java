package communicationHandlers;

import clientApp.MessageLogger;
import communication.EncryptionRequest;
import communication.RequestType;

import java.io.IOException;
import java.io.ObjectInputStream;

public class EncryptionHandler extends ActionHandler {
    public EncryptionHandler(String cardNumber) {
        requestToServer = new EncryptionRequest(cardNumber);
    }

    @Override
    public boolean processResponseFromServer(ObjectInputStream inputStream, MessageLogger logger) {
        try {
            response = getResponseFromServer(inputStream);
            return isResponseValid(response, RequestType.ENCRYPTION);
        } catch (IOException ioException) {
            logger.displayMessageOnServer("Input/Output error during card encryption.");
            ioException.printStackTrace();
        } catch (ClassNotFoundException unknownClassException) {
            logger.displayMessageOnServer("Unknown object received during card encryption.");
            unknownClassException.printStackTrace();
        }
        return false;
    }
}
