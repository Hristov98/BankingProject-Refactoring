package cardManipulation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CardValidatorTest {
    private CardValidator validator = new CardValidator();

    @Test
    void testDecryptedCardNumberIsValid() {
        assertTrue(validator.decryptedCardNumberIsValid("3958237601947280"));
        assertTrue(validator.decryptedCardNumberIsValid("4976737695168251"));
        assertTrue(validator.decryptedCardNumberIsValid("6695620593399365"));
    }

    @Test
    void testDecryptedCardNumberIsInvalid() {
        assertFalse(validator.decryptedCardNumberIsValid("1140175048844810"));
        assertFalse(validator.decryptedCardNumberIsValid("9018415677556444"));
        assertFalse(validator.decryptedCardNumberIsValid("Random text"));
    }

    @Test
    void testEncryptedCardNumberIsValid() {
        assertTrue(validator.encryptedCardNumberIsValid("8539959906512942"));
        assertTrue(validator.encryptedCardNumberIsValid("8101414409476795"));
        assertTrue(validator.encryptedCardNumberIsValid("8403782156492735"));
    }

    @Test
    void testEncryptedCardNumberIsInvalid() {
        assertFalse(validator.encryptedCardNumberIsValid("853995990651294"));
        assertFalse(validator.encryptedCardNumberIsValid("85399599065129421"));
        assertFalse(validator.encryptedCardNumberIsValid("Random text"));
    }

    @Test
    void testLuhnAlgorithmIsValid() {
        assertTrue(validator.cardNumberIsValidByLuhn("3084404451067497"));
        assertTrue(validator.cardNumberIsValidByLuhn("3958237601947280"));
        assertTrue(validator.cardNumberIsValidByLuhn("4563960122001999"));
    }

    @Test
    void testLuhnAlgorithmIsInvalid() {
        assertFalse(validator.cardNumberIsValidByLuhn("1140171048844810"));
        assertFalse(validator.cardNumberIsValidByLuhn("9018415677586444"));
        assertFalse(validator.cardNumberIsValidByLuhn("Random text"));
    }
}