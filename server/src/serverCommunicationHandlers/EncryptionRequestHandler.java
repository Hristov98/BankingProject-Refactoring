package serverCommunicationHandlers;

import communication.*;
import userStorage.AccessRights;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class EncryptionRequestHandler extends CardRequestHandler {
    protected boolean requestIsValid(Request clientRequest) {
        String cardNumber = getCardNumberFromRequest(clientRequest);
        String username = getUsernameFromRequest(clientRequest);

        return cardNumberInputIsValid(cardNumber) && userHasValidAccessRights(username, AccessRights.ENCRYPTION);
    }

    protected boolean cardNumberInputIsValid(String cardNumber) {
        return validator.cardNumberIsValidByLuhn(cardNumber) && validator.decryptedCardNumberIsValid(cardNumber);
    }

    protected String getModifiedCard(String cardNumber) {
        return cipher.encryptCardNumber(cardNumber);
    }

    protected void notifyUserForInvalidRequest(Request clientRequest, ObjectOutputStream outputStream) throws IOException {
        String cardNumber = getCardNumberFromRequest(clientRequest);
        String username = getUsernameFromRequest(clientRequest);

        if (!userHasValidAccessRights(username, AccessRights.ENCRYPTION)) {
            notifyClientForInvalidRights("encryption", outputStream);
        } else {
            notifyClientForInvalidCardNumber(cardNumber, outputStream);
        }
    }
}