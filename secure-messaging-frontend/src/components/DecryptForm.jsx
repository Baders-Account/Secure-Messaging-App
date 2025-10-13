import React, { useState } from "react";
import { decryptMessage } from "../api/cryptoApi";
import { useContext} from "react";
import  {EncryptionContext} from "../App.jsx" // Create a context for sharing state (in the case of keeping the encryption values)

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

      


    return(

        
    <div className="showEncryption">
        <div className="decrypted-message">
         
         {isMessageVisible && <h1>Message: {decryptionOutput}</h1>}


        </div>
        <span className="dec-btn" >
        <button>Verify</button>
        <button id="decrypt" onClick={handleDecrypt}>Decrypt</button>
        </span>
        

        </div>

        

    )

} 