package serverApp.server;

import cardManipulation.cardTables.CardTable;
import cardManipulation.cardTables.TableSortedByCardNumber;
import cardManipulation.cardTables.TableSortedByEncryptedNumber;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import serverApp.ServerMessageLogger;
import userStorage.UserController;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerController implements Initializable {
    private ExecutorService executor;
    private ServerSocket serverSocket;
    private ClientRunnable clientSession;
    private UserController userController;
    private CardTable tableSortedByCardNumber;
    private CardTable tableSortedByEncryptedNumber;
    private ServerMessageLogger logger;

    @FXML
    private TextArea textAreaLog;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initialiseClassVariables();
        loadCardTables();
        startServerThread();
    }

    private void initialiseClassVariables() {
        executor = Executors.newCachedThreadPool();
        tableSortedByCardNumber = new TableSortedByCardNumber();
        tableSortedByEncryptedNumber = new TableSortedByEncryptedNumber();
        logger = new ServerMessageLogger(textAreaLog);
        userController = new UserController();
        userController.loadUsers();
    }

    private void loadCardTables() {
        tableSortedByCardNumber.loadCardTable();
        tableSortedByEncryptedNumber.loadCardTable();
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
        logger.displayMessage("Welcome, server administrator.");
        while (true) {
            connectToClient();
            executeSession();
        }
    }

    private void connectToClient() throws IOException {
        Socket clientSocket = serverSocket.accept();
        initialiseClientSession(clientSocket);
    }

    private void initialiseClientSession(Socket clientSocket) throws IOException {
        clientSession = new ClientRunnable(clientSocket, textAreaLog);
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
        Parent parent = FXMLLoader.load(getClass().getResource("../registration/registration.fxml"));
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
            userController.loadUsers();
            logger.displayMessage(userController.getRegisteredUsers().toString());
        } catch (NullPointerException nullPointerException) {
            logger.displayMessage("Error: Could not find file to update.");
            nullPointerException.printStackTrace();
        }
    }

    @FXML
    void clickButtonViewCardsSortedByEncryption() {
        logger.displayMessage(String.format("Displaying card table sorted by encryption: \n"
                + "Card number\t\tEncrypted Number\n"
                + "%s", tableSortedByEncryptedNumber.tableToString()));
    }

    @FXML
    void clickButtonViewCardsSortedByNumber() {
        logger.displayMessage(String.format("Displaying card table sorted by card number: \n"
                + "Card number\t\tEncrypted Number\n"
                + "%s", tableSortedByCardNumber.tableToString()));
    }

    @FXML
    void clickButtonExit() {
        Platform.exit();
        executor.shutdown();
        System.exit(0);
    }
}