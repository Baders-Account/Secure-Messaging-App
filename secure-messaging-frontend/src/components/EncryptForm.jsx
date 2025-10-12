import React, { useState } from "react";
import { encryptMessage } from "../api/cryptoApi";

export default function EncryptForm() {
const [message, setMessage] = useState("");
  const [output, setOutput] = useState("");
  const [logs, setLogs] = useState("");

  const handleEncrypt = async () =>{
    const response = await encryptMessage(message);
    setOutput(response);

  };
  return(

     <div className="writeEncryption">
    <input
    id='message'
    type="text"
    placeholder="Enter your message"
    value={message}
    onChange={(e) => setMessage(e.target.value)}
    /> 
    
    
    <span className="enc-btn">
    <button onClick={handleEncrypt}>Encrypt</button>
    <button>Send</button>
    </span>

    { output && (
        <div>
            <p>CipherText: {output.ciphertext}</p>
            <p>Key: {output.key}</p>
            <p> Initilized Vectors: {output.iv}</p>
        </div>
    )}



        </div>

    )



    

  

  


}