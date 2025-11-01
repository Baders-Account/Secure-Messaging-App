
import React, { useContext, useState } from "react";
import { EncryptionContext } from "../EncryptionContext.jsx";
import { getPublicKey, getInbox } from "../api/usersApi";
import { deriveAESKey, decryptAES, b64ToBuf, bufToB64 } from "../api/cryptoApi";

export default function DecryptForm() {
  const { username, decryptionOutput, setDecryptionOutput } = useContext(EncryptionContext);
  const [inbox, setInbox] = useState([]);
  const [selectedMessage, setSelectedMessage] = useState(null);

  const loadInbox = async () => {
    try {
      const res = await getInbox(username);
      setInbox(res.data);
    } catch (err) {
      console.error("Inbox load failed:", err);
      alert("Failed to load inbox");
    }
  };

  const handleDecrypt = async (msg) => {
  try {
    const senderPublicKey = (await getPublicKey(msg.sender)).data;
    console.log("Sender public key:", senderPublicKey);

    // private key
    const privateKey = localStorage.getItem("privateKey");
    console.log(`this is what i need${privateKey}`)
    //const privateKeyBytes = new Uint8Array(privateKeyArray);
    
    console.log(`iv: ${msg.iv}`)
    console.log(`cipherText: ${msg.ciphertext}`)


    // Derive AES key from ECDH
    const aesKey = await deriveAESKey(privateKey, senderPublicKey);
    console.log(`Aes Key?${aesKey}`)
    // Decrypt ciphertext (these are Base64)
    const plaintext = await decryptAES(aesKey, msg.ciphertext, msg.iv);

    console.log("Decrypted plaintext:", plaintext);
    setSelectedMessage(msg);
    setDecryptionOutput(plaintext);
  } catch (err) {
    console.error("Decryption failed:", err);
    alert("Decryption failed");
  }
};

  return (
    <div className="container mt-4 p-3 border rounded">
      <h3>Decrypt Messages</h3>
      <button className="btn btn-secondary mb-3" onClick={loadInbox}>
        Load Inbox
      </button>

      {inbox.map((msg) => (
        <div key={msg.id} className="border p-2 mb-2">
          <p><b>From:</b> {msg.sender}</p>
          <p><b>Ciphertext:</b> <code>{msg.ciphertext}</code></p>
          <button
            className="btn btn-sm btn-outline-success"
            onClick={() => handleDecrypt(msg)}
          >
            Decrypt
          </button>
        </div>
      ))}

      {selectedMessage && (
        <div className="mt-3">
          <h5>Decrypted Message:</h5>
          <p className="alert alert-info">{decryptionOutput}</p>
        </div>
      )}
    </div>
  );
}
