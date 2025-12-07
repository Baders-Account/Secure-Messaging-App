package j16.secure.securemessage.controller;

import org.springframework.web.bind.annotation.*;

import attacks.BruteForcer;

import org.springframework.beans.factory.annotation.Autowired;

import j16.secure.securemessage.service.ECDHService;

import j16.secure.securemessage.model.Message;
import j16.secure.securemessage.service.MessageService;
import java.util.Arrays;
import java.security.KeyPair;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

@RestController
@RequestMapping("/api/crypto")
@CrossOrigin(origins = {"http://localhost:3000"}) // allow frontend access
public class CryptoController {

    @Autowired
    private ECDHService ecdhService;
    @Autowired
    private MessageService messageService;

    private KeyPair serverKeyPair;

    // Generate server keypair once 
    @GetMapping("/public")
    public String getServerPublicKey() throws Exception {
        if (serverKeyPair == null) {
            serverKeyPair = ecdhService.generateECKeyPair();
        }
        return Base64.getEncoder().encodeToString(serverKeyPair.getPublic().getEncoded());
    }

    // Endpoint for deriving and encrypting 
    @PostMapping("/derive-and-encrypt")
    public Map<String, String> deriveAndEncrypt(@RequestBody Map<String, String> body) throws Exception {
        String clientPublicKeyBase64 = body.get("clientPublicKeyBase64");
        String plaintext = body.get("plaintext");

        // Derive AES key using ECDH with server's private + client's public
        var aesKey = ecdhService.deriveSharedSecret(serverKeyPair.getPrivate(), clientPublicKeyBase64);

        // Encrypt plaintext
        var result = ecdhService.encryptAES(aesKey, plaintext);

        return Map.of(
            "ciphertextB64", result.get("ciphertextB64"),
            "ivB64", result.get("ivB64")
        );
    }

    // Endpoint for deriving and decrypting

    @PostMapping("/derive-and-decrypt")
    public Map<String, String> deriveAndDecrypt(@RequestBody Map<String, String> body) {
    try {
        // Ensure server keypair is initialized
        if (serverKeyPair == null) {
            serverKeyPair = ecdhService.generateECKeyPair();
        }

        // Get messageId from request
        String messageId = body.get("messageId");
        if (messageId == null || messageId.isBlank()) {
            throw new IllegalArgumentException("Missing messageId");
        }

        // Load message from storage
        Message msg = messageService.getMessageById(messageId);
        if (msg == null) {
            throw new IllegalArgumentException("Message not found for id: " + messageId);
        }

        // Get crypto fields from stored message
        String senderPubKeyB64 = msg.getSenderPublicKeyBase64();
        String ciphertextB64 = msg.getCiphertext();
        String ivB64 = msg.getIv();

        if (senderPubKeyB64 == null || ciphertextB64 == null || ivB64 == null) {
            throw new IllegalArgumentException("Message is missing crypto fields");
        }

        System.out.println("Using stored senderPublicKeyBase64: " + senderPubKeyB64);

        // Derive key and decrypt
        var aesKey = ecdhService.deriveSharedSecret(serverKeyPair.getPrivate(), senderPubKeyB64);
        String plaintext = ecdhService.decryptAES(aesKey, ciphertextB64, ivB64);

        return Map.of("plaintext", plaintext);
    } catch (IllegalArgumentException e) {
        e.printStackTrace();
        throw new RuntimeException("Invalid input: " + e.getMessage());
    } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("Decryption failed: " + e.getMessage());
    }


}









}


