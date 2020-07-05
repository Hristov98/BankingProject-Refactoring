package serverApp;

import communication.*;
import javafx.scene.control.TextArea;
import cardManipulation.*;
import userStorage.AccessRights;
import userStorage.User;
import userStorage.UserLoader;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashSet;

public class ClientRunnable implements Runnable {
    private final Socket connection;
    private final SubstitutionCipher cipher;
    private final Validation validator;
    private String clientName;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private UserLoader userLoader;
    private BankCardTableController cardController;
    private ServerMessageLogger logger;

    ClientRunnable(Socket connect) {
        connection = connect;
        setClientName("guest");
        cipher = new SubstitutionCipher(5);
        validator = new Validation();
    }

    private void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public void initialiseUserLoader(UserLoader userLoader) {
        this.userLoader = userLoader;
    }

    public void initialiseCardController(BankCardTableController cardController) {
        this.cardController = cardController;
    }

    public void initialiseLogger(TextArea textArea) {
        logger = new ServerMessageLogger(textArea);
    }

    @Override
    public void run() {
        logger.displayMessage("Connection received from: " + connection.getInetAddress().getHostName());

        try {
            initialiseStreams();
            processConnection();
        } catch (IOException ioException) {
            logger.displayMessage(String.format("%s has terminated the connection.", clientName));
            ioException.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    private void initialiseStreams() throws IOException {
        initialiseInputStream();
        initialiseOutputStream();
        logger.displayMessage("Server Input/Output streams loaded successfully.");
    }

    private void initialiseInputStream() throws IOException {
        outputStream = new ObjectOutputStream(connection.getOutputStream());
        outputStream.flush();
    }

    private void initialiseOutputStream() throws IOException {
        inputStream = new ObjectInputStream(connection.getInputStream());
    }

    private void processConnection() throws IOException {
        logger.displayMessage("Server connected successfully.");

        while (true) {
            processClientRequest();
        }
    }

    private void processClientRequest() throws IOException {
        try {
            Object clientRequest = inputStream.readObject();

            if (clientRequest instanceof String) {
                processString((String) clientRequest);
            }
            if (clientRequest instanceof LoginRequest) {
                processLoginRequest((LoginRequest) clientRequest);
            }
            if (clientRequest instanceof EncryptionRequest) {
                processEncryptionRequest((EncryptionRequest) clientRequest);
            }
            if (clientRequest instanceof DecryptionRequest) {
                processDecryptionRequest((DecryptionRequest) clientRequest);
            }
        } catch (ClassNotFoundException classNotFoundException) {
            logger.displayMessage(String.format("Error: Unknown object received from %s.", clientName));
            classNotFoundException.printStackTrace();
        }
    }

    private void processString(String message) {
        logger.displayMessage(message);
    }

    private void processLoginRequest(LoginRequest request) throws IOException {
        String username = request.getUsername();
        String password = request.getPassword();

        if (userExists(username, password)) {
            notifyClientForSuccessfulLogin(username);
        } else {
            notifyClientForFailedLogin(username);
        }
    }

    private boolean userExists(String username, String password) {
        User user = findUserByName(username);

        if (user != null) {
            return password.equals(user.getPassword());
        } else {
            return false;
        }
    }

    private User findUserByName(String username) {
        HashSet<User> users = userLoader.getRegisteredUsers().getUsers();

        for (User user : users) {
            if (username.equals(user.getUsername())) {
                return user;
            }
        }

        return null;
    }

    private void notifyClientForSuccessfulLogin(String username) throws IOException {
        logger.displayMessage(String.format("Logging in user %s.", username));
        setClientName(username);

        Response result = new Response(ResponseStatus.SUCCESS, username);
        sendResponseToClient(result);
    }

    private void sendResponseToClient(Response response) throws IOException {
        outputStream.writeObject(response);
        outputStream.flush();
    }

    private void notifyClientForFailedLogin(String username) throws IOException {
        logger.displayMessage(String.format("User %s could not be found.", username));
        String errorMessage = "You have entered an incorrect name and/or password.";

        Response result = new Response(ResponseStatus.FAILURE, errorMessage);
        sendResponseToClient(result);
    }

    private void processEncryptionRequest(EncryptionRequest request) throws IOException {
        String cardNumber = getCardNumberFromRequest(request);

        if (!cardNumberIsValidForEncryption(cardNumber)) {
            notifyUserForInvalidCardNumberDuringEncryption(cardNumber);
        } else if (!userHasValidAccessRights(clientName, AccessRights.ENCRYPTION)) {
            notifyUserForInvalidEncryptionRights();
        } else {
            processSuccessfulEncryptionRequest(cardNumber);
        }
    }

    private String getCardNumberFromRequest(EncryptionRequest request) {
        return request.getCardNumber().replaceAll(" ", "");
    }

    private boolean cardNumberIsValidForEncryption(String cardNumber) {
        return validator.validationByLuhn(cardNumber) && validator.validationByRegexDecrypted(cardNumber);
    }

    private void notifyUserForInvalidEncryptionRights() throws IOException {
        String errorMessage = "You do not have the permissions to perform an encryption.";
        Response result = new Response(ResponseStatus.FAILURE, errorMessage);
        sendResponseToClient(result);
    }

    private boolean userHasValidAccessRights(String username, AccessRights neededRights) {
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

    private void notifyUserForInvalidCardNumberDuringEncryption(String cardNumber) throws IOException {
        String errorMessage = String.format("%s is not a valid card", cardNumber);
        Response result = new Response(ResponseStatus.FAILURE, errorMessage);
        sendResponseToClient(result);
    }

    private void processSuccessfulEncryptionRequest(String cardNumber) throws IOException {
        String encryptedCard = encryptCardNumber(cardNumber);
        returnEncryptedCardNumberToClient(encryptedCard);
        saveCardPairToTable(cardNumber, encryptedCard);
    }

    private String encryptCardNumber(String cardNumber) {
        return cipher.encrypt(cardNumber);
    }

    private void returnEncryptedCardNumberToClient(String encryptedNumber) throws IOException {
        logger.displayMessage(String.format("Sending %s back to %s", encryptedNumber, clientName));
        Response result = new Response(ResponseStatus.SUCCESS, encryptedNumber);
        sendResponseToClient(result);
    }

    private void saveCardPairToTable(String cardNumber, String encryptedNumber) {
        cardController.addCard(cardNumber, encryptedNumber);
        cardController.saveSortByCardToFile();
        cardController.saveSortByEncryptionToFile();
    }

    private void processDecryptionRequest(DecryptionRequest request) throws IOException {
        String encryptedNumber = request.getCardNumber().replaceAll(" ", "");

        if (!cardNumberIsValidForDecryption(encryptedNumber)) {
            notifyUserForInvalidCardNumberDuringDecryption(encryptedNumber);
        } else if (!userHasValidAccessRights(clientName, AccessRights.DECRYPTION)) {
            notifyUserForInvalidDecryptionRights();
        } else {
            processSuccessfulDecryptionRequest(encryptedNumber);
        }
    }

    private boolean cardNumberIsValidForDecryption(String encryptedNumber) {
        return validator.validationByRegexEncrypted(encryptedNumber);
    }

    private void notifyUserForInvalidCardNumberDuringDecryption(String encryptedNumber) throws IOException {
        String errorMessage = String.format("%s is not a valid card", encryptedNumber);
        Response result = new Response(ResponseStatus.FAILURE, errorMessage);
        sendResponseToClient(result);
    }

    private void notifyUserForInvalidDecryptionRights() throws IOException {
        String errorMessage = "You do not have the permissions to perform a decryption.";
        Response result = new Response(ResponseStatus.FAILURE, errorMessage);
        sendResponseToClient(result);
    }

    private void processSuccessfulDecryptionRequest(String encryptedNumber) throws IOException {
        String decryptedNumber = decryptCardNumber(encryptedNumber);
        returnDecryptedCardNumberToClient(decryptedNumber);
        saveCardPairToTable(decryptedNumber, encryptedNumber);
    }

    private String decryptCardNumber(String encryptedNumber) {
        return cipher.decrypt(encryptedNumber);
    }

    private void returnDecryptedCardNumberToClient(String decryptedNumber) throws IOException {
        logger.displayMessage(String.format("Sending %s back to %s", decryptedNumber, clientName));
        Response result = new Response(ResponseStatus.SUCCESS, decryptedNumber);
        sendResponseToClient(result);
    }

    private void closeConnection() {
        try {
            closeInputStream();
            closeOutputStream();
            closeConnectionSocket();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void closeInputStream() throws IOException {
        if (inputStream != null) {
            inputStream.close();
        }
    }

    private void closeOutputStream() throws IOException {
        if (outputStream != null) {
            outputStream.close();
        }
    }

    private void closeConnectionSocket() throws IOException {
        if (connection != null) {
            connection.close();
        }
    }
}