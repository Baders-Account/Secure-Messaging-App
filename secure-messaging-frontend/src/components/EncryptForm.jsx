import React, { useContext, useState } from "react";
import { encryptMessage } from "../api/cryptoApi";
import  {EncryptionContext} from "../App.jsx" // Create a context for sharing state (in the case of keeping the encryption values)
export default function EncryptForm() {
const [message, setMessage] = useState(""); // takes the message input from user
const {encryptionOutput,setEncryptionOutput } = useContext(EncryptionContext); // the output from the backend API
  const [logs, setLogs] = useState("");

 

  const handleEncrypt = async () =>{
    const response = await encryptMessage(message);  // calls the encryptMessage function from cryptoApi.jsx
    setEncryptionOutput (response); 

  };
  return(



     <div className="writeEncryption">
        { encryptionOutput &&  (
          
        <div className="EncryptionOutput"> 
            <p><strong>CipherText:</strong><br></br> {encryptionOutput.ciphertext}</p>
            <p><strong>Key: </strong> {encryptionOutput.key}</p>
            <p> <strong>Initilized Vectors: </strong> <br></br> {encryptionOutput.iv}</p>
        </div>
    )}

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

 
    



        </div>

    )



    

  

  


}