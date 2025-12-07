package j16.secure.securemessage.controller;

import j16.secure.securemessage.model.User;
import j16.secure.securemessage.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private UserService userService;

    // Login endpoint
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        
        try {
            // Validate credentials using your UserService
            boolean isValid = userService.validateUser(username, password);
            
            if (isValid) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Login successful");
                response.put("username", username);
                
                System.out.println("✅ [LOGIN] User logged in: " + username);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("error", "Invalid username or password");
                
                System.out.println("⚠️ [LOGIN] Failed login attempt for: " + username);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Login failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Register endpoint (if you need it)
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> userData) {
        String username = userData.get("username");
        String password = userData.get("password");
        
        try {
            // Check if user already exists
            if (userService.findUser(username) != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Username already exists"));
            }
            
            // Create new user
            userService.createUser(username, password);
            
            System.out.println("✅ [REGISTER] New user registered: " + username);
            return ResponseEntity.ok(Map.of("success", true, "message", "User registered successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Registration failed: " + e.getMessage()));
        }
    }

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
