package communication;

import java.io.Serializable;

public class DecryptionRequest extends Request implements Serializable {
    private final String cardNumber;

    public DecryptionRequest(String cardNumber) {
        super(RequestType.DECRYPTION);
        this.cardNumber = cardNumber;
    }

    public String getCardNumber() {
        return cardNumber;
    }
}