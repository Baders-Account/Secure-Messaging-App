
import React, { useState, useContext } from "react";
import { EncryptionContext } from "../EncryptionContext.jsx";
import { getPublicKey, sendMessage } from "../api/usersApi";
import { deriveAESKey, encryptAES } from "../api/cryptoApi";

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
      // Get receiver public key
      const peerPublicKey = (await getPublicKey(receiverInput)).data;
      console.log(`what is happening? ${peerPublicKey}`)

      // Import user's private key from localStorage
     const privateKey = localStorage.getItem("privateKey"); 
     //const privateKeyBytes = new Uint8Array(privateKeyArray);



      // Derive shared AES key
      const aesKey = await deriveAESKey(privateKey, peerPublicKey);

      // Encrypt message
      const { ciphertext, iv } = await encryptAES(aesKey, message);

      // Send to backend
      await sendMessage({
        sender: username,
        receiver: receiverInput,
        ciphertext,
        iv,
      });

      setEncryptionOutput(ciphertext);
      setReceiver(receiverInput);
      alert("Message encrypted and sent successfully");
    } catch (err) {
      console.error(err);
      alert("Encryption failed");
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
