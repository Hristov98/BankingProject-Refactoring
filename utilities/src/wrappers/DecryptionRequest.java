package wrappers;

import java.io.Serializable;

public class DecryptionRequest implements Serializable {
    private String cardNumber;

    public DecryptionRequest(String card) {
        cardNumber = card;
    }

    public String getCardNumber() {
        return cardNumber;
    }
}
