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
    public void initialize(URL location, ResourceBundle resources)
    {
        //set the combo box options
        cmbAccessRights.getItems().removeAll(cmbAccessRights.getItems());
        cmbAccessRights.getItems().addAll("None", "Encryption", "Decryption", "Full Access");
        cmbAccessRights.getSelectionModel().select("None");
    }

    private UserWrapper getUsersFromFile()
    {
        UserWrapper userWrapper =null;
        ObjectInputStream inputStream = null;

        //open input stream to read the file
        try
        {
            inputStream = new ObjectInputStream(new FileInputStream("users.ser"));
        }
        catch (FileNotFoundException fnf)
        {
            System.err.println("Error: Could not find registered user file.\n");
            fnf.printStackTrace();
        }
        catch (IOException io)
        {
            System.err.println("Error: IO exception.\n");
            io.printStackTrace();
        }

        //read the file and save it into a variable
        try
        {
            Object obj=inputStream.readObject();

            if (obj instanceof UserWrapper)
            {
                userWrapper= new UserWrapper(((UserWrapper) obj));
            }
        }
        catch (NullPointerException n)
        {
            userWrapper=new UserWrapper();
        }
        catch (ClassNotFoundException cl)
        {
            System.err.println("Error: Could not recognise class of read object.\n");
        }
        catch (IOException io)
        {
            System.err.println("Error: IO exception.\n");
            io.printStackTrace();
        }

        //close input stream
        try
        {
            inputStream.close();
        }
        catch (IOException io)
        {
            System.err.println("Error while closing streams.");
            io.printStackTrace();
        }

        return userWrapper;
    }

    private void saveUsersToFile(UserWrapper users)
    {
        ObjectOutputStream outputStream = null;

        //open the output stream to write the users into a file
        try
        {
            outputStream = new ObjectOutputStream(new FileOutputStream("users.ser"));
        }
        catch (IOException io)
        {
            System.err.println("Error: Could not open file.");
            io.printStackTrace();
        }

        //write the users into a file
        try
        {
            outputStream.writeObject(users);
        }
        catch (IOException io)
        {
            System.err.println("Error: Could not write to file.");
            io.printStackTrace();

        }

        //close output stream
        try
        {
            outputStream.close();
        }
        catch (IOException io)
        {
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

        //setting enum based on combo box result
        switch(cmbAccessRights.getValue())
        {
            case "None":         access= AccessRights.NONE;       break;
            case "Encryption":   access= AccessRights.ENCRYPTION; break;
            case "Decryption":   access= AccessRights.DECRYPTION; break;
            case "Full Access":  access= AccessRights.FULL;       break;
        }

        User newUser=new User(txtUsername.getText(),txtPassword.getText(),access);

        //adding user to file
        UserWrapper wrapper;
        File userFile=new File("users.ser");
        if (userFile.exists()) //if file exists, load users from it and append to the set of users
        {
            wrapper=getUsersFromFile();
        }
        else wrapper=new UserWrapper(); //else, create a new set

        wrapper.addUser(newUser);
        saveUsersToFile(wrapper);

        //close registration menu
        cmbAccessRights.getScene().getWindow().hide();

    }

    @FXML
    void btnCancelClicked(ActionEvent event) {
        cmbAccessRights.getScene().getWindow().hide();
    }

}
