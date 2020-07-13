package serverCommunicationHandlers;

import communication.DecryptionRequest;
import communication.Request;
import communication.Response;
import communication.ResponseStatus;
import userStorage.AccessRights;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class DecryptionRequestHandler extends CardRequestHandler {
    public DecryptionRequestHandler(Request clientRequest, ObjectOutputStream outputStream, String clientName) {
        super(clientRequest, outputStream, clientName);
    }

    @Override
    public void processRequest() throws IOException {
        String encryptedNumber = getCardNumberFromRequest();

        if (requestIsValid(encryptedNumber)) {
            processValidRequest(encryptedNumber);
        } else {
            notifyUserForInvalidRequest(encryptedNumber);
        }
    }

    private boolean requestIsValid(String encryptedNumber) {
        return cardNumberIsValid(encryptedNumber) && userHasValidAccessRights(clientName, AccessRights.DECRYPTION);
    }

    private void notifyUserForInvalidRequest(String encryptedNumber) throws IOException {
        if (!userHasValidAccessRights(clientName, AccessRights.DECRYPTION)) {
            notifyClientForInvalidRights("decryption");
        } else {
            notifyClientForInvalidCardNumber(encryptedNumber);
        }
    }

    @Override
    public String getCardNumberFromRequest() {
        return ((DecryptionRequest) clientRequest).getCardNumber()
                .replaceAll(" ", "");
    }

    @Override
    public boolean cardNumberIsValid(String cardNumber) {
        return validator.encryptedCardNumberIsValid(cardNumber);
    }

    @Override
    public void processValidRequest(String cardNumber) throws IOException {
        String decryptedNumber = getModifiedCard(cardNumber);
        returnCardNumberToClient(decryptedNumber);
        saveCardPairToTable(decryptedNumber, cardNumber);
    }

    @Override
    public String getModifiedCard(String cardNumber) {
        return cipher.decryptCardNumber(cardNumber);
    }

    @Override
    public void returnCardNumberToClient(String decryptedNumber) throws IOException {
        Response result = new Response(ResponseStatus.SUCCESS, decryptedNumber);
        sendResponseToClient(result);
    }
}
