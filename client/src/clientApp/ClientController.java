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
    private ObjectOutputStream outputStream;    //output stream to server
    private ObjectInputStream inputStream;      //input stream from server
    private String chatServer;                  //host server for the client to connect to
    private Socket client;                      //socket to communicate with server

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //disable the encryption menu so the user can't access it before logging in
        tabEncryption.setDisable(true);

        //start the client application on a new thread
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
        // create Socket to make connection to server
        client = new Socket(InetAddress.getByName(chatServer), 12345);
    }

    private void getStreams() throws IOException
    {
        //get output stream
        outputStream=new ObjectOutputStream(client.getOutputStream());
        outputStream.flush();

        //get input stream
        inputStream=new ObjectInputStream(client.getInputStream());

        //notify the server that the streams have been loaded
        displayMessage("Client I/O loaded successfully.");
    }

    private void closeConnection() {
        //close streams before shutting down the program
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
        }
        catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void startClient() throws IOException {
        try
        {
            connectToServer();
            getStreams();
        }
        catch (EOFException eofException) {
            displayMessage("Client has terminated the connection.\n");
        }
        catch (IOException ioException) {
            System.err.println("Client IOexception " + ioException.getMessage());
            ioException.printStackTrace();
        }
    }

    //send message to server's input stream, after which it will be timestamped and printed on the text log
    public void displayMessage(String message) throws IOException
    {
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

        //send info to server to verify if user exists
        LoginRequest request=new LoginRequest(txtEnterUsername.getText(),pwdEnterPass.getText());
        outputStream.writeObject(request);
        outputStream.flush();

        try
        {
            //get server's response
            Object obj = inputStream.readObject();

            //process the response
            if (obj instanceof LoginRequest)
            {
                if (((LoginRequest) obj).isValidUser())
                {
                    lblUsername.setText(txtEnterUsername.getText());
                    tabEncryption.setDisable(false);      //enable the encryption menu once the user has logged in
                    tabMenu.getTabs().remove(0);    //remove the login tab
                }
                else
                {
                    //open error window to notify client that the login is unsuccessful
                    Alert alert=new Alert(Alert.AlertType.ERROR);

                    alert.setTitle("Error window");
                    alert.setHeaderText("You have entered an incorrect name and/or password.");
                    alert.setContentText(" Closing connection...");
                    alert.show();

                    closeConnection();
                }
            }
            else System.err.println("Wrong wrapper class receiver from server.\n");
        }
        catch (IOException ex)
        {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
        catch (ClassNotFoundException cl)
        {
            System.err.println("Unknown object received");
        }
    }

    @FXML
    void btnEncryptCardNumberClicked(ActionEvent event) throws IOException {
        displayMessage(String.format("Sending encryption request from %s.",txtEnterUsername.getText()));

        //wrapping the card number in a class and sending it to the server
        EncryptionRequest encReq = new EncryptionRequest(txtDecryptedNumber.getText());
        outputStream.writeObject(encReq);
        outputStream.flush();

        try
         {
             //get server's response
             Object obj = inputStream.readObject();

             //process the response
             if (obj instanceof EncryptionRequest) //if the server sent the encrypted card number
             {
                 txtEncryptedNumber.setText(((EncryptionRequest) obj).getCardNumber());
             }
             if (obj instanceof String) //if the server sent an error message
             {
                 //open error window to notify client that the encryption is unsuccessful
                 Alert alert=new Alert(Alert.AlertType.ERROR);
                 alert.setTitle("Error window");
                 alert.setHeaderText("Error during encryption.");
                 alert.setContentText((String) obj);
                 alert.show();
             }
         }
         catch (IOException ex)
         {
             System.err.println(ex.getMessage());
             ex.printStackTrace();
         }
         catch (ClassNotFoundException cl)
         {
             System.err.println("Unknown object received");
         }

    }

    @FXML
    void btnDecryptCardNumberClicked(ActionEvent event) throws IOException
    {
        displayMessage(String.format("Sending decryption request from %s.",txtEnterUsername.getText()));

        //wrapping the card number in a class and sending it to the server
        DecryptionRequest decReq = new DecryptionRequest(txtEncryptedNumber.getText());
        outputStream.writeObject(decReq);
        outputStream.flush();

        try
        {
            //get server's response
            Object obj = inputStream.readObject();

            //process the response
            if (obj instanceof DecryptionRequest) //if the server sent the decrypted card number
            {
                txtDecryptedNumber.setText(((DecryptionRequest) obj).getCardNumber());
            }
            if (obj instanceof String) //if the server sent an error message
            {
                //open error window to notify client that the decryption is unsuccessful
                Alert alert=new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error window");
                alert.setHeaderText("Error during decryption.");
                alert.setContentText((String) obj);
                alert.show();
            }
        }
        catch (IOException ex)
        {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
        catch (ClassNotFoundException cl)
        {
            System.err.println("Unknown object received");
        }
    }

    @FXML
    void btnExitClicked(ActionEvent event)
    {
        closeConnection();
        Platform.exit();
    }

}
