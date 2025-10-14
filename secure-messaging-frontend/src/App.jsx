import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'
import EncryptForm from './components/EncryptForm'
import DecryptForm from './components/DecryptForm'
import LogPanel from './components/LogPanel'

import React, { useContext } from "react";
export const EncryptionContext = React.createContext();// Import the ciphtertext and key and iv from EncryptForm.jsx

function App() {
    const [encryptionOutput, setEncryptionOutput] = useState(null);
  const [decryptionOutput, setDecryptionOutput] = useState(null);
  

  return (
    <>
    
      
   
    

    {/*
   
    <h3>Logs: </h3>
    <span className="logs">
        <textarea> </textarea>

    </span>
   */} 
      <EncryptionContext.Provider value={{encryptionOutput,setEncryptionOutput,decryptionOutput,setDecryptionOutput}}>  {/*Provide the output value to the context*/}
      <EncryptForm />
      <DecryptForm/>
      <LogPanel/>
      
</EncryptionContext.Provider>  {/*Provide the output value to the context*/}



    </>
    
  
  ) 
  
}

export default App
