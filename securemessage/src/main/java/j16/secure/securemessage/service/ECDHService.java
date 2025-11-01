package j16.secure.securemessage.service;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.security.MessageDigest;
import java.security.spec.ECGenParameterSpec;

import org.springframework.stereotype.Service;

@Service
public class ECDHService {

    public KeyPair generateECKeyPair() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
        kpg.initialize(new ECGenParameterSpec("secp256r1"));
        return kpg.generateKeyPair();
    }

    public SecretKey deriveSharedSecret(PrivateKey privateKey, String peerPublicKeyBase64) throws Exception {
        byte[] peerKeyBytes = Base64.getDecoder().decode(peerPublicKeyBase64);
        KeyFactory kf = KeyFactory.getInstance("EC");
        PublicKey peerPubKey = kf.generatePublic(new X509EncodedKeySpec(peerKeyBytes));

        KeyAgreement ka = KeyAgreement.getInstance("ECDH");
        ka.init(privateKey);
        ka.doPhase(peerPubKey, true);

        byte[] sharedSecret = ka.generateSecret();
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] hash = sha256.digest(sharedSecret);

        return new SecretKeySpec(hash, 0, 32, "AES"); // AES-256
    }
}
