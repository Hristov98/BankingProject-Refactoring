package serverApp;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import userStorage.AccessRights;
import userStorage.UserController;

import java.io.Serializable;
import java.net.URL;
import java.util.ResourceBundle;

public class RegistrationController implements Initializable, Serializable {
    private UserController registrator;

    @FXML
    private TextField username;

    @FXML
    private TextField password;

    @FXML
    private ComboBox<String> comboBoxAccessRights;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initialiseComboBox();
        registrator = new UserController("users.ser");
    }

    private void initialiseComboBox() {
        comboBoxAccessRights.getItems().removeAll(comboBoxAccessRights.getItems());
        comboBoxAccessRights.getItems().addAll("ENCRYPTION", "DECRYPTION", "FULL_ACCESS");
        comboBoxAccessRights.getSelectionModel().select("ENCRYPTION");
    }

    @FXML
    void clickButtonAddUser() {
        addUserFromInterfaceInput();
        closeWindow();
    }
    
    private void addUserFromInterfaceInput(){
        AccessRights accessRights = getAccessRights();
        registrator.addUserToServer(username.getText(), password.getText(), accessRights);
    }

    private AccessRights getAccessRights() {
        return AccessRights.valueOf(comboBoxAccessRights.getValue());
    }

    private void closeWindow(){
        comboBoxAccessRights.getScene().getWindow().hide();
    }

    @FXML
    void clickButtonCancel() {
        comboBoxAccessRights.getScene().getWindow().hide();
    }
}