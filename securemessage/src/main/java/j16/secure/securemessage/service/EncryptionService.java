package j16.secure.securemessage.service;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;

import java.util.HashMap;
import java.util.Map;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import org.springframework.stereotype.Service; 
@Service
public class EncryptionService {
    
       static {
        // Register BouncyCastle as a security provider
        Security.addProvider(new BouncyCastleProvider());
    }
    
    public  Map<String, String> encryptMessage(String message) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException{
    byte[] iv = new byte[16];
    SecureRandom random = new SecureRandom();
    random.nextBytes(iv);
    IvParameterSpec ivParams = new IvParameterSpec(iv);
    KeyGenerator keyGen = KeyGenerator.getInstance("AES");
    keyGen.init(256);
    SecretKey secretKey = keyGen.generateKey();
    System.out.println("Secret Key: " + secretKey.getEncoded());
    System.out.println("iv: " + iv);
    

    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParams);

    byte[] encryptedMessage = cipher.doFinal(message.getBytes("UTF-8"));
      String ciphertextBase64 = Base64.encodeBase64String(encryptedMessage);
        String keyBase64 = Base64.encodeBase64String(secretKey.getEncoded());
        String ivBase64 = Base64.encodeBase64String(iv);
    
        Map<String, String> result = new HashMap<>();
        result.put("ciphertext", ciphertextBase64);
        result.put("key", keyBase64);
        result.put("iv", ivBase64);
        
        


    return result ;

    }

    public String decrypt(String ciphertextBase64, String keyBase64, String ivBase64) throws InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        
        //Decode
        byte [] ciphertext = Base64.decodeBase64(ciphertextBase64);
        byte [] key = Base64.decodeBase64(keyBase64);
        byte [] iv = Base64.decodeBase64(ivBase64);

        // recreate the key and iv 
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        IvParameterSpec ivParams = new IvParameterSpec(iv);

        Cipher cipher= Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParams);

        byte[] decryptedMessage = cipher.doFinal(ciphertext);
        return new String(decryptedMessage);

     }
    


    



}
