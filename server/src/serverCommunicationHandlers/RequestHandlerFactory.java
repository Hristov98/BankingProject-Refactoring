package serverCommunicationHandlers;

import communication.Request;
import communication.RequestType;

public class RequestHandlerFactory {
    private Request clientRequest;
    private String clientName;

    public RequestHandlerFactory(Request clientRequest, String clientName) {
        this.clientRequest = clientRequest;
        this.clientName = clientName;
    }

    public void setClientRequest(Request clientRequest) {
        this.clientRequest = clientRequest;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public RequestHandler createRequestProcessor(RequestType type) {
        switch (type) {
            case LOGIN: {
                return new LoginRequestHandler(clientRequest, clientName);
            }
            case ENCRYPTION: {
                return new EncryptionRequestHandler(clientRequest, clientName);
            }
            case DECRYPTION: {
                return new DecryptionRequestHandler(clientRequest, clientName);
            }
            default: {
                return null;
            }
        }
    }
}
