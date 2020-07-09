package userStorage;

public enum AccessRights {
    ENCRYPTION("Encryption"),
    DECRYPTION("Decryption"),
    FULL_ACCESS("Full Access");

    private final String rights;

    AccessRights(String permissions) {
        rights = permissions;
    }
}