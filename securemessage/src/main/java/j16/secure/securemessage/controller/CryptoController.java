package j16.secure.securemessage.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import j16.secure.securemessage.model.User;
import j16.secure.securemessage.service.UserService;

@RestController
@RequestMapping("/crypto")
@CrossOrigin(origins = "http://localhost:3000")
public class CryptoController {

    @Autowired
    private UserService userService;

    // Retrieve another user’s public key for ECDH key derivation
    @GetMapping("/publicKey/{username}")
    public ResponseEntity<?> getPublicKey(@PathVariable String username) {
        try {
            User user = userService.findUser(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User not found");
            }
            return ResponseEntity.ok(user.getEcPublicKey());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error reading user data");
        }
    }

    // Update a user’s public key when regenerated in frontend
    @PutMapping("/publicKey/{username}")
    public ResponseEntity<?> updatePublicKey(@PathVariable String username, @RequestBody String newKey) {
        try {
            User user = userService.findUser(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User not found");
            }

            user.setEcPublicKey(newKey);
            userService.addUser(user); // saves updated user back to JSON

            return ResponseEntity.ok("Public key updated successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating public key");
        }
    }
}
