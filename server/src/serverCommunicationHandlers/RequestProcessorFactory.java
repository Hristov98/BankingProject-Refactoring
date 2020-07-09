package serverCommunicationHandlers;

import cardManipulation.BankCardTableController;
import communication.Request;
import communication.RequestType;
import serverApp.ServerMessageLogger;
import userStorage.UserLoader;

import java.io.ObjectOutputStream;

public class RequestProcessorFactory {
    private final ObjectOutputStream outputStream;
    private final BankCardTableController cardController;
    private final UserLoader userLoader;
    private final ServerMessageLogger logger;
    private Request clientRequest;
    private String clientName;

    public RequestProcessorFactory(Request clientRequest, UserLoader userLoader,
                                   ObjectOutputStream outputStream, ServerMessageLogger logger,
                                   String clientName, BankCardTableController cardController) {
        this.clientRequest = clientRequest;
        this.outputStream = outputStream;
        this.userLoader = userLoader;
        this.logger = logger;
        this.cardController = cardController;
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
                return new LoginRequestProcessor(clientRequest, userLoader, outputStream,
                        logger, clientName);
            }
            case ENCRYPTION: {
                return new EncryptionRequestProcessor(clientRequest, userLoader, outputStream,
                        logger, clientName, cardController);
            }
            case DECRYPTION: {
                return new DecryptionRequestProcessor(clientRequest, userLoader, outputStream,
                        logger, clientName, cardController);
            }
            default: {
                return null;
            }
        }
    }
}
