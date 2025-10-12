import React, { useState } from "react";
import { decryptMessage } from "../api/cryptoApi";


export default function DecryptForm() {
    const [ciphertext, setCiphertext] = useState("");
    const [output, setOutput] = useState("");
      const [isMessageVisible, setMessageVisible] = useState(false);

    const  handleDecrypt = async () => {
        const result= await decryptMessage(ciphertext);
        setOutput(result);
        setMessageVisible(true);
    };



    return(

        
    <div className="showEncryption">
         {isMessageVisible && <h1>Message: {output}</h1>}

        <span className="dec-btn" >
        <button>Verify</button>
        <button id="decrypt" onClick={handleDecrypt}>Decrypt</button>
        </span>
        

        </div>

        

    )

} 