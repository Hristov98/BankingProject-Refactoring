package cardManipulation.encryptionAlgorithms;

public class SubstitutionCipher implements Cipher {
    private int cipherKey;

    public SubstitutionCipher(int cipherKey) {
        this.cipherKey = cipherKey % 10;
    }

    public String encryptCardNumber(String plainText) {
        char[] cardNumberDigits = plainText.toCharArray();
        char[] encryptedDigits = new char[cardNumberDigits.length];

        for (int i = 0; i < encryptedDigits.length; i++) {
            encryptedDigits[i] = encryptSymbol(cardNumberDigits[i]);
        }

        return new String(encryptedDigits);
    }

    private char encryptSymbol(char oldSymbol) {
        char newSymbol;

        if (encryptionLeadsToOverflow(oldSymbol)) {
            newSymbol = encryptOverflowingSymbol(oldSymbol);
        } else {
            newSymbol = encryptRegularSymbol(oldSymbol);
        }

        return newSymbol;
    }

    private boolean encryptionLeadsToOverflow(char symbol) {
        return (int) symbol - '0' + cipherKey > 9;
    }

    private char encryptOverflowingSymbol(char symbol) {
        return (char) ((int) symbol + cipherKey - 10);
    }

    private char encryptRegularSymbol(char symbol) {
        return (char) (symbol + cipherKey);
    }

    public String decryptCardNumber(String encryptedCardNumber) {
        char[] encryptedDigits = encryptedCardNumber.toCharArray();
        char[] decryptedDigits = new char[encryptedDigits.length];

        for (int i = 0; i < decryptedDigits.length; i++) {
            decryptedDigits[i] = decryptSymbol(encryptedDigits[i]);
        }

        return new String(decryptedDigits);
    }

    private char decryptSymbol(char oldSymbol) {
        char newSymbol;

        if (decryptionLeadsToUnderflow(oldSymbol)) {
            newSymbol = decryptUnderflowingSymbol(oldSymbol);
        } else {
            newSymbol = decryptRegularSymbol(oldSymbol);
        }

        return newSymbol;
    }

    private boolean decryptionLeadsToUnderflow(char symbol) {
        return (int) symbol - '0' - cipherKey < 0;
    }

    private char decryptUnderflowingSymbol(char symbol) {
        return (char) ((int) symbol - cipherKey + 10);
    }

    private char decryptRegularSymbol(char symbol) {
        return (char) (symbol - cipherKey);
    }

}