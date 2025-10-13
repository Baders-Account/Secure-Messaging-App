import React from "react";

// Context will hold both encryption and decryption outputs
export const EncryptionContext = React.createContext({
  encryptionOutput: null,
  setEncryptionOutput: () => {},
  decryptionOutput: null,
  setDecryptionOutput: () => {},
});