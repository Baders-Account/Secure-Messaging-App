package j16.secure.securemessage.controller;

import j16.secure.securemessage.model.User;
import j16.secure.securemessage.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private UserService userService;

    // Retrieve a user's public key
    @GetMapping("/{username}/publicKey")
    public ResponseEntity<String> getPublicKey(@PathVariable String username) {
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
                    .body("Error retrieving public key");
        }
    }
}
