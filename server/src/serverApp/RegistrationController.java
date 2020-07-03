package serverApp;

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
        comboBoxAccessRights.getItems().removeAll(comboBoxAccessRights.getItems());
        comboBoxAccessRights.getItems().addAll("None", "Encryption", "Decryption", "Full Access");
        comboBoxAccessRights.getSelectionModel().select("None");
    }

    private UserWrapper getUsersFromFile() {
        UserWrapper userWrapper = null;
        ObjectInputStream inputStream = null;

        try {
            inputStream = new ObjectInputStream(new FileInputStream("users.ser"));
        } catch (FileNotFoundException fileNotFoundException) {
            System.err.println("Error: Could not find registered user file.\n");
            fileNotFoundException.printStackTrace();
        } catch (IOException ioException) {
            System.err.println("Error: IO exception.\n");
            ioException.printStackTrace();
        }

        try {
            Object object = inputStream.readObject();

            if (object instanceof UserWrapper) {
                userWrapper = new UserWrapper(((UserWrapper) object));
            }
        } catch (NullPointerException nullPointerException) {
            userWrapper = new UserWrapper();
        } catch (ClassNotFoundException unknownClassException) {
            System.err.println("Error: Could not recognise class of read object.\n");
        } catch (IOException ioException) {
            System.err.println("Error: Input/Output exception.\n");
            ioException.printStackTrace();
        }

        try {
            inputStream.close();
        } catch (IOException ioException) {
            System.err.println("Error while closing streams.");
            ioException.printStackTrace();
        }

        return userWrapper;
    }

    private void saveUsersToFile(UserWrapper users) {
        ObjectOutputStream outputStream = null;

        try {
            outputStream = new ObjectOutputStream(new FileOutputStream("users.ser"));
        } catch (IOException ioException) {
            System.err.println("Error: Could not open file.");
            ioException.printStackTrace();
        }

        try {
            outputStream.writeObject(users);
        } catch (IOException ioException) {
            System.err.println("Error: Could not write to file.");
            ioException.printStackTrace();

        }

        try {
            outputStream.close();
        } catch (IOException ioException) {
            System.err.println("Error: Could not close output stream.");
            ioException.printStackTrace();
        }
    }

    @FXML
    private TextField username;

    @FXML
    private TextField password;

    @FXML
    private ComboBox<String> comboBoxAccessRights;

    @FXML
    void clickButtonAddUser() {
        AccessRights accessRights = null;

        switch (comboBoxAccessRights.getValue()) {
            case "None":
                accessRights = AccessRights.NONE;
                break;
            case "Encryption":
                accessRights = AccessRights.ENCRYPTION;
                break;
            case "Decryption":
                accessRights = AccessRights.DECRYPTION;
                break;
            case "Full Access":
                accessRights = AccessRights.FULL;
                break;
        }

        User newUser = new User(username.getText(), password.getText(), accessRights);

        UserWrapper users;
        File userFile = new File("users.ser");
        if (userFile.exists()) {
            users = getUsersFromFile();
        } else users = new UserWrapper();

        users.addUser(newUser);
        saveUsersToFile(users);

        comboBoxAccessRights.getScene().getWindow().hide();
    }

    @FXML
    void clickButtonCancel() {
        comboBoxAccessRights.getScene().getWindow().hide();
    }
}
