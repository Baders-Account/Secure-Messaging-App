package j16.secure.securemessage.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import j16.secure.securemessage.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

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

    // Add user and generate key pair securely
    public void addUser(User newUser) throws Exception {
        List<User> users = getAllUsers();

        // Assign ID
        if (newUser.getId() == null) {
            long newId = users.isEmpty() ? 1 : users.get(users.size() - 1).getId() + 1;
            newUser.setId(newId);
        }

        // Generate ECDH key pair
        KeyPair pair = ecdhService.generateECKeyPair();
        String publicKeyBase64 = Base64.getEncoder().encodeToString(pair.getPublic().getEncoded());
        String privateKeyBase64 = Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded());

        // Store keys
        newUser.setEcPublicKey(publicKeyBase64);
        

        // Create directory if missing
        File dir = new File("data");
        if (!dir.exists()) dir.mkdirs();

        users.add(newUser);
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(USERS_FILE), users);
    }
}
