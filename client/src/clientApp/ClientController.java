package clientApp;

import javafx.application.Platform;
import javafx.event.ActionEvent;
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
    private String chatServer;
    private Socket client;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tabEncryption.setDisable(true);

        Thread thread = new Thread(() -> {
            try {
                startClient();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    private void connectToServer() throws IOException {
        client = new Socket(InetAddress.getByName(chatServer), 12345);
    }

    private void getStreams() throws IOException {
        outputStream = new ObjectOutputStream(client.getOutputStream());
        outputStream.flush();
        inputStream = new ObjectInputStream(client.getInputStream());

        displayMessage("Client I/O loaded successfully.");
    }

    private void closeConnection() {
        try {

            if (outputStream != null) {
                outputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (client != null) {
                client.close();
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void startClient() throws IOException {
        try {
            connectToServer();
            getStreams();
        } catch (EOFException eofException) {
            displayMessage("Client has terminated the connection.\n");
        } catch (IOException ioException) {
            System.err.println("Client IOexception " + ioException.getMessage());
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
    private TextField txtEnterUsername;

    @FXML
    private PasswordField pwdEnterPass;

    @FXML
    private Tab tabEncryption;

    @FXML
    private TextField txtDecryptedNumber;

    @FXML
    private TextField txtEncryptedNumber;

    @FXML
    private Label lblUsername;


    @FXML
    void btnContinueClicked(ActionEvent event) throws IOException {
        displayMessage("Sending login request from client.");

        LoginRequest request = new LoginRequest(txtEnterUsername.getText(), pwdEnterPass.getText());
        outputStream.writeObject(request);
        outputStream.flush();

        try {
            Object obj = inputStream.readObject();

            if (obj instanceof LoginRequest) {
                if (((LoginRequest) obj).isValidUser()) {
                    lblUsername.setText(txtEnterUsername.getText());
                    tabEncryption.setDisable(false);
                    tabMenu.getTabs().remove(0);
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error window");
                    alert.setHeaderText("You have entered an incorrect name and/or password.");
                    alert.setContentText(" Closing connection...");
                    alert.show();

                    closeConnection();
                }
            } else System.err.println("Wrong wrapper class receiver from server.\n");
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        } catch (ClassNotFoundException cl) {
            System.err.println("Unknown object received");
        }
    }

    @FXML
    void btnEncryptCardNumberClicked(ActionEvent event) throws IOException {
        displayMessage(String.format("Sending encryption request from %s.", txtEnterUsername.getText()));

        EncryptionRequest encReq = new EncryptionRequest(txtDecryptedNumber.getText());
        outputStream.writeObject(encReq);
        outputStream.flush();

        try {
            Object obj = inputStream.readObject();

            if (obj instanceof EncryptionRequest) {
                txtEncryptedNumber.setText(((EncryptionRequest) obj).getCardNumber());
            }
            if (obj instanceof String) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error window");
                alert.setHeaderText("Error during encryption.");
                alert.setContentText((String) obj);
                alert.show();
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        } catch (ClassNotFoundException cl) {
            System.err.println("Unknown object received");
        }

    }

    @FXML
    void btnDecryptCardNumberClicked(ActionEvent event) throws IOException {
        displayMessage(String.format("Sending decryption request from %s.", txtEnterUsername.getText()));

        DecryptionRequest decReq = new DecryptionRequest(txtEncryptedNumber.getText());
        outputStream.writeObject(decReq);
        outputStream.flush();

        try {
            Object obj = inputStream.readObject();

            if (obj instanceof DecryptionRequest) {
                txtDecryptedNumber.setText(((DecryptionRequest) obj).getCardNumber());
            }
            if (obj instanceof String) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error window");
                alert.setHeaderText("Error during decryption.");
                alert.setContentText((String) obj);
                alert.show();
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        } catch (ClassNotFoundException cl) {
            System.err.println("Unknown object received");
        }
    }

    @FXML
    void btnExitClicked(ActionEvent event) {
        closeConnection();
        Platform.exit();
    }

}
