
import React, { useState, useContext } from "react";
import { Card, Button, Form, ButtonGroup } from "react-bootstrap";
import { registerUser, getPublicKey } from "../api/usersApi";
import { generateKeyPair } from "../api/cryptoApi";
import { EncryptionContext } from "../EncryptionContext.jsx";

export default function LoginForm() {
  const [userName, setUserName] = useState("");
  const [password, setPassword] = useState("");
  const [loggingVisible, setLoggingVisible] = useState(true);
  const { setUsername } = useContext(EncryptionContext);

  // Handle login (for now just set session)
  const handleLogIn = async (e) => {
  e.preventDefault();

  if (!userName || !password) {
    alert("Please enter both username and password");
    return;
  }

  //  restore private key after login
  const savedKey = localStorage.getItem("privateKey");
  if (!savedKey) {
    alert(" No private key found. Please register again.");
    return;
  }

  setUsername(userName);
  localStorage.setItem("username", userName);

  alert(`Logged in as ${userName}`);
};


  // Handle registration
  
  const handleRegister = async (e) => {
  e.preventDefault();

  if (!userName || !password) {
    alert("Please fill in both fields");
    return;
  }

  try {
    // Generate EC key pair
    const { publicKey, privateKey } = await generateKeyPair();

    //  Save private key locally (Base64)
    localStorage.setItem("privateKey", privateKey);

    //  Send public key to backend
    await registerUser({
      username: userName,
      password: password, 
      ecPublicKey: publicKey, 
    });

    // Save username in session
    setUsername(userName);
    localStorage.setItem("username", userName);

    alert(`User ${userName} registered successfully.`);
  } catch (err) {
    console.error(err);
    alert("Registration failed.");
  }
};

  return (
    <Card className="mt-4">
      <Card.Header>
        <ButtonGroup aria-label="Basic example" className="w-100">
          <Button
            variant={loggingVisible ? "primary" : "secondary"}
            onClick={() => setLoggingVisible(true)}
          >
            Login
          </Button>
          <Button
            variant={!loggingVisible ? "primary" : "secondary"}
            onClick={() => setLoggingVisible(false)}
          >
            Register
          </Button>
        </ButtonGroup>
      </Card.Header>

      <Card.Body>
        {loggingVisible ? (
          <Form onSubmit={handleLogIn}>
            <Form.Group className="mb-3">
              <Form.Control
                type="text"
                placeholder="Enter your username"
                value={userName}
                onChange={(e) => setUserName(e.target.value)}
                required
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Control
                type="password"
                placeholder="Enter your password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </Form.Group>
            <Button variant="success" type="submit" className="w-100">
              Log in
            </Button>
          </Form>
        ) : (
          <Form onSubmit={handleRegister}>
            <Form.Group className="mb-3">
              <Form.Control
                type="text"
                placeholder="Enter your username"
                value={userName}
                onChange={(e) => setUserName(e.target.value)}
                required
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Control
                type="password"
                placeholder="Enter your password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </Form.Group>
            <Button variant="primary" type="submit" className="w-100">
              Register
            </Button>
          </Form>
        )}
      </Card.Body>
    </Card>
  );
}
