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
    private ExecutorService executor;
    private ServerSocket serverSocket;
    private UserWrapper registeredUsers;
    private SubstitutionCipher cipher;
    private Validation validator;
    private ClientRunnable clientSession;
    private BankCardTableController cardController;
    private ServerMessageLogger logger;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        executor = Executors.newCachedThreadPool();
        cipher = new SubstitutionCipher(5);
        cardController = new BankCardTableController();
        validator = new Validation();
        logger = new ServerMessageLogger(textAreaLog);

        File userFile = new File("users.ser");
        File tableSortedByCard = new File("tableSortedByCard.txt");
        File tableSortedByEncryption = new File("tableSortedByEncryption.txt");

        if (userFile.exists()) {
            registeredUsers = loadUsers();
            logger.displayMessage("Successfully loaded users.");
        } else {
            logger.displayMessage("WARNING: Could not load users.");
        }
        if (tableSortedByCard.exists()) {
            cardController.setCardTableSortedByCardNumber(cardController.readSortByCardFromFile());
            logger.displayMessage("Successfully loaded tableSortedByCard.txt.");
        } else {
            logger.displayMessage("WARNING: Could not load table with cards sorted by card number.");

        }
        if (tableSortedByEncryption.exists()) {
            cardController.setCardTableSortedByEncryptedNumber(cardController.readSortByEncryptionFromFile());
            logger.displayMessage("Successfully loaded tableSortedByEncryption.txt.");
        } else {
            logger.displayMessage("WARNING: Could not load table with cards sorted by encryption.");
        }

        new Thread(this::startServer).start();
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
            logger.displayMessage("Waiting for client actions...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                clientSession = new ClientRunnable(clientSocket,validator,cipher,textAreaLog,registeredUsers,cardController);
                executor.execute(clientSession);
            }

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
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
        logger.displayMessage("Adding new user.");

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
            logger.displayMessage(registeredUsers.toString());
        } catch (NullPointerException nullPointerException) {
            System.err.println("Error: Could not find file to update.");
        }
    }

    @FXML
    void clickButtonViewCardsSortedByEncryption() {
        TreeMap<String, String> table = cardController.readSortByEncryptionFromFile();
        cardController.setCardTableSortedByEncryptedNumber(table);

        logger.displayMessage(String.format("Displaying card table sorted by encryption: \n" +
                        "Card number\t\tEncrypted Number\n%s",
                cardController.toStringSortedByEncryption()));
    }

    @FXML
    void clickButtonViewCardsSortedByNumber() {
        TreeMap<String, String> table = cardController.readSortByCardFromFile();
        cardController.setCardTableSortedByCardNumber(table);

        logger.displayMessage(String.format("Displaying card table sorted by card number: \n" +
                        "Card number\t\tEncrypted Number\n%s",
                cardController.toStringSortedByCard()));
    }
}