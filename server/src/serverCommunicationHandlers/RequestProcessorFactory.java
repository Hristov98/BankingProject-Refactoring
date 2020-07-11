package serverCommunicationHandlers;

import cardManipulation.BankCardTableController;
import communication.Request;
import communication.RequestType;
import serverApp.ServerMessageLogger;
import userStorage.UserController;

import java.io.ObjectOutputStream;

public class RequestProcessorFactory {
    private final ObjectOutputStream outputStream;
    private final BankCardTableController cardController;
    private final UserController userController;
    private final ServerMessageLogger logger;
    private Request clientRequest;
    private String clientName;

    public RequestProcessorFactory(Request clientRequest, UserController userController,
                                   ObjectOutputStream outputStream, ServerMessageLogger logger,
                                   String clientName, BankCardTableController cardController) {
        this.clientRequest = clientRequest;
        this.outputStream = outputStream;
        this.userController = userController;
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
                return new LoginRequestProcessor(clientRequest, userController, outputStream,
                        logger, clientName);
            }
            case ENCRYPTION: {
                return new EncryptionRequestProcessor(clientRequest, userController, outputStream,
                        logger, clientName, cardController);
            }
            case DECRYPTION: {
                return new DecryptionRequestProcessor(clientRequest, userController, outputStream,
                        logger, clientName, cardController);
            }
            default: {
                return null;
            }
        }
    }
}
