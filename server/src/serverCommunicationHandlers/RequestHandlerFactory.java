package serverCommunicationHandlers;

import communication.Request;
import communication.RequestType;

import java.io.ObjectOutputStream;

public class RequestHandlerFactory {
    private final ObjectOutputStream outputStream;
    private Request clientRequest;
    private String clientName;

    public RequestHandlerFactory(Request clientRequest, ObjectOutputStream outputStream, String clientName) {
        this.clientRequest = clientRequest;
        this.outputStream = outputStream;
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
                return new LoginRequestHandler(clientRequest, outputStream, clientName);
            }
            case ENCRYPTION: {
                return new EncryptionRequestHandler(clientRequest, outputStream, clientName);
            }
            case DECRYPTION: {
                return new DecryptionRequestHandler(clientRequest, outputStream, clientName);
            }
            default: {
                return null;
            }
        }
    }
}
