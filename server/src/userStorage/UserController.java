package userStorage;

import java.io.*;

public class UserController {
    private final String USER_FILE_NAME;
    private UserWrapper registeredUsers;

    public UserController(String userFile) {
        USER_FILE_NAME = userFile;
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
            System.err.println("Error: Could not load users.");
            ioException.printStackTrace();
        } catch (ClassNotFoundException unknownClassException) {
            System.err.println("Error: Unknown object loaded.\n");
            unknownClassException.printStackTrace();
        }
    }

    private ObjectInputStream openUserFileToRead() throws IOException {
        File userFile = new File(USER_FILE_NAME);

        if (!userFile.exists()) {
            System.err.println("WARNING: User file does not exist. Creating empty user file.");
            userFile.createNewFile();
            saveUsersToFile(new UserWrapper());
        }

        return new ObjectInputStream(new FileInputStream(USER_FILE_NAME));
    }

    private void saveUsersToFile(UserWrapper users) {
        try {
            ObjectOutputStream outputStream = openUserFileToWrite();
            outputStream.writeObject(users);
            outputStream.close();
        } catch (IOException ioException) {
            System.err.println("Error: Could not save empty container to file.");
            ioException.printStackTrace();
        }
    }

    private ObjectOutputStream openUserFileToWrite() throws IOException {
        return new ObjectOutputStream(new FileOutputStream(USER_FILE_NAME));
    }

    private void setRegisteredUsers(UserWrapper users) {
        registeredUsers = new UserWrapper(users);
    }

    public void addUserToServer(String username, String password, AccessRights rights) {
        User newUser = new User(username, password, rights);
        addUser(newUser);
    }

    private void addUser(User user) {
        loadUsers();
        registeredUsers.addUser(user);
        saveUsersToFile(registeredUsers);
    }
}