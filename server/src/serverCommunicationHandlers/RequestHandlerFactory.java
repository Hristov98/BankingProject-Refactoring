package serverCommunicationHandlers;

import communication.RequestType;

public class RequestHandlerFactory {
    public RequestHandler createRequestHandler(RequestType type) {
        switch (type) {
            case LOGIN: {
                return new LoginRequestHandler();
            }
            case ENCRYPTION: {
                return new EncryptionRequestHandler();
            }
            case DECRYPTION: {
                return new DecryptionRequestHandler();
            }
            default: {
                return null;
            }
        }
    }
}
