package serverCommunicationHandlers;

import cardManipulation.BankCardTableController;
import communication.EncryptionRequest;
import communication.Request;
import communication.Response;
import communication.ResponseStatus;
import serverApp.ServerMessageLogger;
import userStorage.AccessRights;
import userStorage.UserLoader;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class EncryptionRequestProcessor extends CardRequestProcessor {
    public EncryptionRequestProcessor(Request clientRequest, UserLoader userLoader,
                                      ObjectOutputStream outputStream, ServerMessageLogger logger,
                                      String clientName, BankCardTableController cardController) {
        super(clientRequest, userLoader, outputStream, logger, clientName, cardController);
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