package j16.secure.securemessage.controller;

import java.security.SecureRandom;
import java.util.Base64;

import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import j16.secure.securemessage.model.RegisterRequest;
import j16.secure.securemessage.model.User;
import j16.secure.securemessage.service.UserService;
import j16.secure.securemessage.service.PasswordHashingService;

@RestController
@RequestMapping("/register")
@CrossOrigin(origins = "http://localhost:3000")
public class RegisterController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordHashingService hashingService;

    @PostMapping
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) throws Exception {
        if (userService.findUser(req.getUsername()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Username already exists");
        }

        // Generate salt & hash password
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        int iterations = 260_000;

        byte[] hash = hashingService.hashPassword(req.getPassword(), salt, iterations);

        User user = new User(
                req.getUsername(),
                Hex.encodeHexString(salt), // pwdSaltHex
                Base64.getEncoder().encodeToString(hash), // passwordHash
                req.getEcPublicKey(), // public key
                iterations // pwdIterations
        );

        userService.addUser(user);

        return ResponseEntity.ok("User registered successfully");
    }
}
