package clientApp;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import wrappers.DecryptionRequest;
import wrappers.EncryptionRequest;
import wrappers.LoginRequest;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientController implements Initializable {
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private String clientNetworkAddress;
    private Socket clientSocket;

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
            setInputStream();
            setOutputStream();
            displayMessage("Client Input/Output streams loaded successfully.");
        } catch (EOFException eofException) {
            displayMessage("Client has terminated the connection.\n");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void connectToServer() throws IOException {
        clientSocket = new Socket(InetAddress.getByName(clientNetworkAddress), 12345);
    }

    private void setInputStream() throws IOException {
        inputStream = new ObjectInputStream(clientSocket.getInputStream());
    }

    private void setOutputStream() throws IOException {
        outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        outputStream.flush();
    }

    private void displayMessage(String message) {
        try {
            sendMessageToServer(message);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void sendMessageToServer(String message) throws IOException {
        outputStream.writeObject(message);
        outputStream.flush();
    }

    @FXML
    void clickButtonContinue() throws IOException {
        displayMessage("Sending login request from client to server.");
        sendLoginRequestToServer();
        processResponseToLoginRequest();
    }

    private void sendLoginRequestToServer() throws IOException {
        LoginRequest loginRequest = new LoginRequest(username.getText(), password.getText());
        outputStream.writeObject(loginRequest);
        outputStream.flush();
    }

    private void processResponseToLoginRequest() {
        try {
            Object response = getResponseFromServer();
            processReceivedLoginObject(response);
        } catch (IOException ioException) {
            displayMessage("Input/Output error during client login.");
            ioException.printStackTrace();
        } catch (ClassNotFoundException unknownClassException) {
            displayMessage("Unknown object received during client login.");
            unknownClassException.printStackTrace();
        }
    }

    private Object getResponseFromServer() throws IOException, ClassNotFoundException {
        return inputStream.readObject();
    }

    private void processReceivedLoginObject(Object response) {
        if (isResponseALoginRequest(response)) {
            if (((LoginRequest) response).isUserValid()) {
                logInUser();
            } else {
                alertUserForFailedLogin();
                disconnectFromServer();
            }
        } else System.err.println("Wrong wrapper class receiver from server.\n");
    }

    private boolean isResponseALoginRequest(Object response) {
        return response instanceof LoginRequest;
    }

    private void logInUser() {
        moveUserToEncryptionDecryptionTab();
        hideLoginTab();
    }

    private void moveUserToEncryptionDecryptionTab() {
        labelLoggedInUsername.setText(username.getText());
        tabEncryption.setDisable(false);
    }

    private void hideLoginTab() {
        tabMenu.getTabs().remove(0);
    }

    private void alertUserForFailedLogin() {
        Alert failedLoginAlert = new Alert(Alert.AlertType.ERROR);
        failedLoginAlert.setTitle("Error window");
        failedLoginAlert.setHeaderText("You have entered an incorrect name and/or password.");
        failedLoginAlert.setContentText(" Closing connection...");
        failedLoginAlert.show();
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

    @FXML
    void clickButtonEncryptCardNumber() throws IOException {
        displayMessage(String.format("Sending encryption request from %s.", username.getText()));
        sendEncryptionRequestToServer();
        processResponseToEncryptionRequest();
    }

    private void sendEncryptionRequestToServer() throws IOException {
        EncryptionRequest encryptionRequest = new EncryptionRequest(decryptedNumber.getText());
        outputStream.writeObject(encryptionRequest);
        outputStream.flush();
    }

    private void processResponseToEncryptionRequest() {
        try {
            Object response = getResponseFromServer();
            processReceiverEncryptionObject(response);
        } catch (IOException ioException) {
            displayMessage("Input/Output error during card encryption.");
            ioException.printStackTrace();
        } catch (ClassNotFoundException unknownClassException) {
            displayMessage("Unknown object received during card encryption.");
        }
    }

    private void processReceiverEncryptionObject(Object response) {
        if (isResponseAnEncryptionRequest(response)) {
            getEncryptedCardNumberFromServer(response);
        } else if (isResponseAString(response)) {
            alertUserForFailedEncryption(response);
        }
    }

    private boolean isResponseAnEncryptionRequest(Object response) {
        return response instanceof EncryptionRequest;
    }

    private boolean isResponseAString(Object response) {
        return response instanceof String;
    }

    private void getEncryptedCardNumberFromServer(Object response) {
        encryptedNumber.setText(((EncryptionRequest) response).getCardNumber());
    }

    private void alertUserForFailedEncryption(Object response) {
        Alert failedEncryptionAlert = new Alert(Alert.AlertType.ERROR);
        failedEncryptionAlert.setTitle("Error window");
        failedEncryptionAlert.setHeaderText("Error during encryption.");
        failedEncryptionAlert.setContentText((String) response);
        failedEncryptionAlert.show();
    }

    @FXML
    void clickButtonDecryptCardNumber() throws IOException {
        displayMessage(String.format("Sending decryption request from %s.", username.getText()));
        sendDecryptionRequestToServer();
        processResponseToDecryptionRequest();

    }

    private void sendDecryptionRequestToServer() throws IOException {
        DecryptionRequest decryptionRequest = new DecryptionRequest(encryptedNumber.getText());
        outputStream.writeObject(decryptionRequest);
        outputStream.flush();
    }

    private void processResponseToDecryptionRequest() {
        try {
            Object response = getResponseFromServer();
            processReceiverDecryptionObject(response);
        } catch (IOException ioException) {
            displayMessage("Input/Output error during card decryption.");
            ioException.printStackTrace();
        } catch (ClassNotFoundException unknownClassException) {
            displayMessage("Unknown object received during card decryption.");
        }
    }

    private void processReceiverDecryptionObject(Object response) {
        if (isResponseADecryptionRequest(response)) {
            getDecryptedCardNumberFromServer(response);
        }
        if (response instanceof String) {
            alertUserForFailedDecryption(response);
        }
    }

    private boolean isResponseADecryptionRequest(Object response) {
        return response instanceof DecryptionRequest;
    }

    private void getDecryptedCardNumberFromServer(Object response) {
        decryptedNumber.setText(((DecryptionRequest) response).getCardNumber());
    }

    private void alertUserForFailedDecryption(Object response) {
        Alert failedDecryptionAlert = new Alert(Alert.AlertType.ERROR);
        failedDecryptionAlert.setTitle("Error window");
        failedDecryptionAlert.setHeaderText("Error during decryption.");
        failedDecryptionAlert.setContentText((String) response);
        failedDecryptionAlert.show();
    }

    @FXML
    void clickButtonExit() {
        disconnectFromServer();
        closeUserInterface();
    }

    private void closeUserInterface() {
        Platform.exit();
    }
}
