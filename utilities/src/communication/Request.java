package communication;

import java.io.Serializable;

public abstract class Request implements Serializable {
    protected RequestType type;

    Request(RequestType type) {
        this.type = type;
    }
}