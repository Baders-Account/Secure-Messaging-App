const API_URL = "http://localhost:8081/api/crypto";

async function postJSON(url, data) {
  const response = await fetch(url, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
  if (!response.ok) throw new Error(await response.text());
  return await response.json();
}

// Get server’s ECDH public key
export async function getServerPublicKey() {
  const res = await fetch(`${API_URL}/public`);
  if (!res.ok) throw new Error("Failed to fetch server public key");
  return await res.text(); // Base64 string
}

// Derive and encrypt plaintext (server-side)
export async function deriveAndEncrypt(clientPublicKeyBase64, plaintext) {
  return await postJSON(`${API_URL}/derive-and-encrypt`, {
    clientPublicKeyBase64,
    plaintext,
  });
}

// Derive and decrypt ciphertext (server-side)
export async function deriveAndDecrypt(clientPublicKeyBase64, ciphertextB64, ivB64) {
  return await postJSON(`${API_URL}/derive-and-decrypt`, {
    clientPublicKeyBase64,
    ciphertextB64,
    ivB64,
  });
}

export async function deriveAndDecryptById(messageId) {
return await postJSON(`${API_URL}/derive-and-decrypt`, { messageId });
}