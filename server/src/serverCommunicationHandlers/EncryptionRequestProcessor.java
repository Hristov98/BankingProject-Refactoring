package serverCommunicationHandlers;

import communication.EncryptionRequest;
import communication.Request;
import communication.Response;
import communication.ResponseStatus;
import userStorage.AccessRights;

import java.io.IOException;

public class EncryptionRequestProcessor extends CardRequestProcessor {
    public EncryptionRequestProcessor(Request request) {
        super(request);
    }

    @Override
    public void processRequest() throws IOException {
        String cardNumber = getCardNumberFromRequest();

        if (!cardNumberIsValid(cardNumber)) {
            notifyClientForInvalidCardNumber(cardNumber);
        } else if (!userHasValidAccessRights(clientName, AccessRights.ENCRYPTION)) {
            notifyClientForInvalidRights("encryption");
        } else {
            processSuccessfulRequest(cardNumber);
        }
    }

    @Override
    public String getCardNumberFromRequest() {
        return ((EncryptionRequest) clientRequest).getCardNumber().replaceAll(" ", "");
    }

    @Override
    public boolean cardNumberIsValid(String cardNumber) {
        return validator.validationByLuhn(cardNumber) && validator.validationByRegexDecrypted(cardNumber);
    }

    @Override
    public void processSuccessfulRequest(String cardNumber) throws IOException {
        String encryptedCard = getModifiedCard(cardNumber);
        returnCardNumberToClient(encryptedCard);
        saveCardPairToTable(cardNumber, encryptedCard);
    }

    @Override
    public String getModifiedCard(String cardNumber) {
        return cipher.encrypt(cardNumber);
    }

    @Override
    public void returnCardNumberToClient(String encryptedNumber) throws IOException {
        logger.displayMessage(String.format("Sending %s back to %s", encryptedNumber, clientName));
        Response result = new Response(ResponseStatus.SUCCESS, encryptedNumber);
        sendResponseToClient(result);
    }
}