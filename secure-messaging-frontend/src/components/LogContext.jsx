import React, { createContext, useState } from "react";

export const LogContext = createContext();

export function LogProvider({ children }) {
  const [logs, setLogs] = useState([]);

  const addLog = (level, message) => {
    const newLog = {
      id: Date.now(),
      level,
      message,
      timestamp: new Date().toLocaleTimeString(),
    };
    setLogs((prev) => [...prev, newLog]);
  };

  return (
    <LogContext.Provider value={{ logs, addLog }}>
      {children}
    </LogContext.Provider>
  );
}
