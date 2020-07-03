package other;

import java.io.Serializable;
import java.util.HashSet;

public class UserWrapper implements Serializable {
    private HashSet<User> list;

    public UserWrapper() {
        list = new HashSet<>();
    }

    public UserWrapper(UserWrapper uw) {
        setSet(uw.getSet());
    }

    public HashSet<User> getSet() {
        return new HashSet<>(list);
    }

    public void setSet(HashSet<User> list) {
        this.list = new HashSet<>(list);
    }

    public void addUser(User user) {
        list.add(user);
    }

    public boolean contains(User user) {
        return list.contains(user);
    }

    @Override
    public String toString() {
        return "User container:\n" + list;
    }
}
