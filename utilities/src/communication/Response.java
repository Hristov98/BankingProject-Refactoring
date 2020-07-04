package communication;

import java.io.Serializable;

public class Response implements Serializable {
    private RequestType type;
    private ResponseStatus status;
    private String returnedMessage;

    public Response(RequestType type, ResponseStatus status, String returnedMessage) {
        this.type = type;
        this.status = status;
        this.returnedMessage = returnedMessage;
    }

    public RequestType getType() {
        return type;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public String getReturnedMessage() {
        return returnedMessage;
    }
}
