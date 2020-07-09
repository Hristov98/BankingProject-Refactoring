package communication;

import java.io.Serializable;

public abstract class Request implements Serializable {
    protected final RequestType type;

    Request(RequestType type) {
        this.type = type;
    }

    public RequestType getType() {
        return type;
    }
}