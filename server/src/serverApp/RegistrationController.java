package serverApp;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import userStorage.AccessRights;
import userStorage.UserRegistrator;

import java.io.Serializable;
import java.net.URL;
import java.util.ResourceBundle;

public class RegistrationController implements Initializable, Serializable {
    private UserRegistrator userRegistrator;

    @FXML
    private TextField username;

    @FXML
    private TextField password;

    @FXML
    private ComboBox<String> comboBoxAccessRights;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initialiseComboBox();
        initialiseUserRegistrator();
    }

    private void initialiseComboBox() {
        comboBoxAccessRights.getItems().removeAll(comboBoxAccessRights.getItems());
        comboBoxAccessRights.getItems().addAll("ENCRYPTION", "DECRYPTION", "FULL_ACCESS");
        comboBoxAccessRights.getSelectionModel().select("ENCRYPTION");
    }

    private void initialiseUserRegistrator() {
        userRegistrator = new UserRegistrator();
    }

    @FXML
    void clickButtonAddUser() {
        AccessRights accessRights = getAccessRights();
        userRegistrator.setUserData(username.getText(), password.getText(), accessRights);
        userRegistrator.addUserToServer();
        comboBoxAccessRights.getScene().getWindow().hide();
    }

    private AccessRights getAccessRights() {
        return AccessRights.valueOf(comboBoxAccessRights.getValue());
    }

    @FXML
    void clickButtonCancel() {
        comboBoxAccessRights.getScene().getWindow().hide();
    }
}