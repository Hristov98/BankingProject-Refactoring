package wrappers;

import java.io.Serializable;

public class EncryptionRequest implements Serializable {
    private String cardNumber;

    public EncryptionRequest(String card) {
        cardNumber = card;
    }

    public String getCardNumber() {
        return cardNumber;
    }
}
