package serverCommunicationHandlers;

import cardManipulation.CardValidator;
import cardManipulation.cardTables.TableSortedByCardNumber;
import cardManipulation.cardTables.TableSortedByEncryptedNumber;
import cardManipulation.encryptionAlgorithms.SubstitutionCipher;
import communication.CardActionRequest;
import communication.Request;
import communication.Response;
import communication.ResponseStatus;
import userStorage.AccessRights;
import userStorage.User;

import java.io.IOException;
import java.io.ObjectOutputStream;

public abstract class CardRequestHandler extends RequestHandler {
    protected final SubstitutionCipher cipher;
    protected final CardValidator validator;

    public CardRequestHandler() {
        cipher = new SubstitutionCipher(5);
        validator = new CardValidator();
    }

    public void processRequest(Request clientRequest, ObjectOutputStream outputStream) throws IOException {
        if (requestIsValid(clientRequest)) {
            processValidRequest(clientRequest, outputStream);
        } else {
            notifyUserForInvalidRequest(clientRequest, outputStream);
        }
    }

    protected abstract boolean requestIsValid(Request clientRequest);

    protected String getCardNumberFromRequest(Request clientRequest) {
        return ((CardActionRequest) clientRequest).getCardNumber().replaceAll(" ", "");
    }

    protected String getUsernameFromRequest(Request clientRequest) {
        return ((CardActionRequest) clientRequest).getUserSendingRequest();
    }

    protected abstract boolean cardNumberInputIsValid(String cardNumber);

    protected boolean userHasValidAccessRights(String username, AccessRights neededRights) {
        User user = findUserByName(username);

        return user != null && compareUserPermissionsToNeededType(user.getPermissions(), neededRights);
    }

    private boolean compareUserPermissionsToNeededType(AccessRights userRights, AccessRights neededRights) {
        return userRights.equals(neededRights) || userRights.equals(AccessRights.FULL_ACCESS);
    }

    protected void processValidRequest(Request clientRequest, ObjectOutputStream outputStream) throws IOException {
        String cardNumber = getCardNumberFromRequest(clientRequest);
        String encryptedCard = getModifiedCard(cardNumber);

        returnCardNumberToClient(encryptedCard, outputStream);
        saveCardPairToTable(cardNumber, encryptedCard);
    }

    protected abstract String getModifiedCard(String cardNumber);

    public void returnCardNumberToClient(String cardNumber, ObjectOutputStream outputStream) throws IOException {
        Response result = new Response(ResponseStatus.SUCCESS, cardNumber);
        sendResponseToClient(result, outputStream);
    }

    private void saveCardPairToTable(String cardNumber, String encryptedNumber) {
        updateTableSortedByCardNumber(cardNumber, encryptedNumber);
        updateTableSortedByEncryptedNumber(cardNumber, encryptedNumber);
    }

    private void updateTableSortedByCardNumber(String cardNumber, String encryptedNumber) {
        TableSortedByCardNumber tableSortedByCardNumber = new TableSortedByCardNumber();
        tableSortedByCardNumber.loadCardTable();

        tableSortedByCardNumber.addCardToTable(cardNumber, encryptedNumber);
        tableSortedByCardNumber.saveTableToFile();
    }

    private void updateTableSortedByEncryptedNumber(String cardNumber, String encryptedNumber) {
        TableSortedByEncryptedNumber tableSortedByEncryptedNumber = new TableSortedByEncryptedNumber();
        tableSortedByEncryptedNumber.loadCardTable();

        tableSortedByEncryptedNumber.addCardToTable(cardNumber, encryptedNumber);
        tableSortedByEncryptedNumber.saveTableToFile();
    }

    protected abstract void notifyUserForInvalidRequest(Request clientRequest, ObjectOutputStream outputStream) throws IOException;

    protected void notifyClientForInvalidRights(String action, ObjectOutputStream outputStream) throws IOException {
        String errorMessage = String.format("You do not have the permissions to perform this %s.", action);

        Response result = new Response(ResponseStatus.FAILURE, errorMessage);
        sendResponseToClient(result, outputStream);
    }

    protected void notifyClientForInvalidCardNumber(String cardNumber, ObjectOutputStream outputStream) throws IOException {
        String errorMessage = String.format("%s is not a valid card", cardNumber);

        Response result = new Response(ResponseStatus.FAILURE, errorMessage);
        sendResponseToClient(result, outputStream);
    }
}
