package clientApp;

import clientCommunicationHandlers.ActionHandler;
import clientCommunicationHandlers.DecryptionHandler;
import clientCommunicationHandlers.EncryptionHandler;
import clientCommunicationHandlers.LoginHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientController implements Initializable {
    private Socket clientSocket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private ClientMessageLogger logger;

    @FXML
    private TabPane tabMenu;

    @FXML
    private Tab tabEncryption;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private TextField decryptedNumber;

    @FXML
    private TextField encryptedNumber;

    @FXML
    private Label labelLoggedInUsername;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        disableEncryptionTab();
        startClientThread();
    }

    private void disableEncryptionTab() {
        tabEncryption.setDisable(true);
    }

    private void startClientThread() {
        Thread thread = new Thread(() -> {
            try {
                startClient();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        thread.start();
    }

    private void startClient() throws IOException {
        try {
            connectToServer();
            initialiseInputStream();
            initialiseOutputStream();
            initialiseMessageLogger();
            logger.displayMessageOnServer("Client Input/Output streams loaded successfully.");
        } catch (EOFException eofException) {
            logger.displayMessageOnServer("Client has terminated the connection.\n");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void connectToServer() throws IOException {
        clientSocket = new Socket(InetAddress.getByName(""), 12345);
    }

    private void initialiseInputStream() throws IOException {
        inputStream = new ObjectInputStream(clientSocket.getInputStream());
    }

    private void initialiseOutputStream() throws IOException {
        outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        outputStream.flush();
    }

    private void initialiseMessageLogger() {
        logger = new ClientMessageLogger(outputStream);
    }

    @FXML
    void clickButtonContinue() throws IOException {
        logger.displayMessageOnServer("Received login request from client.");

        ActionHandler loginHandler = new LoginHandler(username.getText(), password.getText());
        loginHandler.sendRequestToServer(outputStream);
        processLoginResponse(loginHandler);
    }

    private void processLoginResponse(ActionHandler handler){
        boolean loginIsSuccessful = handler.processResponseFromServer(inputStream);

        if (loginIsSuccessful) {
            logInUser();
        } else {
            alertUserForFailedAction(handler.getResponseMessage());
        }
    }

    private void logInUser() {
        moveUserToEncryptionTab();
        hideLoginTab();
    }

    private void moveUserToEncryptionTab() {
        labelLoggedInUsername.setText(username.getText());
        tabEncryption.setDisable(false);
    }

    private void hideLoginTab() {
        tabMenu.getTabs().remove(0);
    }

    private void alertUserForFailedAction(String failureMessage) {
        Alert failedLoginAlert = new Alert(Alert.AlertType.ERROR);
        failedLoginAlert.setTitle("Error window");
        failedLoginAlert.setHeaderText(failureMessage);
        failedLoginAlert.show();
    }

    @FXML
    void clickButtonEncryptCardNumber() throws IOException {
        logger.displayMessageOnServer(String.format("Received encryption request from %s.", username.getText()));

        ActionHandler encryptionHandler = new EncryptionHandler(decryptedNumber.getText());
        encryptionHandler.sendRequestToServer(outputStream);
        processEncryptionResponse(encryptionHandler);
    }

    private void processEncryptionResponse(ActionHandler handler){
        boolean encryptionIsSuccessful = handler.processResponseFromServer(inputStream);

        if (encryptionIsSuccessful) {
            setEncryptedCardNumber(handler.getResponseMessage());
        } else {
            alertUserForFailedAction(handler.getResponseMessage());
        }
    }

    private void setEncryptedCardNumber(String cardNumber) {
        encryptedNumber.setText(cardNumber);
    }

    @FXML
    void clickButtonDecryptCardNumber() throws IOException {
        logger.displayMessageOnServer(String.format("Received decryption request from %s.", username.getText()));

        ActionHandler decryptionHandler = new DecryptionHandler(encryptedNumber.getText());
        decryptionHandler.sendRequestToServer(outputStream);
        processDecryptionResponse(decryptionHandler);
    }

    private void processDecryptionResponse(ActionHandler handler){
        boolean decryptionIsSuccessful = handler.processResponseFromServer(inputStream);

        if (decryptionIsSuccessful) {
            setDecryptedCardNumber(handler.getResponseMessage());
        } else {
            alertUserForFailedAction(handler.getResponseMessage());
        }
    }

    private void setDecryptedCardNumber(String cardNumber) {
        decryptedNumber.setText(cardNumber);
    }

    @FXML
    void clickButtonExit() {
        disconnectFromServer();
        closeUserInterface();
    }

    private void disconnectFromServer() {
        try {
            closeInputStream();
            closeOutputStream();
            closeClientSocket();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void closeOutputStream() throws IOException {
        if (outputStream != null) {
            outputStream.close();
        }
    }

    private void closeInputStream() throws IOException {
        if (inputStream != null) {
            inputStream.close();
        }
    }

    private void closeClientSocket() throws IOException {
        if (clientSocket != null) {
            clientSocket.close();
        }
    }

    private void closeUserInterface() {
        Platform.exit();
    }
}