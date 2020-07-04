package communication;

public enum RequestType {
    LOGIN("Login"),
    ENCRYPTION("Encryption"),
    DECRYPTION("Decryption");

    private final String type;

    RequestType(String type) {
        this.type = type;
    }
}


