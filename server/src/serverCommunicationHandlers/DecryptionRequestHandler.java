package serverCommunicationHandlers;

import communication.DecryptionRequest;
import communication.Request;
import communication.Response;
import communication.ResponseStatus;
import userStorage.AccessRights;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class DecryptionRequestHandler extends CardRequestHandler {
    public DecryptionRequestHandler(Request clientRequest, String clientName) {
        super(clientRequest, clientName);
    }

    @Override
    public void processRequest(ObjectOutputStream outputStream) throws IOException {
        String encryptedNumber = getCardNumberFromRequest();

        if (requestIsValid(encryptedNumber)) {
            processValidRequest(encryptedNumber, outputStream);
        } else {
            notifyUserForInvalidRequest(encryptedNumber, outputStream);
        }
    }

    private boolean requestIsValid(String encryptedNumber) {
        return cardNumberIsValid(encryptedNumber) && userHasValidAccessRights(clientName, AccessRights.DECRYPTION);
    }

    private void notifyUserForInvalidRequest(String encryptedNumber, ObjectOutputStream outputStream) throws IOException {
        if (!userHasValidAccessRights(clientName, AccessRights.DECRYPTION)) {
            notifyClientForInvalidRights("decryption", outputStream);
        } else {
            notifyClientForInvalidCardNumber(encryptedNumber, outputStream);
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
}
