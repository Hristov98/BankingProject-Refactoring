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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tabEncryption.setDisable(true);

        Thread thread = new Thread(() -> {
            try {
                startClient();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        thread.start();
    }

    private void connectToServer() throws IOException {
        clientSocket = new Socket(InetAddress.getByName(clientNetworkAddress), 12345);
    }

    private void getInputAndOutputStream() throws IOException {
        outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        outputStream.flush();
        inputStream = new ObjectInputStream(clientSocket.getInputStream());

        displayMessage("Client Input/Output streams loaded successfully.");
    }

    private void disconnectFromServer() {
        try {
            if (outputStream != null) {
                outputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void startClient() throws IOException {
        try {
            connectToServer();
            getInputAndOutputStream();
        } catch (EOFException eofException) {
            displayMessage("Client has terminated the connection.\n");
        } catch (IOException ioException) {
            System.err.println("Client IOException " + ioException.getMessage());
            ioException.printStackTrace();
        }
    }

    public void displayMessage(String message) throws IOException {
        outputStream.writeObject(message);
        outputStream.flush();
    }

    @FXML
    private TabPane tabMenu;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Tab tabEncryption;

    @FXML
    private TextField decryptedNumber;

    @FXML
    private TextField encryptedNumber;

    @FXML
    private Label labelLoggedInUsername;

    @FXML
    void clickButtonContinue() throws IOException {
        displayMessage("Sending login request from client.");

        LoginRequest loginRequest = new LoginRequest(username.getText(), password.getText());
        outputStream.writeObject(loginRequest);
        outputStream.flush();

        try {
            Object object = inputStream.readObject();

            if (object instanceof LoginRequest) {
                if (((LoginRequest) object).isValidUser()) {
                    labelLoggedInUsername.setText(username.getText());
                    tabEncryption.setDisable(false);
                    tabMenu.getTabs().remove(0);
                } else {
                    Alert failedLoginAlert = new Alert(Alert.AlertType.ERROR);
                    failedLoginAlert.setTitle("Error window");
                    failedLoginAlert.setHeaderText("You have entered an incorrect name and/or password.");
                    failedLoginAlert.setContentText(" Closing connection...");
                    failedLoginAlert.show();

                    disconnectFromServer();
                }
            } else System.err.println("Wrong wrapper class receiver from server.\n");
        } catch (IOException ioException) {
            System.err.println(ioException.getMessage());
            ioException.printStackTrace();
        } catch (ClassNotFoundException unknownClassException) {
            System.err.println("Unknown object received");
        }
    }

    @FXML
    void clickButtonEncryptCardNumber() throws IOException {
        displayMessage(String.format("Sending encryption request from %s.", username.getText()));

        EncryptionRequest encryptionRequest = new EncryptionRequest(decryptedNumber.getText());
        outputStream.writeObject(encryptionRequest);
        outputStream.flush();

        try {
            Object object = inputStream.readObject();

            if (object instanceof EncryptionRequest) {
                encryptedNumber.setText(((EncryptionRequest) object).getCardNumber());
            }
            if (object instanceof String) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error window");
                alert.setHeaderText("Error during encryption.");
                alert.setContentText((String) object);
                alert.show();
            }
        } catch (IOException ioException) {
            System.err.println(ioException.getMessage());
            ioException.printStackTrace();
        } catch (ClassNotFoundException unknownClassException) {
            System.err.println("Unknown object received");
        }

    }

    @FXML
    void clickButtonDecryptCardNumber() throws IOException {
        displayMessage(String.format("Sending decryption request from %s.", username.getText()));

        DecryptionRequest decryptionRequest = new DecryptionRequest(encryptedNumber.getText());
        outputStream.writeObject(decryptionRequest);
        outputStream.flush();

        try {
            Object object = inputStream.readObject();

            if (object instanceof DecryptionRequest) {
                decryptedNumber.setText(((DecryptionRequest) object).getCardNumber());
            }
            if (object instanceof String) {
                Alert failedLoginAlert = new Alert(Alert.AlertType.ERROR);
                failedLoginAlert.setTitle("Error window");
                failedLoginAlert.setHeaderText("Error during decryption.");
                failedLoginAlert.setContentText((String) object);
                failedLoginAlert.show();
            }
        } catch (IOException ioException) {
            System.err.println(ioException.getMessage());
            ioException.printStackTrace();
        } catch (ClassNotFoundException cl) {
            System.err.println("Unknown object received");
        }
    }

    @FXML
    void clickButtonExit() {
        disconnectFromServer();
        Platform.exit();
    }
}
