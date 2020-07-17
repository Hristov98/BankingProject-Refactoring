package serverCommunicationHandlers;

import communication.DecryptionRequest;
import communication.Request;
import communication.Response;
import communication.ResponseStatus;
import userStorage.AccessRights;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class DecryptionRequestHandler extends CardRequestHandler {
    @Override
    public void processRequest(Request clientRequest, ObjectOutputStream outputStream) throws IOException {
        String encryptedNumber = getCardNumberFromRequest(clientRequest);
        String username = ((DecryptionRequest) clientRequest).getUserSendingRequest();

        if (requestIsValid(encryptedNumber, username)) {
            processValidRequest(encryptedNumber, outputStream);
        } else {
            notifyUserForInvalidRequest(username, encryptedNumber, outputStream);
        }
    }

    @Override
    public String getCardNumberFromRequest(Request clientRequest) {
        return ((DecryptionRequest) clientRequest).getCardNumber()
                .replaceAll(" ", "");
    }

    private boolean requestIsValid(String encryptedNumber, String username) {
        return cardNumberIsValid(encryptedNumber)
                && userHasValidAccessRights(username, AccessRights.DECRYPTION);
    }

    @Override
    public boolean cardNumberIsValid(String cardNumber) {
        return validator.encryptedCardNumberIsValid(cardNumber);
    }

    @Override
    public void processValidRequest(String cardNumber, ObjectOutputStream outputStream) throws IOException {
        String decryptedNumber = getModifiedCard(cardNumber);
        returnCardNumberToClient(decryptedNumber, outputStream);
        saveCardPairToTable(decryptedNumber, cardNumber);
    }

    @Override
    public String getModifiedCard(String cardNumber) {
        return cipher.decryptCardNumber(cardNumber);
    }

    @Override
    public void returnCardNumberToClient(String decryptedNumber, ObjectOutputStream outputStream) throws IOException {
        Response result = new Response(ResponseStatus.SUCCESS, decryptedNumber);
        sendResponseToClient(result, outputStream);
    }

    private void notifyUserForInvalidRequest(String username, String encryptedNumber, ObjectOutputStream outputStream) throws IOException {
        if (!userHasValidAccessRights(username, AccessRights.DECRYPTION)) {
            notifyClientForInvalidRights("decryption", outputStream);
        } else {
            notifyClientForInvalidCardNumber(encryptedNumber, outputStream);
        }
    }
}
