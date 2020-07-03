package wrappers;

import java.io.Serializable;

public class LoginRequest implements Serializable {
    private String username;
    private String password;
    private boolean isUserValid;

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

    public void setUserValid(boolean userValid) {
        this.isUserValid = userValid;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public boolean isUserValid() {
        return isUserValid;
    }
}
