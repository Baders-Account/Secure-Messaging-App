import React, { useState } from "react";
import { decryptMessage } from "../api/cryptoApi";
import { useContext} from "react";
import  {EncryptionContext} from "../App.jsx" // Create a context for sharing state (in the case of keeping the encryption values)
import { Card, Button, Alert } from "react-bootstrap";

export default function DecryptForm() {
    //const [ciphertext, setCiphertext] = useState("");
    const {
    encryptionOutput,
    decryptionOutput,
    setDecryptionOutput,
  } = useContext(EncryptionContext);
    
    const [isMessageVisible, setMessageVisible] = useState(false);
  
    const  handleDecrypt = async () => {
        const respond= await decryptMessage(encryptionOutput.ciphertext, encryptionOutput.key, encryptionOutput.iv);  // calls the decryptMessage function from cryptoApi.jsx
        setDecryptionOutput(respond);
        setMessageVisible(true);
    };

      
return (
  <div className="showEncryption mt-4">
    <Card>
      <Card.Header as="h5">Decrypt Message</Card.Header>

      <Card.Body>
        {/* Decrypted Message Area */}
        <Alert
          variant={isMessageVisible ? "success" : "secondary"}
          className="decrypted-message text-break"
        >
          {isMessageVisible ? (
            <p className="mb-0">
              <strong>Message:</strong> {decryptionOutput}
            </p>
          ) : (
            <p className="mb-0">Decrypted message will appear here.</p>
          )}
        </Alert>

        {/* Buttons Section */}
        <div className="d-flex gap-2 justify-content-end mt-3">
          <Button variant="info">Verify</Button>
          <Button id="decrypt" variant="primary" onClick={handleDecrypt}>
            Decrypt
          </Button>
        </div>
      </Card.Body>
    </Card>
  </div>
);

   

} 