package serverApp;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import other.BankCardTableController;
import other.SubstitutionCipher;
import other.UserWrapper;
import other.Validation;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerController implements Initializable {
    private final String USER_FILE_NAME = "users.ser";
    private ExecutorService executor;
    private ServerSocket serverSocket;
    private UserWrapper registeredUsers;
    private SubstitutionCipher cipher;
    private Validation validator;
    private ClientRunnable clientSession;
    private BankCardTableController cardController;
    private ServerMessageLogger logger;

    @FXML
    private TextArea textAreaLog;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initialiseClassVariables();
        loadUsers();
        loadCardTableSortedByCardNumber();
        loadCardTableSortedByEncryptedCardNumber();
        startServerThread();
    }

    private void initialiseClassVariables() {
        executor = Executors.newCachedThreadPool();
        cipher = new SubstitutionCipher(5);
        cardController = new BankCardTableController();
        validator = new Validation();
        logger = new ServerMessageLogger(textAreaLog);
    }

    private void loadUsers() {
        try {
            ObjectInputStream inputStream = openFile();
            UserWrapper users = (UserWrapper) inputStream.readObject();
            setRegisteredUsers(users);
            inputStream.close();
            logger.displayMessage("Successfully loaded users.");
        } catch (IOException ioException) {
            logger.displayMessage("Error while opening file.");
            ioException.printStackTrace();
        } catch (ClassNotFoundException unknownClassException) {
            logger.displayMessage("Unknown object loaded.\n");
            unknownClassException.printStackTrace();
        }
    }

    private ObjectInputStream openFile() throws IOException {
        File userFile = new File(USER_FILE_NAME);

        if (!userFile.exists()) {
            logger.displayMessage("WARNING: User file does not exist. Creating empty user file.");
            userFile.createNewFile();
            saveEmptyContainerToFile();
        }

        return new ObjectInputStream(new FileInputStream(USER_FILE_NAME));
    }

    private void saveEmptyContainerToFile() {
        try {
            ObjectOutputStream outputStream =
                    new ObjectOutputStream(new FileOutputStream("users.ser"));
            outputStream.writeObject(new UserWrapper());
            outputStream.close();
        } catch (IOException ioException) {
            logger.displayMessage("ERROR: Could not save empty container to file.");
            ioException.printStackTrace();
        }
    }

    private void setRegisteredUsers(UserWrapper users) {
        registeredUsers = new UserWrapper(users);
    }

    private void loadCardTableSortedByCardNumber() {
        File tableSortedByCard = new File("tableSortedByCard.txt");

        if (tableSortedByCard.exists()) {
            cardController.setCardTableSortedByCardNumber(cardController.readSortByCardFromFile());
            logger.displayMessage("Successfully loaded tableSortedByCard.txt.");
        } else {
            logger.displayMessage("WARNING: Could not load table with cards sorted by card number.");
        }
    }

    private void loadCardTableSortedByEncryptedCardNumber() {
        File tableSortedByEncryption = new File("tableSortedByEncryption.txt");

        if (tableSortedByEncryption.exists()) {
            cardController.setCardTableSortedByEncryptedNumber(cardController.readSortByEncryptionFromFile());
            logger.displayMessage("Successfully loaded tableSortedByEncryption.txt.");
        } else {
            logger.displayMessage("WARNING: Could not load table with cards sorted by encryption.");
        }
    }

    private void startServerThread() {
        new Thread(this::startServer).start();
    }

    public void startServer() {
        try {
            initialiseServerSocket();
            listenForClientConnections();
        } catch (IOException ioException) {
            logger.displayMessage("Error: Server could not start correctly");
            ioException.printStackTrace();
        }
    }

    private void initialiseServerSocket() throws IOException {
        serverSocket = new ServerSocket(12345, 100);
    }

    private void listenForClientConnections() throws IOException {
        while (true) {
            connectToClient();
            executeSession();
        }
    }

    private void connectToClient() throws IOException {
        Socket clientSocket = serverSocket.accept();
        clientSession = new ClientRunnable(clientSocket, validator, cipher,
                textAreaLog, registeredUsers, cardController);
    }

    private void executeSession() {
        executor.execute(clientSession);
    }

    @FXML
    void clickButtonRegisterUser() throws IOException {
        openRegistrationWindow();
    }

    private void openRegistrationWindow() throws IOException {
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
            loadUsers();
            logger.displayMessage(registeredUsers.toString());
        } catch (NullPointerException nullPointerException) {
            logger.displayMessage("Error: Could not find file to update.");
            nullPointerException.printStackTrace();
        }
    }

    @FXML
    void clickButtonViewCardsSortedByEncryption() {
        TreeMap<String, String> table = cardController.readSortByEncryptionFromFile();
        cardController.setCardTableSortedByEncryptedNumber(table);

        logger.displayMessage(String.format("Displaying card table sorted by encryption: \n"
                + "Card number\t\tEncrypted Number\n%s", cardController.toStringSortedByEncryption()));
    }

    @FXML
    void clickButtonViewCardsSortedByNumber() {
        TreeMap<String, String> table = cardController.readSortByCardFromFile();
        cardController.setCardTableSortedByCardNumber(table);

        logger.displayMessage(String.format("Displaying card table sorted by card number: \n"
                + "Card number\t\tEncrypted Number\n%s", cardController.toStringSortedByCard()));
    }

    @FXML
    void clickButtonExit() {
        Platform.exit();
        executor.shutdown();
        System.exit(0);
    }
}