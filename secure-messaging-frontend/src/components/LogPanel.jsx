import React, { useContext } from "react";
import { Card } from "react-bootstrap";
import { LogContext } from "./LogContext"; // fixed path

export default function LogPanel() {
  const { logs } = useContext(LogContext);

  return (
    <div className="log-panel mt-4">
      <Card>
        <Card.Header as="h5">Logs</Card.Header>
        <Card.Body style={{ maxHeight: "200px", overflowY: "auto" }}>
          {logs.length === 0 && <p>No logs yet.</p>}
          {logs.map((log, idx) => (
            <div key={idx}>
              <strong>[{log.timestamp}] [{log.level}]</strong> {log.message}
            </div>
          ))}
        </Card.Body>
      </Card>
    </div>
  );
}
