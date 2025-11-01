
import React, { createContext, useState } from "react";

export const EncryptionContext = createContext();

export const EncryptionProvider = ({ children }) => {
  const [username, setUsername] = useState("");
  const [receiver, setReceiver] = useState("");
  const [encryptionOutput, setEncryptionOutput] = useState("");
  const [decryptionOutput, setDecryptionOutput] = useState("");

  return (
    <EncryptionContext.Provider
      value={{
        username,
        setUsername,
        receiver,
        setReceiver,
        encryptionOutput,
        setEncryptionOutput,
        decryptionOutput,
        setDecryptionOutput,
      }}
    >
      {children}
    </EncryptionContext.Provider>
  );
};
