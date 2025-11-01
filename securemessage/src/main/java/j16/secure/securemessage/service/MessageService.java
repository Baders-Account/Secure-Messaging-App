package j16.secure.securemessage.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Service;
import j16.secure.securemessage.model.Message;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID; // generating unique IDs

@Service
public class MessageService {

    private static final String MESSAGES_FILE = "data/messages.json";
    private final ObjectMapper mapper = new ObjectMapper();

    // Read all messages
    private List<Message> getAllMessages() throws IOException {
        File file = new File(MESSAGES_FILE);
        if (!file.exists()) return new ArrayList<>();
        return mapper.readValue(file, new TypeReference<List<Message>>() {});
    }

    // Add a new encrypted message
    public void saveMessage(Message msg) throws IOException {
        List<Message> messages = getAllMessages();

        //Generate a unique ID if message doesn't already have one
        if (msg.getId() == null || msg.getId().isBlank()) {
            msg.setId(UUID.randomUUID().toString());
        }

        // Add timestamp if missing
        if (msg.getTimestamp() == null)
            msg.setTimestamp(Instant.now().toString());

        File dir = new File("data");
        if (!dir.exists()) dir.mkdirs();

        messages.add(msg);
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(MESSAGES_FILE), messages);
    }

    // Retrieve messages sent to a specific receiver
    public List<Message> getMessagesForUser(String receiver) throws IOException {
        List<Message> all = getAllMessages();
        List<Message> results = new ArrayList<>();
        for (Message m : all) {
            if (m.getReceiver().equalsIgnoreCase(receiver)) {
                results.add(m);
            }
        }
        return results;
    }
}
