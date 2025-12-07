package j16.secure.securemessage.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import j16.secure.securemessage.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

@Service
public class UserService {

    private static final String USERS_FILE = "data/users.json";
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private ECDHService ecdhService;

    // Read all users from JSON file
    private List<User> getAllUsers() throws IOException {
        File file = new File(USERS_FILE);
        if (!file.exists()) return new ArrayList<>();
        return mapper.readValue(file, new TypeReference<List<User>>() {});
    }

    // Find user by username
    public User findUser(String username) throws IOException {
        return getAllUsers().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
    }

    // ✅ Validate user credentials using PBKDF2
    public boolean validateUser(String username, String password) {
        try {
            User user = findUser(username);
            if (user == null) {
                return false;
            }
            
            // Hash the provided password with the user's salt
            String hashedPassword = hashPassword(password, user.getPwdSaltHex(), user.getPwdIterations());
            
            // Compare with stored hash
            return hashedPassword.equals(user.getPasswordHash());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ Create new user with hashed password
    public User createUser(String username, String password) throws Exception {
        // Generate random salt
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        String saltHex = bytesToHex(salt);
        
        // Hash the password
        int iterations = 100000; // PBKDF2 iterations
        String passwordHash = hashPassword(password, saltHex, iterations);
        
        // Create user
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPasswordHash(passwordHash);
        newUser.setPwdSaltHex(saltHex);
        newUser.setPwdIterations(iterations);
        
        addUser(newUser);
        return newUser;
    }

    // Add user and generate key pair securely
    public void addUser(User newUser) throws Exception {
        List<User> users = getAllUsers();

        // Check if user already exists
        if (findUser(newUser.getUsername()) != null) {
            throw new Exception("User already exists");
        }

        // Assign ID
        if (newUser.getId() == null) {
            long newId = users.isEmpty() ? 1 : users.get(users.size() - 1).getId() + 1;
            newUser.setId(newId);
        }

        // Generate ECDH key pair if not already set
        if (newUser.getEcPublicKey() == null) {
            KeyPair pair = ecdhService.generateECKeyPair();
            String publicKeyBase64 = Base64.getEncoder().encodeToString(pair.getPublic().getEncoded());
            newUser.setEcPublicKey(publicKeyBase64);
        }

        // Create directory if missing
        File dir = new File("data");
        if (!dir.exists()) dir.mkdirs();

        users.add(newUser);
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(USERS_FILE), users);
    }
    
    // ✅ Hash password using PBKDF2
    private String hashPassword(String password, String saltHex, int iterations) throws Exception {
        byte[] salt = hexToBytes(saltHex);
        
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hash = factory.generateSecret(spec).getEncoded();
        
        return Base64.getEncoder().encodeToString(hash);
    }
    
    // Helper: Convert bytes to hex string
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    
    // Helper: Convert hex string to bytes
    private byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i+1), 16));
        }
        return data;
    }
}
