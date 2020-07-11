package serverCommunicationHandlers;

import communication.Request;
import communication.RequestType;
import serverApp.ServerMessageLogger;

import java.io.ObjectOutputStream;

public class RequestProcessorFactory {
    private final ObjectOutputStream outputStream;
    private final ServerMessageLogger logger;
    private Request clientRequest;
    private String clientName;

    public RequestProcessorFactory(Request clientRequest, ObjectOutputStream outputStream,
                                   ServerMessageLogger logger, String clientName) {
        this.clientRequest = clientRequest;
        this.outputStream = outputStream;
        this.logger = logger;
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
                return new LoginRequestProcessor(clientRequest, outputStream,
                        logger, clientName);
            }
            case ENCRYPTION: {
                return new EncryptionRequestProcessor(clientRequest, outputStream,
                        logger, clientName);
            }
            case DECRYPTION: {
                return new DecryptionRequestProcessor(clientRequest, outputStream,
                        logger, clientName);
            }
            default: {
                return null;
            }
        }
    }
}
