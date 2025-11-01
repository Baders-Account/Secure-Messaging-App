
package j16.secure.securemessage.controller;

import java.security.KeyPair;
import java.util.Base64;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import j16.secure.securemessage.service.ECDHService;

@RestController
public class KeyController {
    @Autowired
    private ECDHService ecdhService;

    @GetMapping("/generateKeyPair")
    public Map<String, String> getKeyPair() throws Exception {
        KeyPair keyPair = ecdhService.generateECKeyPair();
        String pub = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        String priv = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        System.out.println(priv);
        return Map.of("publicKey", pub, "privateKey", priv);
    }
}
