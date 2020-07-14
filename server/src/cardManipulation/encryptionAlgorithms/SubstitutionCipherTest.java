package cardManipulation.encryptionAlgorithms;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SubstitutionCipherTest {

    @Test
    void testEncryptionBySubstitutionCipher() {
        Cipher cipher = new SubstitutionCipher(5);

        Assertions.assertEquals("8539959906512942", cipher.encryptCardNumber("3084404451067497"));
        Assertions.assertEquals("9018415677556444", cipher.encryptCardNumber("4563960122001999"));
        Assertions.assertEquals("1140175048844810", cipher.encryptCardNumber("6695620593399365"));
    }


    @Test
    void testDecryptionBySubstitutionCipher() {
        Cipher cipher = new SubstitutionCipher(5);

        Assertions.assertEquals("3084404451067497", cipher.decryptCardNumber("8539959906512942"));
        Assertions.assertEquals("4563960122001999", cipher.decryptCardNumber("9018415677556444"));
        Assertions.assertEquals("6695620593399365", cipher.decryptCardNumber("1140175048844810"));
    }

    @Test
    void testSubstitutionCipherKeyCorrectnessDuringEncryption() {
        Cipher cipherWithKeyOne = new SubstitutionCipher(1);
        Cipher cipherWithKeySeven = new SubstitutionCipher(7);
        Cipher cipherWithKeyEleven = new SubstitutionCipher(11);

        Assertions.assertEquals("4195515562178508", cipherWithKeyOne.encryptCardNumber("3084404451067497"));
        Assertions.assertEquals("1230637899778666", cipherWithKeySeven.encryptCardNumber("4563960122001999"));
        Assertions.assertEquals("7706731604400476", cipherWithKeyEleven.encryptCardNumber("6695620593399365"));
    }

    @Test
    void testSubstitutionCipherKeyCorrectnessDuringDecryption() {
        Cipher cipherWithKeyOne = new SubstitutionCipher(1);
        Cipher cipherWithKeySeven = new SubstitutionCipher(7);
        Cipher cipherWithKeyEleven = new SubstitutionCipher(11);

        Assertions.assertEquals("2973393340956386", cipherWithKeyOne.decryptCardNumber("3084404451067497"));
        Assertions.assertEquals("7896293455334222", cipherWithKeySeven.decryptCardNumber("4563960122001999"));
        Assertions.assertEquals("5584519482288254", cipherWithKeyEleven.decryptCardNumber("6695620593399365"));
    }
}