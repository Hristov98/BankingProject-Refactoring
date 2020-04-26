package wrappers;

import java.io.Serializable;

//a wrapper class that packages information sent between client and server during login process
public class LoginRequest implements Serializable
{
    private String username;    //client's username
    private String password;    //client's password
    private boolean validUser;  //true if the client's username and password match those of a registered user,
                                //will be set by the server

    public LoginRequest()
    {
        setUsername("");
        setPassword("");
    }

    public LoginRequest(String user, String pass)
    {
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
        this.validUser = validUser;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public boolean isValidUser() {
        return validUser;
    }
}
