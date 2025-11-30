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


@PostMapping("/encrypt-with-password")
public Map<String, String> encryptWithPassword(@RequestBody Map<String, String> body) {
    try {
        String plaintext = body.get("plaintext");
        String password = body.get("password");
        
        SecretKey key = ecdhService.deriveKeyFromPasswordWeak(password);
        return ecdhService.encryptAES(key, plaintext);
    } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("Password encryption failed: " + e.getMessage());
    }
}

@PostMapping("/brute-force-attack")
public Map<String, Object> bruteForceAttack(@RequestBody Map<String, String> body) {
    try {
        String ciphertextB64 = body.get("ciphertext");
        String ivB64 = body.get("iv");
        String knownPlaintext = body.get("knownPlaintext");
        boolean useStrong = Boolean.parseBoolean(body.getOrDefault("useStrong", "false"));
        
        long startTime = System.currentTimeMillis();
        String crackedPassword = null;
        String successfulMethod = null;
        int totalAttempts = 0;
        int maxAttemptsPerMethod = useStrong ? 5 : 5000;
        
        // Try numeric first (fastest)
        BruteForcer numericForcer = BruteForcer.createNumericBruteForcer();
        int attempts = 0;
        while (attempts < maxAttemptsPerMethod && crackedPassword == null) {
            String pwd = numericForcer.computeNextCombination();
            attempts++;
            totalAttempts++;
            
            if (numericForcer.getCurrentLength() > 4) break;
            
            if (tryPassword(pwd, ciphertextB64, ivB64, knownPlaintext, useStrong)) {
                crackedPassword = pwd;
                successfulMethod = "Numeric";
                break;
            }
        }
        
        // Try alphabetic if numeric failed
        if (crackedPassword == null) {
            BruteForcer alphaForcer = BruteForcer.createAlphaBruteForcer();
            attempts = 0;
            while (attempts < maxAttemptsPerMethod && crackedPassword == null) {
                String pwd = alphaForcer.computeNextCombination();
                attempts++;
                totalAttempts++;
                
                if (alphaForcer.getCurrentLength() > 3) break; // Limit for demo
                
                if (tryPassword(pwd, ciphertextB64, ivB64, knownPlaintext, useStrong)) {
                    crackedPassword = pwd;
                    successfulMethod = "Alphabetic";
                    break;
                }
            }
        }
        
        // Try alphanumeric if both failed
        if (crackedPassword == null) {
            BruteForcer alphanumericForcer = BruteForcer.createAlphaNumericBruteForcer();
            attempts = 0;
            while (attempts < maxAttemptsPerMethod && crackedPassword == null) {
                String pwd = alphanumericForcer.computeNextCombination();
                attempts++;
                totalAttempts++;
                
                if (alphanumericForcer.getCurrentLength() > 3) break; // Limit for demo
                
                if (tryPassword(pwd, ciphertextB64, ivB64, knownPlaintext, useStrong)) {
                    crackedPassword = pwd;
                    successfulMethod = "Alphanumeric";
                    break;
                }
            }
        }
        
        long timeTaken = System.currentTimeMillis() - startTime;
        
        String message;
        if (crackedPassword != null) {
            message = String.format("✅ Password '%s' cracked using %s brute force in %dms after %d total attempts!", 
                crackedPassword, successfulMethod, timeTaken, totalAttempts);
        } else if (useStrong) {
            message = String.format("🛡️ Strong PBKDF2 protection withstood %d attempts across all methods in %dms. Full attack would take days!", 
                totalAttempts, timeTaken);
        } else {
            message = String.format("❌ Password not cracked after %d attempts across all methods in %dms", 
                totalAttempts, timeTaken);
        }
        
        return Map.of(
            "success", crackedPassword != null,
            "crackedPassword", crackedPassword != null ? crackedPassword : "Not found",
            "attempts", totalAttempts,
            "timeTakenMs", timeTaken,
            "attackType", successfulMethod != null ? successfulMethod : "All methods tried",
            "message", message
        );
    } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("Brute force failed: " + e.getMessage());
    }
}

private boolean tryPassword(String password, String ciphertextB64, String ivB64, 
                           String knownPlaintext, boolean useStrong) {
    try {
        SecretKey key;
        if (useStrong) {
            byte[] salt = new byte[16];
            key = ecdhService.deriveKeyFromPasswordStrong(password, salt, 100000);
        } else {
            key = ecdhService.deriveKeyFromPasswordWeak(password);
        }
        
        String decrypted = ecdhService.decryptAES(key, ciphertextB64, ivB64);
        return decrypted.contains(knownPlaintext);
    } catch (Exception e) {
        return false;
    }
}




}


