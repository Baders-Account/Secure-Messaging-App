import React from "react";
import { Card, Form } from "react-bootstrap";

export default function LogPanel() {
  return (
    <div className="log-panel mt-4">
      <Card>
        <Card.Header as="h5">Logs</Card.Header>

        <Card.Body>
          <Form.Group controlId="logLevel">
            <Form.Label>Log Level:</Form.Label>
            <Form.Select multiple aria-label="Select log level">
              <option>INFO</option>
              <option>WARN</option>
              <option>ERROR</option>
              <option>FATAL</option>
            </Form.Select>
          </Form.Group>
        </Card.Body>
      </Card>
    </div>
  );
}
