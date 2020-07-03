package other;

import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private String password;
    private AccessRights permissions;

    public User() {
        this("", "", null);
    }

    public User(User user) {
        this(user.getUsername(), user.getPassword(), user.getPermissions());
    }

    public User(String user, String pass, AccessRights perm) {
        setUsername(user);
        setPassword(pass);
        setPermissions(perm);
    }

    private void setUsername(String username) {
        this.username = username;
    }

    private void setPassword(String password) {
        this.password = password;
    }

    private void setPermissions(AccessRights permissions) {
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
