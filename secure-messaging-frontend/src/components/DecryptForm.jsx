import React, { useContext, useState, useEffect } from "react";
import { EncryptionContext } from "../EncryptionContext.jsx";
import { getPublicKey, getInbox } from "../api/usersApi";
//import { getServerPublicKey, deriveAndDecrypt } from "../api/cryptoApi";
import { deriveAndDecryptById } from "../api/cryptoApi";


export default function DecryptForm() {
  const { username, decryptionOutput, setDecryptionOutput } =
    useContext(EncryptionContext);
  const [inbox, setInbox] = useState([]);
  const [selectedMessage, setSelectedMessage] = useState(null);
 
  useEffect(() => {
    if (username) loadInbox();
  }, [username]);

  const loadInbox = async () => {
    try {
      const messages = await getInbox(username);
      console.log("Inbox:", messages);
      setInbox(messages || []); // fallback to empty array
    } catch (err) {
      console.error("Inbox load failed:", err);
      alert("Failed to load inbox");
    }
  };

  // Decrypt a selected message 
  const handleDecrypt = async (msg) => {
    try {
      console.log("Decrypting message id:", msg.id);
      const { plaintext } = await deriveAndDecryptById(msg.id);


      setSelectedMessage(msg);
      setDecryptionOutput(plaintext);
      console.log("Decrypted plaintext:", plaintext);
    } catch (err) {
      console.error("Decryption failed:", err);
      alert("Decryption failed. Check the console for details.");
    }
  };

  return (
    <div className="container mt-4 p-3 border rounded">
      <h3>Decrypt Messages (Server-Side)</h3>

      <button className="btn btn-secondary mb-3" onClick={loadInbox}>
        Load Inbox
      </button>

      {inbox.length>0 ? (inbox.map((msg) => (
        <div key={msg.id} className="border p-2 mb-2">
          <p>
            <b>From:</b> {msg.sender}
          </p>
          <p>
            <b>Ciphertext:</b> <code className="text-break">{msg.ciphertext}</code>
          </p>
          <button
            className="btn btn-sm btn-outline-success"
            onClick={() => handleDecrypt(msg)}
          >
            Decrypt
          </button>
        </div>
      ))
    ) : ( <p>No Messages in your inbox. </p>)
      }

      {selectedMessage && (
        <div className="mt-3">
          <h5>Decrypted Message:</h5>
          <p className="alert alert-info">{decryptionOutput}</p>
        </div>
      )}
    </div>
  );
}
