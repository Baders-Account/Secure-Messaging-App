import React, { useState } from "react";

export default function BruteForceDemo() {
  const [password, setPassword] = useState("123");
  const [message, setMessage] = useState("hello secret");
  const [useStrong, setUseStrong] = useState(false);
  const [ciphertext, setCiphertext] = useState("");
  const [iv, setIv] = useState("");
  const [attackResult, setAttackResult] = useState(null);
  const [loading, setLoading] = useState(false);

  const encryptWithPassword = async () => {
    try {
      const res = await fetch("http://localhost:8081/api/crypto/encrypt-with-password", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ plaintext: message, password, useStrong })
      });
      const data = await res.json();
      setCiphertext(data.ciphertextB64);
      setIv(data.ivB64);
      alert(`Encrypted with ${useStrong ? 'strong' : 'weak'} password!`);
    } catch (err) {
      console.error("Encryption failed:", err);
      alert("Encryption failed");
    }
  };

  const runBruteForce = async () => {
    setLoading(true);
    setAttackResult(null);
    
    try {
      const res = await fetch("http://localhost:8081/api/crypto/brute-force-attack", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ 
          ciphertext, 
          iv, 
          knownPlaintext: "hello",
          useStrong
        })
      });
      const data = await res.json();
      setAttackResult(data);
    } catch (err) {
      console.error("Attack failed:", err);
      alert("Attack failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container mt-4 p-3 border rounded">
      <h3>Brute Force Attack Demo</h3>
      
      <div className="mb-3">
        <label>Password:</label>
        <input 
          className="form-control" 
          value={password} 
          onChange={(e) => setPassword(e.target.value)}
        />
        <small className="text-muted">Try: 123, abc, or a1b2</small>
      </div>
      
      <div className="mb-3">
        <label>Message:</label>
        <input 
          className="form-control" 
          value={message} 
          onChange={(e) => setMessage(e.target.value)}
        />
      </div>

      <div className="mb-3 form-check">
        <input 
          type="checkbox" 
          className="form-check-input" 
          id="useStrong"
          checked={useStrong}
          onChange={(e) => setUseStrong(e.target.checked)}
        />
        <label className="form-check-label" htmlFor="useStrong">
          Use Strong Password Protection (PBKDF2 with 100,000 iterations)
        </label>
      </div>
      
      <button className="btn btn-primary me-2" onClick={encryptWithPassword}>
        Encrypt with {useStrong ? 'Strong' : 'Weak'} Password
      </button>
      
      {ciphertext && (
        <div>
          <div className="mt-3">
            <p><b>Ciphertext:</b> <code>{ciphertext.substring(0, 40)}...</code></p>
            <p><b>Mode:</b> {useStrong ? '🔒 Strong (PBKDF2)' : '⚠️ Weak (SHA-256)'}</p>
          </div>
          
          <button 
            className="btn btn-danger" 
            onClick={runBruteForce}
            disabled={loading}
          >
            {loading ? "Attacking..." : "Run Comprehensive Brute Force Attack"}
          </button>
        </div>
      )}
      
      {attackResult && (
        <div className={`alert mt-3 ${attackResult.success ? 'alert-danger' : 'alert-success'}`}>
          <h5>{attackResult.success ? "⚠️ Attack Successful!" : "✓ Attack Failed"}</h5>
          <p><b>Method Used:</b> {attackResult.attackType}</p>
          <p><b>Total Attempts:</b> {attackResult.attempts}</p>
          <p><b>Time Taken:</b> {attackResult.timeTakenMs} ms</p>
          {attackResult.success && (
            <p><b>Cracked Password:</b> <code>{attackResult.crackedPassword}</code></p>
          )}
          <p>{attackResult.message}</p>
        </div>
      )}
      
      <div className="alert alert-info mt-3">
        <h6>🛡️ Comprehensive Brute Force Attack:</h6>
        <p>The attack automatically tries three methods in sequence:</p>
        <ol>
          <li><b>Numeric:</b> 0-9999 (10,000 combinations)</li>
          <li><b>Alphabetic:</b> a-zzzz (lowercase and uppercase)</li>
          <li><b>Alphanumeric:</b> All combinations of letters and numbers</li>
        </ol>
        <hr />
        <p><b>Defense:</b> PBKDF2 with 100,000 iterations makes each attempt ~100ms</p>
        <p><b>Impact:</b> 10,000 attempts = 0.1s (weak) vs. 17 minutes (strong)</p>
      </div>
    </div>
  );
}
