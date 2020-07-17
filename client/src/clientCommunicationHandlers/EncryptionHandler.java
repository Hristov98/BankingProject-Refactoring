package clientCommunicationHandlers;

import communication.CardActionRequest;
import communication.RequestType;

import java.io.IOException;
import java.io.ObjectInputStream;

public class EncryptionHandler extends ActionHandler {
    public EncryptionHandler(String cardNumber, String userSendingRequest) {
        requestToServer = new CardActionRequest(RequestType.ENCRYPTION, cardNumber, userSendingRequest);
    }

    @Override
    public boolean processResponseFromServer(ObjectInputStream inputStream) {
        try {
            response = getResponseFromServer(inputStream);
            return responseIsSuccessful();
        } catch (IOException ioException) {
            System.err.println("Input/Output error during card encryption.");
            ioException.printStackTrace();
        } catch (ClassNotFoundException unknownClassException) {
            System.err.println("Unknown object received during card encryption.");
            unknownClassException.printStackTrace();
        }

        return false;
    }
}