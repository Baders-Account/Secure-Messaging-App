package j16.secure.securemessage.service;

import java.security.MessageDigest;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Service;


@Service
public class PasswordHashingService {

    public byte[] hashPassword(String password, byte[] salt, int iterations) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, 256);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return skf.generateSecret(spec).getEncoded();
    }

    public boolean verifyPassword(String password, String storedHashBase64, String storedSaltHex, int iterations) throws Exception {
        byte[] salt = Hex.decodeHex(storedSaltHex.toCharArray());
        byte[] computedHash = hashPassword(password, salt, iterations);
        byte[] storedHash = Base64.getDecoder().decode(storedHashBase64);
        return MessageDigest.isEqual(computedHash, storedHash);
    }
}
