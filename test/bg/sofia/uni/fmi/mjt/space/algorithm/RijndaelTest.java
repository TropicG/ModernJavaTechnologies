package bg.sofia.uni.fmi.mjt.space.algorithm;

import bg.sofia.uni.fmi.mjt.space.exception.CipherException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RijndaelTest {
    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final int KEY_SIZE_IN_BITS = 128;

    private static Rijndael rijndael;
    private static Cipher cipher;

    @BeforeAll
    static void setUpSecretKey() {
        try {
            // generating the secret key
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ENCRYPTION_ALGORITHM);
            keyGenerator.init(KEY_SIZE_IN_BITS);
            SecretKey secretKey = keyGenerator.generateKey();

            // cipher will be used only for encryption
            cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            rijndael = new Rijndael(secretKey);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException exception) {
            System.out.println("No such algorithm passed to RiJndael");
        } catch (InvalidKeyException e) {
            System.out.println("Problems with the key for encryption/decription");
        }
    }

    // public void encrypt(InputStream inputStream, OutputStream outputStream) throws CipherException
    @Test
    void testSuccessfulEncryption() throws IOException {
        String stringToBeEncryptedAES = "This string is going to be encrypted with AES";
        byte[] bytesEncrypted;
        // writting bytes to internal byte array
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             CipherOutputStream cis = new CipherOutputStream(baos, cipher)) {
            cis.write(stringToBeEncryptedAES.getBytes(StandardCharsets.UTF_8));
            // saving the encrypted bytes for future comparing for success
            bytesEncrypted = baos.toByteArray();
        }

        // encrypting data with rijndael function
        byte[] bytesEncryptedFromRijndael;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(stringToBeEncryptedAES.getBytes());
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            rijndael.encrypt(bais, baos);
            bytesEncryptedFromRijndael = baos.toByteArray();
        }

        // comparing the internal successful encryption with the rijndael encryption
        for (int i = 0; i < bytesEncrypted.length; i++) {
            assertEquals(bytesEncrypted[i], bytesEncryptedFromRijndael[i],
                    "There is a mismatch between the bytes from the Rijndael algorithm in encryption");
        }
    }

    @Test
    void testEncryptionWithNullServiceKeyThrowsCipherException() {
        // This test focuses on the throwing of CipherException when trying to encrypt with null secretKey
        Rijndael tempRijndael = new Rijndael(null);
        String stringToBeEncryptedAES = "This string is going to be encrypted with AES";

        assertThrows(CipherException.class,
                () -> tempRijndael.encrypt(
                        new ByteArrayInputStream(stringToBeEncryptedAES.getBytes(StandardCharsets.UTF_8)),
                        new ByteArrayOutputStream()),
                "When secret key is passed as null during encryption CipherException is thrown");
    }

    // public void decrypt(InputStream inputStream, OutputStream outputStream) throws CipherException
    @Test
    void testSuccessfulDecryption() throws IOException {
        String stringToBeEncryptedAES = "This string is going to be encrypted with AES";
        byte[] bytesEncrypted;
        byte[] bytesDecrypted;

        // encrypting the string
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             CipherOutputStream cis = new CipherOutputStream(baos, cipher)) {
            cis.write(stringToBeEncryptedAES.getBytes(StandardCharsets.UTF_8));
            bytesEncrypted = baos.toByteArray();
        }

        // decrypting the string
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytesEncrypted);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            rijndael.decrypt(bais, baos);
            bytesDecrypted = baos.toByteArray();
        }

        // comparing are the bytes of the decrypted string and the original string equal
        for (int i = 0; i < bytesDecrypted.length; i++) {
            assertEquals(bytesDecrypted[i], stringToBeEncryptedAES.getBytes()[i],
                    "There is a mismatch between the bytes from the Rijndael algorithm in decryption");
        }
    }

    @Test
    void testNullServiceKeyThrowsCipherExceptionWhenDecrypting() throws IOException {
        // This test focuses on the throwing of CipherException when trying to decrypt with null secretKey
        Rijndael tempRijndael = new Rijndael(null);
        String stringToBeEncryptedAES = "This string is going to be encrypted with AES";

        assertThrows(CipherException.class,
                () -> tempRijndael.decrypt(
                        new ByteArrayInputStream(stringToBeEncryptedAES.getBytes(StandardCharsets.UTF_8)),
                        new ByteArrayOutputStream()),
                "When secret key is passed as null during decryption CipherException is thrown");
    }
}
