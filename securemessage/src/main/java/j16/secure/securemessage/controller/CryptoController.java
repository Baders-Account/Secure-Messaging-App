package j16.secure.securemessage.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import j16.secure.securemessage.service.ECDHService;

import java.security.KeyPair;
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/api/crypto")
@CrossOrigin(origins = {"http://localhost:3000"}) // allow frontend access
public class CryptoController {

    @Autowired
    private ECDHService ecdhService;

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
    public Map<String, String> deriveAndDecrypt(@RequestBody Map<String, String> body) throws Exception {
        String clientPublicKeyBase64 = body.get("clientPublicKeyBase64");
        String ciphertextB64 = body.get("ciphertextB64");
        String ivB64 = body.get("ivB64");

        var aesKey = ecdhService.deriveSharedSecret(serverKeyPair.getPrivate(), clientPublicKeyBase64);
        String plaintext = ecdhService.decryptAES(aesKey, ciphertextB64, ivB64);

        return Map.of("plaintext", plaintext);
    }
}
