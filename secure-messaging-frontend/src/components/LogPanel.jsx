
import React from 'react';

export default function LogPanel() {
    return (
      <div className="log-panel">
        
        <h3>Logs:</h3>
        <label htmlFor="log-level">Log Level:</label>
        <select className="dropdown" multiple>
            <option>INFO</option>
            <option>WARN</option>
            <option>ERROR</option>
            <option>FATAL</option>
            



        </select>
      </div>
    );
  }