package cardManipulation;

public class SubstitutionCipher {
    private int cipherKey;

    public SubstitutionCipher(int offset) {
        setCipherKey(offset);
    }

    public void setCipherKey(int cipherKey) {
        this.cipherKey = cipherKey;
    }

    public void increment() {
        cipherKey++;
    }

    public String encrypt(String plainText) {
        char[] plainTextArray = plainText.toCharArray();
        char[] encryptedText = new char[plainTextArray.length];

        for (int i = 0; i < encryptedText.length; i++) {
            if ((int) plainTextArray[i] - '0' + cipherKey > 9) {
                encryptedText[i] = (char) ((int) plainTextArray[i] + cipherKey - 10);
            } else {
                encryptedText[i] = (char) (plainTextArray[i] + cipherKey);
            }
        }

        return new String(encryptedText);
    }

    public String decrypt(String encryptedText) {
        char[] encryptedTextArray = encryptedText.toCharArray();
        char[] decryptedText = new char[encryptedTextArray.length];

        for (int i = 0; i < decryptedText.length; i++) {
            if ((int) encryptedTextArray[i] - '0' - cipherKey < 0) {
                decryptedText[i] = (char) ((int) encryptedTextArray[i] - cipherKey + 10);
            } else {
                decryptedText[i] = (char) (encryptedTextArray[i] - cipherKey);
            }
        }

        return new String(decryptedText);
    }
}