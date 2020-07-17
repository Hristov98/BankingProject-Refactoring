package serverCommunicationHandlers;

import communication.*;
import userStorage.AccessRights;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class DecryptionRequestHandler extends CardRequestHandler {
    protected boolean requestIsValid(Request clientRequest) {
        String cardNumber = getCardNumberFromRequest(clientRequest);
        String username = getUsernameFromRequest(clientRequest);

        return cardNumberInputIsValid(cardNumber) && userHasValidAccessRights(username, AccessRights.DECRYPTION);
    }

    protected boolean cardNumberInputIsValid(String cardNumber) {
        return validator.encryptedCardNumberIsValid(cardNumber);
    }

    protected String getModifiedCard(String cardNumber) {
        return cipher.decryptCardNumber(cardNumber);
    }

    protected void notifyUserForInvalidRequest(Request clientRequest, ObjectOutputStream outputStream) throws IOException {
        String cardNumber = getCardNumberFromRequest(clientRequest);
        String username = getUsernameFromRequest(clientRequest);

        if (!userHasValidAccessRights(username, AccessRights.DECRYPTION)) {
            notifyClientForInvalidRights("decryption", outputStream);
        } else {
            notifyClientForInvalidCardNumber(cardNumber, outputStream);
        }
    }
}
