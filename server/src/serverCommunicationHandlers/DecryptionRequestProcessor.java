package serverCommunicationHandlers;

import communication.DecryptionRequest;
import communication.Request;
import communication.Response;
import communication.ResponseStatus;
import userStorage.AccessRights;

import java.io.IOException;

public class DecryptionRequestProcessor extends CardRequestProcessor {
    public DecryptionRequestProcessor(Request request) {
        super(request);
    }

    @Override
    public void processRequest() throws IOException {
        String encryptedNumber = getCardNumberFromRequest();

        if (!cardNumberIsValid(encryptedNumber)) {
            notifyClientForInvalidCardNumber(encryptedNumber);
        } else if (!userHasValidAccessRights(clientName, AccessRights.DECRYPTION)) {
            notifyClientForInvalidRights("decryption");
        } else {
            processSuccessfulRequest(encryptedNumber);
        }
    }

    @Override
    public String getCardNumberFromRequest() {
        return ((DecryptionRequest) clientRequest).getCardNumber().replaceAll(" ", "");
    }

    @Override
    public boolean cardNumberIsValid(String cardNumber) {
        return validator.validationByRegexEncrypted(cardNumber);
    }

    @Override
    public void processSuccessfulRequest(String cardNumber) throws IOException {
        String decryptedNumber = getModifiedCard(cardNumber);
        returnCardNumberToClient(decryptedNumber);
        saveCardPairToTable(decryptedNumber, cardNumber);
    }

    @Override
    public String getModifiedCard(String cardNumber) {
        return cipher.decrypt(cardNumber);
    }

    @Override
    public void returnCardNumberToClient(String decryptedNumber) throws IOException {
        logger.displayMessage(String.format("Sending %s back to %s", decryptedNumber, clientName));
        Response result = new Response(ResponseStatus.SUCCESS, decryptedNumber);
        sendResponseToClient(result);
    }
}
