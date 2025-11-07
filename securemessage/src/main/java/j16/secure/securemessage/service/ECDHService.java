package j16.secure.securemessage.service;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import javax.crypto.*;
import javax.crypto.spec.*;

import org.springframework.stereotype.Service;

@Service
public class ECDHService {

    
    // Key generation
    
    public KeyPair generateECKeyPair() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
        kpg.initialize(new ECGenParameterSpec("secp256r1"));
        return kpg.generateKeyPair();
    }

    
    // Derive shared AES key 
   
    public SecretKey deriveSharedSecret(PrivateKey myPriv, PublicKey theirPub) throws Exception {
        KeyAgreement ka = KeyAgreement.getInstance("ECDH");
        ka.init(myPriv);
        ka.doPhase(theirPub, true);
        byte[] shared = ka.generateSecret();

        // HKDF-Extract-and-Expand 
        byte[] prk = hmacSha256(new byte[32], shared); // salt 
        byte[] info = "AES-GCM key".getBytes(StandardCharsets.UTF_8);
        byte[] okm = hkdfExpand(prk, info, 32); // 32 bytes = 256-bit key

        return new SecretKeySpec(okm, "AES");
    }

    
    // Derive shared AES key 
   
    public SecretKey deriveSharedSecret(PrivateKey myPriv, String theirPubBase64) throws Exception {
        byte[] peerKeyBytes = Base64.getDecoder().decode(theirPubBase64);
        KeyFactory kf = KeyFactory.getInstance("EC");
        PublicKey theirPub = kf.generatePublic(new X509EncodedKeySpec(peerKeyBytes));
        return deriveSharedSecret(myPriv, theirPub);
    }

    // include CBC also



    // AES-GCM Encrypt

       public Map<String, String> encryptAES(SecretKey aesKey, String plaintext) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, gcmSpec);

        byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

        return Map.of(
            "ciphertextB64", Base64.getEncoder().encodeToString(ciphertext),
            "ivB64", Base64.getEncoder().encodeToString(iv)
        );
    }

    
    //  AES-GCM Decrypt
    
    public String decryptAES(SecretKey aesKey, String ciphertextB64, String ivB64) throws Exception {
        byte[] ciphertext = Base64.getDecoder().decode(ciphertextB64);
        byte[] iv = Base64.getDecoder().decode(ivB64);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, aesKey, gcmSpec);

        byte[] plaintextBytes = cipher.doFinal(ciphertext);
        return new String(plaintextBytes, StandardCharsets.UTF_8);
    }

    
    private byte[] hmacSha256(byte[] key, byte[] data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key, "HmacSHA256"));
        return mac.doFinal(data);
    }

    private byte[] hkdfExpand(byte[] prk, byte[] info, int length) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(prk, "HmacSHA256"));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] prev = new byte[0];
        byte counter = 1;
        while (out.size() < length) {
            mac.reset();
            mac.init(new SecretKeySpec(prk, "HmacSHA256"));
            mac.update(prev);
            mac.update(info);
            mac.update(counter);
            prev = mac.doFinal();
            out.write(prev);
            counter++;
        }
        byte[] okm = out.toByteArray();
        return Arrays.copyOf(okm, length);
    }
}
