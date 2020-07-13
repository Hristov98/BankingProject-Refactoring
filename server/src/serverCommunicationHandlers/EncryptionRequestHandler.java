package serverCommunicationHandlers;

import communication.EncryptionRequest;
import communication.Request;
import communication.Response;
import communication.ResponseStatus;
import userStorage.AccessRights;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class EncryptionRequestHandler extends CardRequestHandler {
    public EncryptionRequestHandler(Request clientRequest, ObjectOutputStream outputStream, String clientName) {
        super(clientRequest, outputStream, clientName);
    }

    @Override
    public void processRequest() throws IOException {
        String cardNumber = getCardNumberFromRequest();

        if (requestIsValid(cardNumber)) {
            processValidRequest(cardNumber);
        } else {
            notifyUserForInvalidRequest(cardNumber);
        }
    }

    private boolean requestIsValid(String encryptedNumber) {
        return cardNumberIsValid(encryptedNumber) && userHasValidAccessRights(clientName, AccessRights.ENCRYPTION);
    }

    private void notifyUserForInvalidRequest(String encryptedNumber) throws IOException {
        if (!userHasValidAccessRights(clientName, AccessRights.ENCRYPTION)) {
            notifyClientForInvalidRights("encryption");
        } else {
            notifyClientForInvalidCardNumber(encryptedNumber);
        }
    }

    @Override
    public String getCardNumberFromRequest() {
        return ((EncryptionRequest) clientRequest).getCardNumber()
                .replaceAll(" ", "");
    }

    @Override
    public boolean cardNumberIsValid(String cardNumber) {
        return validator.cardNumberIsValidByLuhn(cardNumber)
                && validator.decryptedCardNumberIsValid(cardNumber);
    }

    @Override
    public void processValidRequest(String cardNumber) throws IOException {
        String encryptedCard = getModifiedCard(cardNumber);
        returnCardNumberToClient(encryptedCard);
        saveCardPairToTable(cardNumber, encryptedCard);
    }

    @Override
    public String getModifiedCard(String cardNumber) {
        return cipher.encryptCardNumber(cardNumber);
    }

    @Override
    public void returnCardNumberToClient(String encryptedNumber) throws IOException {
        Response result = new Response(ResponseStatus.SUCCESS, encryptedNumber);
        sendResponseToClient(result);
    }
}