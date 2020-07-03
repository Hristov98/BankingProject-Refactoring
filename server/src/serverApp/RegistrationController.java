package serverApp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

import other.AccessRights;
import other.User;
import other.UserWrapper;

public class RegistrationController implements Initializable, Serializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cmbAccessRights.getItems().removeAll(cmbAccessRights.getItems());
        cmbAccessRights.getItems().addAll("None", "Encryption", "Decryption", "Full Access");
        cmbAccessRights.getSelectionModel().select("None");
    }

    private UserWrapper getUsersFromFile() {
        UserWrapper userWrapper = null;
        ObjectInputStream inputStream = null;

        try {
            inputStream = new ObjectInputStream(new FileInputStream("users.ser"));
        } catch (FileNotFoundException fnf) {
            System.err.println("Error: Could not find registered user file.\n");
            fnf.printStackTrace();
        } catch (IOException io) {
            System.err.println("Error: IO exception.\n");
            io.printStackTrace();
        }

        try {
            Object obj = inputStream.readObject();

            if (obj instanceof UserWrapper) {
                userWrapper = new UserWrapper(((UserWrapper) obj));
            }
        } catch (NullPointerException n) {
            userWrapper = new UserWrapper();
        } catch (ClassNotFoundException cl) {
            System.err.println("Error: Could not recognise class of read object.\n");
        } catch (IOException io) {
            System.err.println("Error: IO exception.\n");
            io.printStackTrace();
        }

        try {
            inputStream.close();
        } catch (IOException io) {
            System.err.println("Error while closing streams.");
            io.printStackTrace();
        }

        return userWrapper;
    }

    private void saveUsersToFile(UserWrapper users) {
        ObjectOutputStream outputStream = null;

        try {
            outputStream = new ObjectOutputStream(new FileOutputStream("users.ser"));
        } catch (IOException io) {
            System.err.println("Error: Could not open file.");
            io.printStackTrace();
        }

        try {
            outputStream.writeObject(users);
        } catch (IOException io) {
            System.err.println("Error: Could not write to file.");
            io.printStackTrace();

        }

        try {
            outputStream.close();
        } catch (IOException io) {
            System.err.println("Error: Could not close output stream.");
            io.printStackTrace();
        }
    }

    @FXML
    private TextField txtUsername;

    @FXML
    private TextField txtPassword;

    @FXML
    private ComboBox<String> cmbAccessRights;

    @FXML
    void btnAddUserClicked(ActionEvent event) {
        AccessRights access = null;

        switch (cmbAccessRights.getValue()) {
            case "None":
                access = AccessRights.NONE;
                break;
            case "Encryption":
                access = AccessRights.ENCRYPTION;
                break;
            case "Decryption":
                access = AccessRights.DECRYPTION;
                break;
            case "Full Access":
                access = AccessRights.FULL;
                break;
        }

        User newUser = new User(txtUsername.getText(), txtPassword.getText(), access);

        UserWrapper wrapper;
        File userFile = new File("users.ser");
        if (userFile.exists())
        {
            wrapper = getUsersFromFile();
        } else wrapper = new UserWrapper();

        wrapper.addUser(newUser);
        saveUsersToFile(wrapper);

        cmbAccessRights.getScene().getWindow().hide();
    }

    @FXML
    void btnCancelClicked(ActionEvent event) {
        cmbAccessRights.getScene().getWindow().hide();
    }

}
