package serverCommunicationHandlers;

import communication.Request;
import communication.RequestType;

import java.io.ObjectOutputStream;

public class RequestProcessorFactory {
    private final ObjectOutputStream outputStream;
    private Request clientRequest;
    private String clientName;

    public RequestProcessorFactory(Request clientRequest, ObjectOutputStream outputStream,
                                   String clientName) {
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

    public RequestProcessor createRequestProcessor(RequestType type) {
        switch (type) {
            case LOGIN: {
                return new LoginRequestProcessor(clientRequest, outputStream, clientName);
            }
            case ENCRYPTION: {
                return new EncryptionRequestProcessor(clientRequest, outputStream, clientName);
            }
            case DECRYPTION: {
                return new DecryptionRequestProcessor(clientRequest, outputStream, clientName);
            }
            default: {
                return null;
            }
        }
    }
}
