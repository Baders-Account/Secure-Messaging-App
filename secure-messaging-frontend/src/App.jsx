
import React, { useContext } from "react";
import EncryptForm from "./components/EncryptForm";
import DecryptForm from "./components/DecryptForm";
import LoginForm from "./components/LoginForm";
import { EncryptionProvider, EncryptionContext } from "./EncryptionContext.jsx";

function AppContent() {
  const { username, setUsername } = useContext(EncryptionContext);

  if (!username) {
    return <LoginForm onLogin={setUsername} />;
  }

  return (
    <div className="container mt-5">
      <h2>Welcome, {username}</h2>
      <EncryptForm />
      <DecryptForm />
    </div>
  );
}

export default function App() {
  return (
    <EncryptionProvider>
      <AppContent />
    </EncryptionProvider>
  );
}
