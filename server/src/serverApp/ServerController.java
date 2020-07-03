package serverApp;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import other.*;
import wrappers.DecryptionRequest;
import wrappers.EncryptionRequest;
import wrappers.LoginRequest;

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
    private ServerSocket server;
    private UserWrapper registeredUsers;
    private SubstitutionCipher sub;
    private Validation verifier;
    private StartClient client;
    private BankCardFileControl cardController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        executor = Executors.newCachedThreadPool();
        sub = new SubstitutionCipher(5);
        cardController = new BankCardFileControl();
        verifier = new Validation();

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
            Object obj = inputStream.readObject();
            if (obj instanceof UserWrapper) {
                registeredUsers = new UserWrapper((UserWrapper) obj);
            }
        } catch (EOFException endOfFileException) {
            System.err.println("Reached EOF.");
        } catch (ClassNotFoundException classNotFoundException) {
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
            server = new ServerSocket(12345, 100);
            displayMessage("Waiting for client actions...");

            while (true) {
                Socket socket = server.accept();
                client = new StartClient(socket);
                executor.execute(client);
            }

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public class StartClient implements Runnable {
        private ObjectOutputStream outputStream;
        private ObjectInputStream inputStream;
        private String clientName;
        private final Socket connection;

        StartClient(Socket connect) {
            connection = connect;
            clientName = "guest";
        }

        private void getStreams() throws IOException {
            outputStream = new ObjectOutputStream(connection.getOutputStream());
            outputStream.flush();
            inputStream = new ObjectInputStream(connection.getInputStream());

            displayMessage("Server I/O streams loaded successfully.");
        }

        private void processConnection() throws IOException {
            displayMessage("Server connected successfully.");

            while (true) {
                try {
                    Object obj = inputStream.readObject();

                    if (obj instanceof String) {
                        displayMessage((String) obj);
                    }
                    if (obj instanceof LoginRequest) {
                        String username = ((LoginRequest) obj).getUsername();
                        String password = ((LoginRequest) obj).getPassword();

                        boolean userExists = false;
                        HashSet<User> users = registeredUsers.getSet();
                        Iterator<User> it = users.iterator();

                        while (it.hasNext()) {
                            User user = it.next();
                            if (username.equals(user.getUsername()) && password.equals(user.getPassword())) {
                                userExists = true;
                                break;
                            }
                        }

                        LoginRequest result = new LoginRequest();
                        if (userExists) {
                            displayMessage(String.format("Logging in user %s.", username));
                            result.setValidUser(true);

                            clientName = username;
                        } else {
                            displayMessage(String.format("User %s could not be found.", username));
                            result.setValidUser(false);
                        }

                        outputStream.writeObject(result);
                        outputStream.flush();
                    }
                    if (obj instanceof EncryptionRequest) {
                        String cardNumber = ((EncryptionRequest) obj).getCardNumber()
                                .replaceAll(" ", "");

                        if (verifier.validationByLuhn(cardNumber) && verifier.validationByRegexDecrypted(cardNumber)) {
                            displayMessage(String.format("%s is a valid card.", cardNumber));

                            boolean hasRights = getUserRightsByMethod(clientName, AccessRights.ENCRYPTION);

                            if (hasRights) {
                                String encrypted = sub.encrypt(cardNumber);
                                displayMessage(String.format("Sending %s back to user %s"
                                        , encrypted, clientName));

                                EncryptionRequest result = new EncryptionRequest(encrypted);
                                outputStream.writeObject(result);

                                cardController.addCard(cardNumber, encrypted);
                                cardController.saveSortByCardToFile();
                                cardController.saveSortByEncryptionToFile();
                            } else {
                                String errorMessage = "You do not have the permissions to perform an encryption.";
                                outputStream.writeObject(errorMessage);
                            }
                        } else {
                            String errorMessage = String.format("%s is not a valid card", cardNumber);
                            displayMessage(errorMessage);
                            outputStream.writeObject(errorMessage);
                        }
                        outputStream.flush();
                    }
                    if (obj instanceof DecryptionRequest) {
                        String encryptedNumber = ((DecryptionRequest) obj).getCardNumber()
                                .replaceAll(" ", "");

                        if (verifier.validationByRegexEncrypted(encryptedNumber)) {
                            displayMessage(String.format("%s is a valid card", encryptedNumber));

                            boolean hasRights = getUserRightsByMethod(clientName, AccessRights.DECRYPTION);

                            if (hasRights) {
                                String decrypted = sub.decrypt(encryptedNumber);
                                displayMessage(String.format("Sending %s back to user %s"
                                        , decrypted, clientName));

                                DecryptionRequest result = new DecryptionRequest(decrypted);
                                outputStream.writeObject(result);

                                cardController.addCard(decrypted, encryptedNumber);
                                cardController.saveSortByCardToFile();
                                cardController.saveSortByEncryptionToFile();
                            } else {
                                String errorMessage = "You do not have the permissions to perform a decryption.";
                                outputStream.writeObject(errorMessage);
                            }
                        } else {
                            String errorMessage = String.format("%s is not a valid card", encryptedNumber);
                            displayMessage(errorMessage);

                            outputStream.writeObject(errorMessage);
                        }
                        outputStream.flush();
                    }
                } catch (ClassNotFoundException classNotFoundException) {
                    displayMessage("Unknown object type received.");
                }
            }
        }

        private boolean getUserRightsByMethod(String username, AccessRights rights) {
            boolean hasRights = false;
            HashSet<User> users = registeredUsers.getSet();
            Iterator<User> it = users.iterator();

            while (it.hasNext()) {
                User user = it.next();
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
                if (outputStream != null) {
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
            } catch (IOException eofException) {
                displayMessage(String.format("%s has terminated the connection.", clientName));
            } finally {
                closeConnection();
            }
        }
    }

    public void displayMessage(String message) {
        String timeStampedMessage = String.format("%s\t%s\n",
                new SimpleDateFormat("HH:mm:ss").format(new Date()), message);

        Platform.runLater(() -> txaLog.appendText(timeStampedMessage));
    }

    @FXML
    private TextArea txaLog;

    @FXML
    void btnExitClicked(ActionEvent event) {
        Platform.exit();
        executor.shutdown();
        System.exit(0);
    }

    @FXML
    void btnRegisterUserClicked(ActionEvent event) throws IOException {
        displayMessage("Adding new user.");

        Stage secondaryStage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("registration.fxml"));
        secondaryStage.setTitle("Registration menu");
        secondaryStage.setScene(new Scene(root, 500, 250));
        secondaryStage.show();
    }

    @FXML
    void btnClearLogClicked(ActionEvent event) {
        txaLog.setText("");
    }

    @FXML
    void btnUpdateAndDisplayUsersClicked(ActionEvent event) {
        try {
            registeredUsers = loadUsers();
            displayMessage(registeredUsers.toString());
        } catch (NullPointerException npe) {
            System.err.println("Error: Could not find file to update.");
        }
    }

    @FXML
    void btnViewSortedByEncClicked(ActionEvent event) {
        TreeMap<String, String> table = cardController.readSortByEncryptionFromFile();
        cardController.setCardTableSortedByEncryptedNumber(table);

        displayMessage(String.format("Displaying card table sorted by encryption: \n" +
                        "Card number\t\tEncrypted Number\n%s",
                cardController.toStringSortedByEncryption()));
    }

    @FXML
    void btnViewSortedByNumClicked(ActionEvent event) {

        TreeMap<String, String> table = cardController.readSortByCardFromFile();
        cardController.setCardTableSortedByCardNumber(table);

        displayMessage(String.format("Displaying card table sorted by card number: \n" +
                        "Card number\t\tEncrypted Number\n%s",
                cardController.toStringSortedByCard()));
    }
}
