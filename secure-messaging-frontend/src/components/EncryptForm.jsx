import React, { useState, useContext } from "react";
import { EncryptionContext } from "../EncryptionContext.jsx";
import { getPublicKey, sendMessage } from "../api/usersApi";
import { getServerPublicKey, deriveAndEncrypt } from "../api/cryptoApi";

export default function EncryptForm() {
  const [message, setMessage] = useState("");
  const [receiverInput, setReceiverInput] = useState("");
  const { username, setReceiver, encryptionOutput, setEncryptionOutput } =
    useContext(EncryptionContext);
  

  const handleEncryptAndSend = async () => {
    if (!receiverInput || !message) {
      alert("Please enter both receiver and message");
      return;
    }

    try {
      // Fetch receiver’s public key 
      const peerPublicKey = await getPublicKey(receiverInput);
      console.log("Receiver public key:", peerPublicKey);

      // Fetch server’s own public key
      const serverPublicKey = await getServerPublicKey();
      console.log("Server public key:", serverPublicKey);

      //  derive the AES key & encrypt message
      const { ciphertextB64, ivB64 } = await deriveAndEncrypt(
        peerPublicKey, // the receiver’s public key
        message        // plaintext to encrypt
      );
      const date= new Date();
      // Send encrypted message to backend for storage
      await sendMessage({
        
        sender: username,
        receiver: receiverInput,
        ciphertext: ciphertextB64,
        iv: ivB64,
        timestamp:date
      });

      // Update UI
      setEncryptionOutput(ciphertextB64);
      setReceiver(receiverInput);
      alert("Message encrypted and sent successfully!");
    } catch (err) {
      console.error("Encryption failed:", err);
      alert("Encryption failed. Check the console for details.");
    }
  };

  return (
    <div className="container mt-4 p-3 border rounded">
      <h3>Encrypt & Send Message</h3>

      <input
        type="text"
        placeholder="Receiver username"
        className="form-control my-2"
        value={receiverInput}
        onChange={(e) => setReceiverInput(e.target.value)}
      />

      <textarea
        placeholder="Write your message..."
        className="form-control my-2"
        rows={3}
        value={message}
        onChange={(e) => setMessage(e.target.value)}
      />

      <button className="btn btn-primary" onClick={handleEncryptAndSend}>
        Encrypt & Send
      </button>

      {encryptionOutput && (
        <div className="mt-3">
          <h5>Encrypted Message (Base64):</h5>
          <code className="text-break">{encryptionOutput}</code>
        </div>
      )}
    </div>
  );
}
