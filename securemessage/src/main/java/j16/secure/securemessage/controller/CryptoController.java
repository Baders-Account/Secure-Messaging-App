package j16.secure.securemessage.controller;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import j16.secure.securemessage.service.EncryptionService;

@RestController
@RequestMapping("/api") // base path for all endpoints in this controller
@CrossOrigin(origins = "http://localhost:3000")   // access the react server
public class CryptoController {

    private final EncryptionService encryptionService;

    public CryptoController(EncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }

    @PostMapping("/encrypt")
    public Map<String, String> encryption(@RequestBody Map<String, String> request) {
       try {
           
           return encryptionService.encryptMessage(request.get("message")); // get the message from the user
       } catch (Exception e) {
              e.printStackTrace();
              return new HashMap<>(); // return an empty map in case of error
       } 
      
    }
    @PostMapping("/decrypt")
    public String decryption(@RequestBody Map<String, String> request) {
        String cipherText = request.get("ciphertext");
        String key = request.get("key");
        String iv = request.get("iv");
       try {
        
        return encryptionService.decrypt(cipherText, key, iv);
           
          
          
       } catch (Exception e) {
                System.out.println("Ciphertext: " + cipherText);
                System.out.println("Key: " + key);
                System.out.println("IV: " + iv);
              e.printStackTrace();
              return "Im in here"; // return an empty map in case of error
       } 
      
    }

    
}
