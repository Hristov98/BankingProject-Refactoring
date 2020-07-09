package userStorage;

import java.io.Serializable;

public class User implements Serializable {
    private final String username;
    private final String password;
    private final AccessRights permissions;

    public User(String username, String password, AccessRights permissions) {
        this.username = username;
        this.password = password;
        this.permissions = permissions;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public AccessRights getPermissions() {
        return permissions;
    }

    @Override
    public String toString() {
        return String.format("Username: %s, Password: %s Access Rights: %s\n",
                username, password, permissions.name());
    }
}