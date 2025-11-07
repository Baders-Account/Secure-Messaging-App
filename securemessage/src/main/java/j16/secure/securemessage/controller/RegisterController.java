package j16.secure.securemessage.controller;

import java.security.KeyPair;
import java.security.SecureRandom;
import java.util.Base64;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import j16.secure.securemessage.model.RegisterRequest;
import j16.secure.securemessage.model.User;
import j16.secure.securemessage.service.ECDHService;
import j16.secure.securemessage.service.PasswordHashingService;
import j16.secure.securemessage.service.UserService;

@RestController
@RequestMapping("/register")
@CrossOrigin(origins = "http://localhost:3000")
public class RegisterController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordHashingService hashingService;

    @Autowired
    private ECDHService ecdhService;

    @PostMapping
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) throws Exception {
        // Check for duplicates
        if (userService.findUser(req.getUsername()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Username already exists");
        }

        // Hash password securely
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        int iterations = 260_000;

        byte[] hash = hashingService.hashPassword(req.getPassword(), salt, iterations);

        // Generate ECDH key pair (server-side)
        KeyPair pair = ecdhService.generateECKeyPair();
        String publicKeyBase64 = Base64.getEncoder().encodeToString(pair.getPublic().getEncoded());
        String privateKeyBase64 = Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded());

        // Create user with both keys
        User user = new User(
                req.getUsername(),
                Hex.encodeHexString(salt),
                Base64.getEncoder().encodeToString(hash),
                publicKeyBase64,
                privateKeyBase64,
                iterations
        );

        // Save user
        userService.addUser(user);

        // Respond
        return ResponseEntity.ok("User registered successfully with secure ECDH keys");
    }
}
