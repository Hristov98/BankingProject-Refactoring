package userControllers;

import other.AccessRights;
import other.User;
import other.UserWrapper;

import java.io.*;

public class UserRegistrator {
    private final String USER_FILE_NAME = "users.ser";
    private UserWrapper registeredUsers;
    private String username;
    private String password;
    private AccessRights rights;

    public void setUserData(String username, String password, AccessRights rights) {
        this.username = username;
        this.password = password;
        this.rights = rights;
    }

    public void addUserToServer() {
        User newUser = new User(username, password, rights);
        addUser(newUser);
    }

    private void addUser(User user) {
        getUsersFromFile();
        registeredUsers.addUser(user);
        saveUsersToFile();
    }

    private void getUsersFromFile() {
        File userFile = new File(USER_FILE_NAME);
        if (!userFile.exists()) {
            registeredUsers = new UserWrapper();
        } else {
            readUserFile();
        }
    }

    private void readUserFile() {
        try {
            ObjectInputStream inputStream = openUserFileToRead();
            UserWrapper users = (UserWrapper) inputStream.readObject();
            registeredUsers = new UserWrapper(users);
            inputStream.close();
        } catch (FileNotFoundException fileNotFoundException) {
            System.err.println("Error: Could not find registered user file.\n");
            fileNotFoundException.printStackTrace();
        } catch (IOException ioException) {
            System.err.println("Error: Could not add new user to container.\n");
            ioException.printStackTrace();
        } catch (ClassNotFoundException unknownClassException) {
            System.err.println("Error: Could not recognise class of read object.\n");
        }
    }

    private ObjectInputStream openUserFileToRead() throws IOException {
        return new ObjectInputStream(new FileInputStream(USER_FILE_NAME));
    }

    private void saveUsersToFile() {
        try {
            ObjectOutputStream outputStream = openUserFileToWrite();
            outputStream.writeObject(registeredUsers);
            outputStream.close();
        } catch (IOException ioException) {
            System.err.println("Error: Could not save users to file after registration.");
            ioException.printStackTrace();
        }
    }

    private ObjectOutputStream openUserFileToWrite() throws IOException {
        return new ObjectOutputStream(new FileOutputStream(USER_FILE_NAME));
    }
}
