package serverApp;

import communication.*;
import javafx.scene.control.TextArea;
import other.*;

import javax.crypto.Cipher;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.Iterator;

public class ClientRunnable implements Runnable {
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private String clientName;
    private final Socket connection;
    private ServerMessageLogger logger;
    private SubstitutionCipher cipher;
    private Validation validator;
    private UserWrapper registeredUsers;
    private BankCardTableController cardController;

    ClientRunnable(Socket connect, Validation validator, SubstitutionCipher cipher,
                   TextArea textArea, UserWrapper registeredUsers, BankCardTableController cardController) {
        connection = connect;
        clientName = "guest";
        logger = new ServerMessageLogger(textArea);
        this.validator = validator;
        this.cipher = cipher;
        this.registeredUsers= registeredUsers;
        this.cardController = cardController;
    }

    private void getStreams() throws IOException {
        outputStream = new ObjectOutputStream(connection.getOutputStream());
        outputStream.flush();
        inputStream = new ObjectInputStream(connection.getInputStream());

        logger.displayMessage("Server Input/Output streams loaded successfully.");
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
                    HashSet<User> users = registeredUsers.getUsers();
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
        HashSet<User> users = registeredUsers.getUsers();
        Iterator<User> iterator = users.iterator();

        while (iterator.hasNext()) {
            User user = iterator.next();
            if (username.equals(user.getUsername())) {
                if (user.getPermissions().equals(rights)
                        || user.getPermissions().equals(AccessRights.FULL)) {
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

    @Override
    public void run() {
        logger.displayMessage("Connection received from: "
                + connection.getInetAddress().getHostName());

        try {
            getStreams();
            processConnection();
        } catch (IOException ioException) {
            logger.displayMessage(String.format("%s has terminated the connection.", clientName));
        } finally {
            closeConnection();
        }
    }
}