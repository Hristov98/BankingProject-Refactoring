package communication;

import java.io.Serializable;

public class LoginRequest extends Request implements Serializable {
    private final String username;
    private final String password;

    public LoginRequest(String username, String password) {
        super(RequestType.LOGIN);
        this.password = password;
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }
}