package j16.secure.securemessage.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Service;

import j16.secure.securemessage.model.User;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private static final String USERS_FILE = "data/users.json";
    private final ObjectMapper mapper = new ObjectMapper();

    // Read all users from JSON file
    private List<User> getAllUsers() throws IOException {
        File file = new File(USERS_FILE);
        if (!file.exists())
            return new ArrayList<>();
        return mapper.readValue(file, new TypeReference<List<User>>() {
        });
    }

    // Find user by username
    public User findUser(String username) throws IOException {
        return getAllUsers().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
    }

    public void addUser(User newUser) throws IOException {
        List<User> users = getAllUsers();

        // Id
        if (newUser.getId() == null) {
            long newId = users.isEmpty() ? 1 : users.get(users.size() - 1).getId() + 1;
            newUser.setId(newId);
        }

        users.add(newUser);

        File dir = new File("data");
        if (!dir.exists())
            dir.mkdirs();

        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(USERS_FILE), users);
    }

}
