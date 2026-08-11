package bg.sofia.uni.fmi.mjt.space.algorithm;

import bg.sofia.uni.fmi.mjt.space.exception.CipherException;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import java.io.InputStream;
import java.io.OutputStream;

public class Rijndael implements SymmetricBlockCipher {

    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final int MAX_READ_BYTES = 1024;
    private static final int NO_MORE_DATA = -1;

    private final SecretKey secretKey;

    public Rijndael(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public void encrypt(InputStream inputStream, OutputStream outputStream) throws CipherException {
        try {
            // setting up the cipher to encrypt based on the secretKey
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            // data is going to be read from inputStream with written to outputStream with encryption
            byte[] bytes = new byte[MAX_READ_BYTES];
            try (CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher)) {
                int bytesRead;
                while ((bytesRead = inputStream.read(bytes)) != NO_MORE_DATA) {
                    cipherOutputStream.write(bytes, 0, bytesRead);
                }
            }
        } catch (Exception e) {
            throw new CipherException("Problem with reading files", e);
        }
    }

    @Override
    public void decrypt(InputStream inputStream, OutputStream outputStream) throws CipherException {
        try {
            // setting up the cipher to encrypt based on the secretKey
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            // data is going to be read from inputStream with written to outputStream with encryption
            byte[] bytes = new byte[MAX_READ_BYTES];
            try (CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher)) {
                int bytesRead;
                while ((bytesRead = inputStream.read(bytes)) != NO_MORE_DATA) {
                    cipherOutputStream.write(bytes, 0, bytesRead);
                }
            }
        } catch (Exception e) {
            throw new CipherException("Problem with reading files", e);
        }
    }
}
