package cardManipulation;

public class CardValidator {
    public boolean decryptedCardNumberIsValid(String cardNumber) {
        return cardNumber.matches("^[3,4,5,6]\\d{15}$");
    }

    public boolean encryptedCardNumberIsValid(String cardNumber) {
        return cardNumber.matches("^\\d{16}$");
    }

    public boolean cardNumberIsValidByLuhn(String cardNumber) {
        char[] cardNumberDigits = cardNumber.toCharArray();

        return sumOfDigitsIsDivisibleByTen(cardNumberDigits);
    }

    private boolean sumOfDigitsIsDivisibleByTen(char[] digits) {
        return (getSumOfDigitsWithLuhnAlgorithm(digits) % 10) == 0;
    }

    private int getSumOfDigitsWithLuhnAlgorithm(char[] digits) {
        int cardLength = digits.length;
        int digitSum = 0;

        for (int i = cardLength - 1; i >= 0; i--) {
            int currentDigit = transformCharacterToInteger(digits[i]);

            if (digitIsAtEvenPosition(i, cardLength)) {
                currentDigit = doubleDigit(currentDigit);
                currentDigit = sumDigitsOfDoubledNumber(currentDigit);
            }

            digitSum += currentDigit;
        }

        return digitSum;
    }

    private int transformCharacterToInteger(char digit){
        return (int) digit - '0';
    }

    private boolean digitIsAtEvenPosition(int digit, int cardLength) {
        return digit % 2 == cardLength % 2;
    }

    private int doubleDigit(int digit) {
        return digit * 2;
    }

    private int sumDigitsOfDoubledNumber(int doubledDigit) {
        return doubledDigit / 10 + doubledDigit % 10;
    }
}
