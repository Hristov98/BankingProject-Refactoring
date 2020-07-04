package clientApp;

import communication.*;
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
        Request loginRequest = new LoginRequest(username.getText(), password.getText());
        sendToServer(loginRequest);
    }

    private void sendToServer(Request request) throws IOException {
        outputStream.writeObject(request);
        outputStream.flush();
    }

    private void processResponseToLoginRequest() {
        try {
            Response response = getResponseFromServer();
            processReceivedLoginObject(response);
        } catch (IOException ioException) {
            displayMessage("Input/Output error during client login.");
            ioException.printStackTrace();
        } catch (ClassNotFoundException unknownClassException) {
            displayMessage("Unknown object received during client login.");
            unknownClassException.printStackTrace();
        }
    }

    private Response getResponseFromServer() throws IOException, ClassNotFoundException {
        return (Response) inputStream.readObject();
    }

    private void processReceivedLoginObject(Response response) {
        if (isCorrectResponseType(response, RequestType.LOGIN)) {
            if (isSuccessfulResponse(response)) {
                logInUser();
            } else {
                alertUserForFailedAction(response.getReturnedMessage());
            }
        } else System.err.println("Wrong wrapper class receiver from server.\n");
    }

    private boolean isCorrectResponseType(Response response, RequestType type) {
        return response.getType() == type;
    }

    private boolean isSuccessfulResponse(Response response) {
        return response.getStatus() == ResponseStatus.SUCCESS;
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

    private void alertUserForFailedAction(String failureMessage) {
        Alert failedLoginAlert = new Alert(Alert.AlertType.ERROR);
        failedLoginAlert.setTitle("Error window");
        failedLoginAlert.setHeaderText(failureMessage);
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
            Response response = getResponseFromServer();
            processReceivedEncryptionObject(response);
        } catch (IOException ioException) {
            displayMessage("Input/Output error during card encryption.");
            ioException.printStackTrace();
        } catch (ClassNotFoundException unknownClassException) {
            displayMessage("Unknown object received during card encryption.");
        }
    }

    private void processReceivedEncryptionObject(Response response) {
        if (isCorrectResponseType(response,RequestType.ENCRYPTION) && isSuccessfulResponse(response)) {
            getEncryptedCardNumberFromServer(response);
        } else {
            alertUserForFailedAction(response.getReturnedMessage());
        }
    }

    private void getEncryptedCardNumberFromServer(Response response) {
        encryptedNumber.setText(response.getReturnedMessage());
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
            Response response = getResponseFromServer();
            processReceiverDecryptionObject(response);
        } catch (IOException ioException) {
            displayMessage("Input/Output error during card decryption.");
            ioException.printStackTrace();
        } catch (ClassNotFoundException unknownClassException) {
            displayMessage("Unknown object received during card decryption.");
        }
    }

    private void processReceiverDecryptionObject(Response response) {
        if (isCorrectResponseType(response,RequestType.DECRYPTION) && isSuccessfulResponse(response)) {
            getDecryptedCardNumberFromServer(response);
        }       else  {
            alertUserForFailedAction(response.getReturnedMessage());
        }
    }

    private void getDecryptedCardNumberFromServer(Response response) {
        decryptedNumber.setText(response.getReturnedMessage());
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
