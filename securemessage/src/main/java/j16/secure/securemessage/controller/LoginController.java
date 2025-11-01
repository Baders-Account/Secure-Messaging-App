package j16.secure.securemessage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import j16.secure.securemessage.model.LoginRequest;
import j16.secure.securemessage.model.User;
import j16.secure.securemessage.service.PasswordHashingService;
import j16.secure.securemessage.service.UserService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/login")
@CrossOrigin(origins = "http://localhost:3000")
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordHashingService hashingService;

    @PostMapping
    public ResponseEntity<?> login(@RequestBody LoginRequest req, HttpSession session) throws Exception {
        User user = userService.findUser(req.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        boolean valid = hashingService.verifyPassword(
                req.getPassword(),
                user.getPasswordHash(),
                user.getPwdSaltHex(),
                user.getPwdIterations());

        if (!valid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
        }

        // Store session info
        session.setAttribute("username", user.getUsername());
        return ResponseEntity.ok("Login successful");
    }
}
