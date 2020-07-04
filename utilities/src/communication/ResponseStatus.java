package communication;

public enum ResponseStatus {
    SUCCESS("Success"),
    FAILURE("Failure");

    private final String statusMessage;

    ResponseStatus(String statusMessage) {
        this.statusMessage = statusMessage;
    }
}
