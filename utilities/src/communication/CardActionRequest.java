package communication;

public class CardActionRequest extends Request {
    private final String cardNumber;
    private final String userSendingRequest;

    public CardActionRequest(RequestType type,String cardNumber, String userSendingRequest) {
        super(type);
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
