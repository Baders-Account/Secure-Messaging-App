import React, { useContext, useState } from "react";
import { encryptMessage } from "../api/cryptoApi";
import { Card, Button, Form,Alert } from "react-bootstrap";
import  {EncryptionContext} from "../App.jsx" // Create a context for sharing state (in the case of keeping the encryption values)



export default function EncryptForm() {
const [message, setMessage] = useState(""); // takes the message input from user
const {encryptionOutput,setEncryptionOutput } = useContext(EncryptionContext); // the output from the backend API
  const [logs, setLogs] = useState("");

 

  const handleEncrypt = async () =>{
    const response = await encryptMessage(message);  // calls the encryptMessage function from cryptoApi.jsx
    setEncryptionOutput (response); 

  };
 return (
  <div className="writeEncryption mt-4">
    <Card>
      <Card.Header as="h5">Encrypt Message</Card.Header>

      <Card.Body>
        {/* Input field */}
        <Form.Group className="mb-3">
          <Form.Label>Message</Form.Label>
          <Form.Control
            id="message"
            type="text"
            placeholder="Enter your message"
            value={message}
            onChange={(e) => setMessage(e.target.value)}
          />
        </Form.Group>

        {/* Buttons */}
        <div className="d-flex gap-2 justify-content-end">
          <Button variant="primary" onClick={handleEncrypt}>
            Encrypt
          </Button>
          <Button variant="success">Send</Button>
        </div>

        {/* Encryption Output */}
        {encryptionOutput && (
          <Alert variant="secondary" className="mt-3">
            <p className="mb-0">
              <strong>CipherText:</strong>
              <br />
              {encryptionOutput.ciphertext}
            </p>
          </Alert>
        )}
      </Card.Body>
    </Card>
  </div>
);



}