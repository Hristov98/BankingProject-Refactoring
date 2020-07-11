package serverCommunicationHandlers;

import cardManipulation.cardTables.BankCardTableController;
import cardManipulation.CardValidator;
import cardManipulation.encryptionAlgorithms.SubstitutionCipher;
import communication.Request;
import communication.Response;
import communication.ResponseStatus;
import serverApp.ServerMessageLogger;
import userStorage.AccessRights;
import userStorage.User;

import java.io.IOException;
import java.io.ObjectOutputStream;

public abstract class CardRequestProcessor extends RequestProcessor {
    private final BankCardTableController cardController;
    protected final SubstitutionCipher cipher;
    protected final CardValidator validator;

    public CardRequestProcessor(Request clientRequest, ObjectOutputStream outputStream,
                                ServerMessageLogger logger, String clientName,
                                BankCardTableController cardController) {
        super(clientRequest, outputStream, logger, clientName);
        this.cardController = cardController;
        cipher = new SubstitutionCipher(5);
        validator = new CardValidator();
    }

    public abstract String getCardNumberFromRequest();

    public abstract boolean cardNumberIsValid(String cardNumber);

    public abstract void processSuccessfulRequest(String cardNumber) throws IOException;

    public abstract String getModifiedCard(String cardNumber);

    public abstract void returnCardNumberToClient(String encryptedNumber) throws IOException;

    protected boolean userHasValidAccessRights(String username, AccessRights neededRights) {
        User user = findUserByName(username);

        if (user != null) {
            return compareUserPermissionsToNeededType(user.getPermissions(), neededRights);
        } else {
            return false;
        }
    }

    private boolean compareUserPermissionsToNeededType(AccessRights userRights, AccessRights neededRights) {
        return userRights.equals(neededRights) || userRights.equals(AccessRights.FULL_ACCESS);
    }

    protected void notifyClientForInvalidRights(String action) throws IOException {
        String errorMessage = String.format("You do not have the permissions to perform this %s.", action);

        Response result = new Response(ResponseStatus.FAILURE, errorMessage);
        sendResponseToClient(result);
    }

    protected void notifyClientForInvalidCardNumber(String cardNumber) throws IOException {
        String errorMessage = String.format("%s is not a valid card", cardNumber);

        Response result = new Response(ResponseStatus.FAILURE, errorMessage);
        sendResponseToClient(result);
    }

    protected void saveCardPairToTable(String cardNumber, String encryptedNumber) {
        cardController.addCard(cardNumber, encryptedNumber);
        cardController.saveSortByCardToFile();
        cardController.saveSortByEncryptionToFile();
    }
}