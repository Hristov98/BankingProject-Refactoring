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
        boolean isLoginResponseValid = loginHandler.processResponseFromServer(inputStream, logger);

        if (isLoginResponseValid) {
            logInUser();
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

    @FXML
    void clickButtonEncryptCardNumber() throws IOException {
        logger.displayMessageOnServer(String.format("Received encryption request from %s.",
                username.getText()));

        ActionHandler encryptionHandler = new EncryptionHandler(decryptedNumber.getText());
        encryptionHandler.sendRequestToServer(outputStream);
        boolean isEncryptionResponseValid = encryptionHandler.processResponseFromServer(inputStream, logger);

        if (isEncryptionResponseValid) {
            setEncryptedCardNumber(encryptionHandler.getResponseMessage());
        }
    }

    private void setEncryptedCardNumber(String cardNumber) {
        encryptedNumber.setText(cardNumber);
    }

    @FXML
    void clickButtonDecryptCardNumber() throws IOException {
        logger.displayMessageOnServer(String.format("Received decryption request from %s.",
                username.getText()));

        ActionHandler decryptionHandler = new DecryptionHandler(encryptedNumber.getText());
        decryptionHandler.sendRequestToServer(outputStream);
        boolean isDecryptionResponseValid = decryptionHandler.processResponseFromServer(inputStream, logger);

        if (isDecryptionResponseValid) {
            setDecryptedCardNumber(decryptionHandler.getResponseMessage());
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