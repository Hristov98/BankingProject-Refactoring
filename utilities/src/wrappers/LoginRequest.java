package wrappers;

import java.io.Serializable;

public class LoginRequest implements Serializable {
    private String username;
    private String password;
    private boolean isValidUser;

    public LoginRequest() {
        setUsername("");
        setPassword("");
    }

    public LoginRequest(String user, String pass) {
        setUsername(user);
        setPassword(pass);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setValidUser(boolean validUser) {
        this.isValidUser = validUser;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public boolean isValidUser() {
        return isValidUser;
    }
}
