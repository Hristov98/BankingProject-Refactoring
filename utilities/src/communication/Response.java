package communication;

import java.io.Serializable;

public class Response implements Serializable {
    private final ResponseStatus status;
    private final String returnedMessage;

    public Response(ResponseStatus status, String returnedMessage) {
        this.status = status;
        this.returnedMessage = returnedMessage;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public String getReturnedMessage() {
        return returnedMessage;
    }
}