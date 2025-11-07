package j16.secure.securemessage.model;

public class Message {

    private String id; // Unique ID for frontend key
    private String sender;
    private String receiver;
    private String ciphertext;
    private String iv;
    private String timestamp;

    public Message() {
    }

    public Message( String sender, String receiver, String ciphertext, String iv, String timestamp) {
        
        this.sender = sender;
        this.receiver = receiver;
        this.ciphertext = ciphertext;
        this.iv = iv;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getCiphertext() {
        return ciphertext;
    }

    public void setCiphertext(String ciphertext) {
        this.ciphertext = ciphertext;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
