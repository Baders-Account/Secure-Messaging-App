// Base64 Helpers//

export function bufToB64(buf) {
  let binary = "";
  const bytes = new Uint8Array(buf);
  for (let i = 0; i < bytes.length; i++) {
    binary += String.fromCharCode(bytes[i]);
  }
  return btoa(binary);
}

export function b64ToBuf(b64) {
  const binary = atob(b64);
  const bytes = new Uint8Array(binary.length);
  for (let i = 0; i < binary.length; i++) {
    bytes[i] = binary.charCodeAt(i);
  }
  return bytes.buffer;
}


// Key Generation 

export async function generateKeyPair() {
  const keyPair = await crypto.subtle.generateKey(
    { name: "ECDH", namedCurve: "P-256" },
    true,
    ["deriveBits", "deriveKey"]
  );

  // Export keys to raw buffer
  const pubKey = await crypto.subtle.exportKey("spki", keyPair.publicKey);
  const privKey = await crypto.subtle.exportKey("pkcs8", keyPair.privateKey);

  return {
    publicKey: bufToB64(pubKey),
    privateKey: bufToB64(privKey)
  };
}


//  ECDH Derive AES Key 

export async function deriveAESKey(privateKeyB64, peerPublicKeyB64) {
  const privateKeyBuf = b64ToBuf(privateKeyB64);
  const peerKeyBuf = b64ToBuf(peerPublicKeyB64);
  console.log("priv type:",  privateKeyB64);
  console.log("peer type:",  peerPublicKeyB64);


  const privateKey = await crypto.subtle.importKey(
    "pkcs8",
    privateKeyBuf,
    { name: "ECDH", namedCurve: "P-256" },
    false,
    ["deriveBits"]
  );

  const peerPublicKey = await crypto.subtle.importKey(
    "spki",
    peerKeyBuf,
    { name: "ECDH", namedCurve: "P-256" },
    false,
    []
  );

  const sharedSecret = await crypto.subtle.deriveBits(
    { name: "ECDH", public: peerPublicKey },
    privateKey,
    256
  );

  return crypto.subtle.importKey(
    "raw",
    sharedSecret,
    { name: "AES-CBC" },
    false,
    ["encrypt", "decrypt"]
  );
}


// Encrypt and Decrypt  //

export async function encryptAES(aesKey, plaintext) {
  const iv = crypto.getRandomValues(new Uint8Array(16));
  const data = new TextEncoder().encode(plaintext);

  const encrypted = await crypto.subtle.encrypt(
    { name: "AES-CBC", iv },
    aesKey,
    data
  );

  return {
    ciphertext: bufToB64(encrypted),
    iv: bufToB64(iv)
  };
}

export async function decryptAES(aesKey, ciphertextB64, ivB64) {
  const ciphertextBuf = b64ToBuf(ciphertextB64);
  const ivBuf = new Uint8Array(b64ToBuf(ivB64));

  const decrypted = await crypto.subtle.decrypt(
    { name: "AES-CBC", iv: ivBuf },
    aesKey,
    ciphertextBuf
  );

  return new TextDecoder().decode(decrypted);
}
