package serverApp;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import other.*;
import communication.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerController implements Initializable {
    private ExecutorService executor;
    private ServerSocket serverSocket;
    private UserWrapper registeredUsers;
    private SubstitutionCipher cipher;
    private Validation validator;
    private ClientRunnable clientSession;
    private BankCardTableController cardController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        executor = Executors.newCachedThreadPool();
        cipher = new SubstitutionCipher(5);
        cardController = new BankCardTableController();
        validator = new Validation();

        File userFile = new File("users.ser");
        File tableSortedByCard = new File("tableSortedByCard.txt");
        File tableSortedByEncryption = new File("tableSortedByEncryption.txt");

        if (userFile.exists()) {
            registeredUsers = loadUsers();
            displayMessage("Successfully loaded users.");
        } else {
            displayMessage("WARNING: Could not load users.");
        }
        if (tableSortedByCard.exists()) {
            cardController.setCardTableSortedByCardNumber(cardController.readSortByCardFromFile());
            displayMessage("Successfully loaded tableSortedByCard.txt.");
        } else {
            displayMessage("WARNING: Could not load table with cards sorted by card number.");

        }
        if (tableSortedByEncryption.exists()) {
            cardController.setCardTableSortedByEncryptedNumber(cardController.readSortByEncryptionFromFile());
            displayMessage("Successfully loaded tableSortedByEncryption.txt.");
        } else {
            displayMessage("WARNING: Could not load table with cards sorted by encryption.");
        }

        new Thread(() -> startServer()).start();
    }

    private UserWrapper loadUsers() {
        registeredUsers = null;
        ObjectInputStream inputStream = null;

        try {
            inputStream = new ObjectInputStream(new FileInputStream("users.ser"));
        } catch (IOException ioException) {
            System.err.println("Error while opening file.");
        }

        try {
            Object object = inputStream.readObject();
            if (object instanceof UserWrapper) {
                registeredUsers = new UserWrapper((UserWrapper) object);
            }
        } catch (EOFException endOfFileException) {
            System.err.println("Reached EOF.");
        } catch (ClassNotFoundException unknownClassException) {
            System.err.println("Unable to create object.\n");
        } catch (IOException ioException) {
            System.err.println("Error while reading from file.\n");
        }

        try {
            if (inputStream != null)
                inputStream.close();
        } catch (IOException ioException) {
            System.err.println("Error closing file.");

        }

        return registeredUsers;
    }

    public void startServer() {
        try {
            serverSocket = new ServerSocket(12345, 100);
            displayMessage("Waiting for client actions...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                clientSession = new ClientRunnable(clientSocket);
                executor.execute(clientSession);
            }

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public class ClientRunnable implements Runnable {
        private ObjectOutputStream outputStream;
        private ObjectInputStream inputStream;
        private String clientName;
        private final Socket connection;

        ClientRunnable(Socket connect) {
            connection = connect;
            clientName = "guest";
        }

        private void getStreams() throws IOException {
            outputStream = new ObjectOutputStream(connection.getOutputStream());
            outputStream.flush();
            inputStream = new ObjectInputStream(connection.getInputStream());

            displayMessage("Server Input/Output streams loaded successfully.");
        }

        private void processConnection() throws IOException {
            displayMessage("Server connected successfully.");

            while (true) {
                try {
                    Object object = inputStream.readObject();

                    if (object instanceof String) {
                        displayMessage((String) object);
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
                            displayMessage(String.format("Logging in user %s.", username));
                            Response result = new Response(RequestType.LOGIN,ResponseStatus.SUCCESS,username);
                            clientName = username;

                            outputStream.writeObject(result);
                            outputStream.flush();
                        } else {
                            displayMessage(String.format("User %s could not be found.", username));
                            String errorMessage= "You have entered an incorrect name and/or password.";
                            Response result = new Response(RequestType.LOGIN,ResponseStatus.FAILURE,errorMessage);
                            outputStream.writeObject(result);
                            outputStream.flush();
                        }
                    }
                    if (object instanceof EncryptionRequest) {
                        String cardNumber = ((EncryptionRequest) object).getCardNumber()
                                .replaceAll(" ", "");

                        if (validator.validationByLuhn(cardNumber) && validator.validationByRegexDecrypted(cardNumber)) {
                            displayMessage(String.format("%s is a valid card.", cardNumber));

                            boolean hasRights = getUserRightsByMethod(clientName, AccessRights.ENCRYPTION);

                            if (hasRights) {
                                String encryptedNumber = cipher.encrypt(cardNumber);
                                displayMessage(String.format("Sending %s back to user %s"
                                        , encryptedNumber, clientName));

                                Response result = new Response(RequestType.ENCRYPTION,ResponseStatus.SUCCESS,encryptedNumber);
                                outputStream.writeObject(result);

                                cardController.addCard(cardNumber, encryptedNumber);
                                cardController.saveSortByCardToFile();
                                cardController.saveSortByEncryptionToFile();
                            } else {
                                String errorMessage = "You do not have the permissions to perform an encryption.";
                                Response result = new Response(RequestType.ENCRYPTION,ResponseStatus.FAILURE,errorMessage);
                                outputStream.writeObject(result);
                            }
                        } else {
                            String errorMessage = String.format("%s is not a valid card", cardNumber);
                            displayMessage(errorMessage);
                            Response result = new Response(RequestType.ENCRYPTION,ResponseStatus.FAILURE,errorMessage);
                            outputStream.writeObject(result);
                        }
                        outputStream.flush();
                    }
                    if (object instanceof DecryptionRequest) {
                        String encryptedNumber = ((DecryptionRequest) object).getCardNumber()
                                .replaceAll(" ", "");

                        if (validator.validationByRegexEncrypted(encryptedNumber)) {
                            displayMessage(String.format("%s is a valid card", encryptedNumber));

                            boolean hasRights = getUserRightsByMethod(clientName, AccessRights.DECRYPTION);

                            if (hasRights) {
                                String decryptedNumber = cipher.decrypt(encryptedNumber);
                                displayMessage(String.format("Sending %s back to user %s"
                                        , decryptedNumber, clientName));

                                Response result = new Response(RequestType.DECRYPTION,ResponseStatus.SUCCESS,decryptedNumber);
                                outputStream.writeObject(result);

                                cardController.addCard(decryptedNumber, encryptedNumber);
                                cardController.saveSortByCardToFile();
                                cardController.saveSortByEncryptionToFile();
                            } else {
                                String errorMessage = "You do not have the permissions to perform a decryption.";

                                Response result = new Response(RequestType.DECRYPTION,ResponseStatus.FAILURE,errorMessage);
                                outputStream.writeObject(result);
                            }
                        } else {
                            String errorMessage = String.format("%s is not a valid card", encryptedNumber);
                            displayMessage(errorMessage);

                            Response result = new Response(RequestType.DECRYPTION,ResponseStatus.FAILURE,errorMessage);
                            outputStream.writeObject(result);
                        }
                        outputStream.flush();
                    }
                } catch (ClassNotFoundException unknownClassException) {
                    displayMessage("Unknown object type received.");
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
                        displayMessage(String.format("%s's rights have been confirmed.", username));
                        hasRights = true;
                        break;
                    }
                }
            }

            return hasRights;
        }

        private void closeConnection() {
            displayMessage(String.format("Terminating connection with %s.", clientName));

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
            displayMessage("Connection received from: "
                    + connection.getInetAddress().getHostName());

            try {
                getStreams();
                processConnection();
            } catch (IOException ioException) {
                displayMessage(String.format("%s has terminated the connection.", clientName));
            } finally {
                closeConnection();
            }
        }
    }

    public void displayMessage(String message) {
        String timeStampedMessage = String.format("%s\t%s\n",
                new SimpleDateFormat("HH:mm:ss").format(new Date()), message);

        Platform.runLater(() -> textAreaLog.appendText(timeStampedMessage));
    }

    @FXML
    private TextArea textAreaLog;

    @FXML
    void clickButtonExit() {
        Platform.exit();
        executor.shutdown();
        System.exit(0);
    }

    @FXML
    void clickButtonRegisterUser() throws IOException {
        displayMessage("Adding new user.");

        Stage secondaryStage = new Stage();
        Parent parent = FXMLLoader.load(getClass().getResource("registration.fxml"));
        secondaryStage.setTitle("Registration menu");
        secondaryStage.setScene(new Scene(parent, 500, 250));
        secondaryStage.show();
    }

    @FXML
    void clickButtonClearLog() {
        textAreaLog.setText("");
    }

    @FXML
    void clickButtonUpdateAndDisplayUsers() {
        try {
            registeredUsers = loadUsers();
            displayMessage(registeredUsers.toString());
        } catch (NullPointerException nullPointerException) {
            System.err.println("Error: Could not find file to update.");
        }
    }

    @FXML
    void clickButtonViewCardsSortedByEncryption() {
        TreeMap<String, String> table = cardController.readSortByEncryptionFromFile();
        cardController.setCardTableSortedByEncryptedNumber(table);

        displayMessage(String.format("Displaying card table sorted by encryption: \n" +
                        "Card number\t\tEncrypted Number\n%s",
                cardController.toStringSortedByEncryption()));
    }

    @FXML
    void clickButtonViewCardsSortedByNumber() {
        TreeMap<String, String> table = cardController.readSortByCardFromFile();
        cardController.setCardTableSortedByCardNumber(table);

        displayMessage(String.format("Displaying card table sorted by card number: \n" +
                        "Card number\t\tEncrypted Number\n%s",
                cardController.toStringSortedByCard()));
    }
}