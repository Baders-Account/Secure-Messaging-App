const API_URL = "http://localhost:8081"; // backend port!

export async function registerUser(userData) {
  const response = await fetch(`${API_URL}/register`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(userData),
  });

  if (!response.ok) throw new Error(await response.text());
  return response;
}

export async function loginUser(credentials) {
  const response = await fetch(`${API_URL}/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(credentials),
  });
  return response;
}

export async function getPublicKey(username) {
  const res = await fetch(`${API_URL}/users/${username}/publicKey`);
  if (!res.ok) throw new Error("Failed to fetch public key");
  return await res.text();
}

export async function getInbox(username) {
  const res = await fetch(`${API_URL}/messages/inbox/${username}`);
  if (!res.ok) throw new Error("Failed to load inbox");
  return await res.json();
}

export async function sendMessage(messageData) {
  const res = await fetch(`${API_URL}/messages/send`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(messageData),
  });
  if (!res.ok) throw new Error("Failed to send message");
  return await res.text();
}
