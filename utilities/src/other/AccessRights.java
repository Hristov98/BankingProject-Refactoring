package other;

public enum AccessRights {
    ENCRYPTION("Encryption"),
    DECRYPTION("Decryption"),
    FULL_ACCESS("Full Access");

    private String rights;

    AccessRights(String permissions) {
        rights = permissions;
    }
}
