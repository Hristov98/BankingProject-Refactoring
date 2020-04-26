package wrappers;

import java.io.Serializable;

//a wrapper class that packages information sent between client and server during decryption process
public class DecryptionRequest implements Serializable
{
    private String cardNumber; //the card number sent between client and server

    public DecryptionRequest(String card)
    {
        cardNumber=card;
    }

    public String getCardNumber() {
        return cardNumber;
    }


}
