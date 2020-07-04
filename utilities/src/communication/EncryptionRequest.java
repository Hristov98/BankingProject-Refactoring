package communication;

import java.io.Serializable;

public class EncryptionRequest extends Request implements Serializable {
    private String cardNumber;

    public EncryptionRequest(String cardNumber) {
        super(RequestType.ENCRYPTION);
        this.cardNumber = cardNumber;
    }

    public String getCardNumber() {
        return cardNumber;
    }
}
