package serverCommunicationHandlers;

import communication.*;
import userStorage.AccessRights;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class EncryptionRequestHandler extends CardRequestHandler {
    @Override
    public void processRequest(Request clientRequest, ObjectOutputStream outputStream) throws IOException {
        String cardNumber = getCardNumberFromRequest(clientRequest);
        String username = ((EncryptionRequest) clientRequest).getUserSendingRequest();

        if (requestIsValid(cardNumber, username)) {
            processValidRequest(cardNumber, outputStream);
        } else {
            notifyUserForInvalidRequest(username, cardNumber, outputStream);
        }
    }

    @Override
    public String getCardNumberFromRequest(Request clientRequest) {
        return ((EncryptionRequest) clientRequest).getCardNumber().replaceAll(" ", "");
    }

    private boolean requestIsValid(String encryptedNumber, String username) {
        return cardNumberIsValid(encryptedNumber) &&
                userHasValidAccessRights(username, AccessRights.ENCRYPTION);
    }

    @Override
    public boolean cardNumberIsValid(String cardNumber) {
        return validator.cardNumberIsValidByLuhn(cardNumber)
                && validator.decryptedCardNumberIsValid(cardNumber);
    }

    @Override
    public void processValidRequest(String cardNumber, ObjectOutputStream outputStream) throws IOException {
        String encryptedCard = getModifiedCard(cardNumber);
        returnCardNumberToClient(encryptedCard, outputStream);
        saveCardPairToTable(cardNumber, encryptedCard);
    }

    @Override
    public String getModifiedCard(String cardNumber) {
        return cipher.encryptCardNumber(cardNumber);
    }

    @Override
    public void returnCardNumberToClient(String encryptedNumber, ObjectOutputStream outputStream) throws IOException {
        Response result = new Response(ResponseStatus.SUCCESS, encryptedNumber);
        sendResponseToClient(result, outputStream);
    }

    private void notifyUserForInvalidRequest(String username, String encryptedNumber, ObjectOutputStream outputStream) throws IOException {
        if (!userHasValidAccessRights(username, AccessRights.ENCRYPTION)) {
            notifyClientForInvalidRights("encryption", outputStream);
        } else {
            notifyClientForInvalidCardNumber(encryptedNumber, outputStream);
        }
    }
}