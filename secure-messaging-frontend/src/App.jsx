import React, { useContext } from "react";
import EncryptForm from "./components/EncryptForm";
import DecryptForm from "./components/DecryptForm";
import LoginForm from "./components/LoginForm";
import { EncryptionProvider, EncryptionContext } from "./EncryptionContext.jsx";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import FakeLogin from "./components/FakeLogin";
import { LogProvider } from "./components/LogContext.jsx";
import LogPanel from "./components/LogPanel.jsx";


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
      <LogPanel />
    </div>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <EncryptionProvider>
        <LogProvider>
          <Routes>
            <Route path="/" element={<AppContent />} />
            <Route path="/fake-login" element={<FakeLogin />} />
            
          </Routes>
        </LogProvider>
      </EncryptionProvider>
    </BrowserRouter>
  );
}
