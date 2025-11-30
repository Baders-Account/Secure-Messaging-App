
import React, { useContext } from "react";
import EncryptForm from "./components/EncryptForm";
import DecryptForm from "./components/DecryptForm";
import BruteForceDemo from "./components/BruteForceDemo";
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
      <BruteForceDemo />
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
