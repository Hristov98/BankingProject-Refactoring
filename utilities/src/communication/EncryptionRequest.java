package communication;

import java.io.Serializable;

public class EncryptionRequest extends Request implements Serializable {
    private final String cardNumber;
    private final String userSendingRequest;

    public EncryptionRequest(String cardNumber, String userSendingRequest) {
        super(RequestType.ENCRYPTION);
        this.cardNumber = cardNumber;
        this.userSendingRequest = userSendingRequest;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getUserSendingRequest() {
        return userSendingRequest;
    }
}