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
import java.util.Iterator;

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
            try {
                Object object = inputStream.readObject();

                if (object instanceof String) {
                    logger.displayMessage((String) object);
                }
                if (object instanceof LoginRequest) {
                    String username = ((LoginRequest) object).getUsername();
                    String password = ((LoginRequest) object).getPassword();

                    boolean userExists = false;
                    HashSet<User> users = userLoader.getRegisteredUsers().getUsers();
                    Iterator<User> iterator = users.iterator();

                    while (iterator.hasNext()) {
                        User user = iterator.next();
                        if (username.equals(user.getUsername()) && password.equals(user.getPassword())) {
                            userExists = true;
                            break;
                        }
                    }

                    if (userExists) {
                        logger.displayMessage(String.format("Logging in user %s.", username));
                        Response result = new Response(RequestType.LOGIN, ResponseStatus.SUCCESS, username);
                        clientName = username;

                        outputStream.writeObject(result);
                        outputStream.flush();
                    } else {
                        logger.displayMessage(String.format("User %s could not be found.", username));
                        String errorMessage = "You have entered an incorrect name and/or password.";
                        Response result = new Response(RequestType.LOGIN, ResponseStatus.FAILURE, errorMessage);
                        outputStream.writeObject(result);
                        outputStream.flush();
                    }
                }
                if (object instanceof EncryptionRequest) {
                    String cardNumber = ((EncryptionRequest) object).getCardNumber()
                            .replaceAll(" ", "");

                    if (validator.validationByLuhn(cardNumber) && validator.validationByRegexDecrypted(cardNumber)) {
                        boolean hasRights = getUserRightsByMethod(clientName, AccessRights.ENCRYPTION);

                        if (hasRights) {
                            String encryptedNumber = cipher.encrypt(cardNumber);
                            logger.displayMessage(String.format("Sending %s back to %s"
                                    , encryptedNumber, clientName));

                            Response result = new Response(RequestType.ENCRYPTION, ResponseStatus.SUCCESS, encryptedNumber);
                            outputStream.writeObject(result);

                            cardController.addCard(cardNumber, encryptedNumber);
                            cardController.saveSortByCardToFile();
                            cardController.saveSortByEncryptionToFile();
                        } else {
                            String errorMessage = "You do not have the permissions to perform an encryption.";
                            Response result = new Response(RequestType.ENCRYPTION, ResponseStatus.FAILURE, errorMessage);
                            outputStream.writeObject(result);
                        }
                    } else {
                        String errorMessage = String.format("%s is not a valid card", cardNumber);
                        Response result = new Response(RequestType.ENCRYPTION, ResponseStatus.FAILURE, errorMessage);
                        outputStream.writeObject(result);
                    }
                    outputStream.flush();
                }
                if (object instanceof DecryptionRequest) {
                    String encryptedNumber = ((DecryptionRequest) object).getCardNumber()
                            .replaceAll(" ", "");

                    if (validator.validationByRegexEncrypted(encryptedNumber)) {
                        boolean hasRights = getUserRightsByMethod(clientName, AccessRights.DECRYPTION);

                        if (hasRights) {
                            String decryptedNumber = cipher.decrypt(encryptedNumber);
                            logger.displayMessage(String.format("Sending %s back to %s"
                                    , decryptedNumber, clientName));

                            Response result = new Response(RequestType.DECRYPTION, ResponseStatus.SUCCESS, decryptedNumber);
                            outputStream.writeObject(result);

                            cardController.addCard(decryptedNumber, encryptedNumber);
                            cardController.saveSortByCardToFile();
                            cardController.saveSortByEncryptionToFile();
                        } else {
                            String errorMessage = "You do not have the permissions to perform a decryption.";

                            Response result = new Response(RequestType.DECRYPTION, ResponseStatus.FAILURE, errorMessage);
                            outputStream.writeObject(result);
                        }
                    } else {
                        String errorMessage = String.format("%s is not a valid card", encryptedNumber);

                        Response result = new Response(RequestType.DECRYPTION, ResponseStatus.FAILURE, errorMessage);
                        outputStream.writeObject(result);
                    }
                    outputStream.flush();
                }
            } catch (ClassNotFoundException unknownClassException) {
                logger.displayMessage("Unknown object type received.");
            }
        }
    }

    private boolean getUserRightsByMethod(String username, AccessRights rights) {
        boolean hasRights = false;
        HashSet<User> users = userLoader.getRegisteredUsers().getUsers();
        Iterator<User> iterator = users.iterator();

        while (iterator.hasNext()) {
            User user = iterator.next();
            if (username.equals(user.getUsername())) {
                if (user.getPermissions().equals(rights)
                        || user.getPermissions().equals(AccessRights.FULL_ACCESS)) {
                    logger.displayMessage(String.format("%s's rights have been confirmed.", username));
                    hasRights = true;
                    break;
                }
            }
        }

        return hasRights;
    }

    private void closeConnection() {
        logger.displayMessage(String.format("Terminating connection with %s.", clientName));

        try {
            if (outputStream != null) {
                outputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}