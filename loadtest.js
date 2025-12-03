import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
  vus: 5,            // 5 virtual users
  duration: '30s',   // 30 seconds
};

export default function () {
  const payload = JSON.stringify({
    sender: "attackerUser",
    receiver: "victimUser",
    content: "Test message"
  });

  const params = {
    headers: {
      "Content-Type": "application/json"
    }
  };

  const res = http.post("http://localhost:8081/messages/send", payload, params);

  check(res, {
    "status is 200": (r) => r.status === 200,
    "status is 429": (r) => r.status === 429
  });

  sleep(0.5); // 2 requests per second per virtual user
}
