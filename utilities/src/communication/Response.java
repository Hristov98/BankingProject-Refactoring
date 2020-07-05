package communication;

import java.io.Serializable;

public class Response implements Serializable {
    private ResponseStatus status;
    private String returnedMessage;

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