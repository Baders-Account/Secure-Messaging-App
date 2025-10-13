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

    { encryptionOutput &&  (
          
        <div>
            <p>CipherText: {encryptionOutput.ciphertext}</p><br></br>
            <p>Key: {encryptionOutput.key}</p><br></br>
            <p> Initilized Vectors: {encryptionOutput.iv}</p>
        </div>
    )}
    



        </div>

    )



    

  

  


}