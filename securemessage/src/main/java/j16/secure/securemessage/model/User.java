package j16.secure.securemessage.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Lob
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "pwd_salt_hex")
    private String pwdSaltHex;

    @Column(name = "pwd_iterations")
    private int pwdIterations;

    @Lob
    @Column(name = "ec_public_key")
    private String ecPublicKey;

    public User() {
    }

    public User(String username, String pwdSaltHex, String passwordHash, String ecPublicKey, int pwdIterations) {
        this.username = username;
        this.pwdSaltHex = pwdSaltHex;
        this.passwordHash = passwordHash;
        this.ecPublicKey = ecPublicKey;
        this.pwdIterations = pwdIterations;
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getPwdSaltHex() {
        return pwdSaltHex;
    }

    public void setPwdSaltHex(String pwdSaltHex) {
        this.pwdSaltHex = pwdSaltHex;
    }

    public int getPwdIterations() {
        return pwdIterations;
    }

    public void setPwdIterations(int pwdIterations) {
        this.pwdIterations = pwdIterations;
    }

    public String getEcPublicKey() {
        return ecPublicKey;
    }

    public void setEcPublicKey(String ecPublicKey) {
        this.ecPublicKey = ecPublicKey;
    }
}
