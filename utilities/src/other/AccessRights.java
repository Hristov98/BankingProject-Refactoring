package other;

public enum AccessRights {
    NONE("NA"),
    ENCRYPTION("E"),
    DECRYPTION("D"),
    FULL("ED");

    private String rights;

    AccessRights(String permissions) {
        rights = permissions;
    }
}
