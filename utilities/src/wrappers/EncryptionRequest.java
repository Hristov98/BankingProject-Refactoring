package wrappers;

import java.io.Serializable;

//a wrapper class that packages information sent between client and server during encryption process
public class EncryptionRequest implements Serializable
{
    private String cardNumber; //the card number sent between client and server

    public EncryptionRequest(String card)
    {
        cardNumber=card;
    }

    public String getCardNumber() {
        return cardNumber;
    }
}
