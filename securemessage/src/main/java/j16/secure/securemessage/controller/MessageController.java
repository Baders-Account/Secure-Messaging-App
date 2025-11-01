package j16.secure.securemessage.controller;

import j16.secure.securemessage.model.Message;
import j16.secure.securemessage.service.MessageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
@CrossOrigin(origins = "http://localhost:3000")
public class MessageController {

    @Autowired
    private MessageService messageService;

    // Store an encrypted message in JSON file
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody Message msg) {
        try {
            messageService.saveMessage(msg);
            return ResponseEntity.ok("Message sent successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving message");
        }
    }

    // Retrieve all messages for a receiver
    @GetMapping("/inbox/{username}")
    public ResponseEntity<List<Message>> getMessages(@PathVariable String username) {
        try {
            List<Message> messages = messageService.getMessagesForUser(username);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
