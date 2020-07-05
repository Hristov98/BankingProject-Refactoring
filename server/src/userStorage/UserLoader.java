package userStorage;

import serverApp.ServerMessageLogger;

import java.io.*;

public class UserLoader {
    private final String USER_FILE_NAME = "users.ser";
    private UserWrapper registeredUsers;
    private ServerMessageLogger logger;

    public UserLoader(ServerMessageLogger logger) {
        this.logger = logger;
    }

    public UserWrapper getRegisteredUsers() {
        return registeredUsers;
    }

    public void loadUsers() {
        try {
            ObjectInputStream inputStream = openUserFileToRead();
            UserWrapper users = (UserWrapper) inputStream.readObject();
            setRegisteredUsers(users);
            inputStream.close();
        } catch (IOException ioException) {
            logger.displayMessage("Error: Could not load users.");
            ioException.printStackTrace();
        } catch (ClassNotFoundException unknownClassException) {
            logger.displayMessage("Error: Unknown object loaded.\n");
            unknownClassException.printStackTrace();
        }
    }

    private ObjectInputStream openUserFileToRead() throws IOException {
        File userFile = new File(USER_FILE_NAME);

        if (!userFile.exists()) {
            logger.displayMessage("WARNING: User file does not exist. Creating empty user file.");
            userFile.createNewFile();
            saveEmptyContainerToFile();
        }

        return new ObjectInputStream(new FileInputStream(USER_FILE_NAME));
    }

    private void saveEmptyContainerToFile() {
        try {
            ObjectOutputStream outputStream = openUserFileToWrite();
            outputStream.writeObject(new UserWrapper());
            outputStream.close();
        } catch (IOException ioException) {
            logger.displayMessage("Error: Could not save empty container to file.");
            ioException.printStackTrace();
        }
    }

    private ObjectOutputStream openUserFileToWrite() throws IOException {
        return new ObjectOutputStream(new FileOutputStream(USER_FILE_NAME));
    }

    private void setRegisteredUsers(UserWrapper users) {
        registeredUsers = new UserWrapper(users);
    }
}
