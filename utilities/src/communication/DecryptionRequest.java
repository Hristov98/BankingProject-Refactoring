package communication;

import java.io.Serializable;

public class DecryptionRequest extends Request implements Serializable {
    private final String cardNumber;
    private final String userSendingRequest;

    public DecryptionRequest(String cardNumber, String userSendingRequest) {
        super(RequestType.DECRYPTION);
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