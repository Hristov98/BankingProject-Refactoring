package other;

public class SubstitutionCipher {
    private int shiftBy;

    public SubstitutionCipher(int offset) {
        setShiftBy(offset);
    }

    public void setShiftBy(int shiftBy) {
        this.shiftBy = shiftBy;
    }

    public void increment() {
        shiftBy++;
    }

    public String encrypt(String plainText) {
        char[] charArr = plainText.toCharArray();
        char[] encrypted = new char[charArr.length];

        for (int i = 0; i < encrypted.length; i++) {
            if ((int) charArr[i] - '0' + shiftBy > 9) {
                encrypted[i] = (char) ((int) charArr[i] + shiftBy - 10);
            } else {
                encrypted[i] = (char) (charArr[i] + shiftBy);
            }
        }

        return new String(encrypted);
    }

    public String decrypt(String encryptedText) {
        char[] charArr = encryptedText.toCharArray();
        char[] decrypted = new char[charArr.length];

        for (int i = 0; i < decrypted.length; i++) {
            if ((int) charArr[i] - '0' - shiftBy < 0) {
                decrypted[i] = (char) ((int) charArr[i] - shiftBy + 10);
            } else {
                decrypted[i] = (char) (charArr[i] - shiftBy);
            }
        }

        return new String(decrypted);
    }
}