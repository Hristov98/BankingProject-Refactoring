package serverCommunicationHandlers;

import cardManipulation.BankCardTableController;
import communication.DecryptionRequest;
import communication.Request;
import communication.Response;
import communication.ResponseStatus;
import serverApp.ServerMessageLogger;
import userStorage.AccessRights;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class DecryptionRequestProcessor extends CardRequestProcessor {
    public DecryptionRequestProcessor(Request clientRequest, ObjectOutputStream outputStream,
                                      ServerMessageLogger logger, String clientName,
                                      BankCardTableController cardController) {
        super(clientRequest, outputStream, logger, clientName, cardController);
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
        return validator.encryptedCardNumberIsValid(cardNumber);
    }

    @Override
    public void processSuccessfulRequest(String cardNumber) throws IOException {
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
        logger.displayMessage(String.format("Sending %s back to %s", decryptedNumber, clientName));
        Response result = new Response(ResponseStatus.SUCCESS, decryptedNumber);
        sendResponseToClient(result);
    }
}
