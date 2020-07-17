package userStorage;

import java.io.Serializable;
import java.util.ArrayList;

public class UserWrapper implements Serializable {
    private ArrayList<User> users;

    public UserWrapper() {
        users = new ArrayList<>();
    }

    public UserWrapper(UserWrapper userWrapper) {
        setUsers(userWrapper.getUsers());
    }

    public ArrayList<User> getUsers() {
        return new ArrayList<>(users);
    }

    public void setUsers(ArrayList<User> users) {
        this.users = new ArrayList<>(users);
    }

    public void addUser(User user) {
        if (doesNotContainUser(user)) {
            users.add(user);
        } else {
            System.err.println("Error: The entered username or password already exist.");
        }
    }

    private boolean doesNotContainUser(User newUser) {
        for (User registeredUser : users) {
            if (usernameAlreadyExists(registeredUser,newUser)
                    || passwordAlreadyExists(registeredUser,newUser)) {
                return false;
            }
        }

        return true;
    }

    private boolean usernameAlreadyExists(User registeredUser, User newUser) {
        return registeredUser.getUsername().equals(newUser.getUsername());
    }

    private boolean passwordAlreadyExists(User registeredUser, User newUser) {
        return registeredUser.getPassword().equals(newUser.getPassword());
    }

    @Override
    public String toString() {
        return "User container:\n" + users;
    }
}