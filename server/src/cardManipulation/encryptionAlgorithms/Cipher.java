package cardManipulation.encryptionAlgorithms;

public interface Cipher {

    String encryptCardNumber(String number);

    String decryptCardNumber(String number);
}
