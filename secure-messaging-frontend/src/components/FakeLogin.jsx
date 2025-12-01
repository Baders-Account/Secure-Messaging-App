import React, { useState } from "react";
import { Card, Button, Form } from "react-bootstrap";

export default function FakeLogin() {
  const [userName, setUserName] = useState("");
  const [password, setPassword] = useState("");

  const handlePhish = (e) => {
    e.preventDefault();

    console.log("Phishing simulation captured credentials:", {
      username: userName,
      password: password,
    });

    alert("Phishing simulation: credentials captured.");
  };

  return (
    <Card className="mt-4">
      <Card.Header className="text-center fw-bold">
        Login
      </Card.Header>

      <Card.Body>
        <Form onSubmit={handlePhish}>
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
      </Card.Body>
    </Card>
  );
}
