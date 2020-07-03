package other;

import java.io.Serializable;
import java.util.HashSet;

public class UserWrapper implements Serializable {
    private HashSet<User> users;

    public UserWrapper() {
        users = new HashSet<>();
    }

    public UserWrapper(UserWrapper userWrapper) {
        setUsers(userWrapper.getUsers());
    }

    public HashSet<User> getUsers() {
        return new HashSet<>(users);
    }

    public void setUsers(HashSet<User> users) {
        this.users = new HashSet<>(users);
    }

    public void addUser(User user) {
        users.add(user);
    }

    public boolean contains(User user) {
        return users.contains(user);
    }

    @Override
    public String toString() {
        return "User container:\n" + users;
    }
}
