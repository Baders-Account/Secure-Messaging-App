package j16.secure.securemessage.model;

public class Message {

    private String id; // Unique ID for frontend key
    private String sender;
    private String senderPublicKeyBase64;
    private String receiver;
    private String ciphertext;
    private String iv;
    private String timestamp;

    public Message() {
    }

    public Message( String sender, String senderPublicKeyBase64, String receiver, String ciphertext, String iv, String timestamp) {
        
        this.sender = sender;
        this.senderPublicKeyBase64 = senderPublicKeyBase64;
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

    public String getSenderPublicKeyBase64() { return senderPublicKeyBase64; }
    
    public void setSenderPublicKeyBase64(String senderPublicKeyBase64) {
        this.senderPublicKeyBase64 = senderPublicKeyBase64;
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
