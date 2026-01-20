import { useEffect, useState } from "react";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

const socketUrl = "http://localhost:8090/ws"; // Backend WebSocket endpoint
let stompClient = null;

export default function WarningWagons() {
  const [warnings, setWarnings] = useState([]);

  useEffect(() => {
    const socket = new SockJS(socketUrl);

    stompClient = new Client({
      webSocketFactory: () => socket,
      onConnect: () => {
        console.log("📡 WebSocket қосылды");

        stompClient.subscribe("/topic/manualCheck", (msg) => {
          const data = JSON.parse(msg.body);
          setWarnings((prev) => [...prev, { type: "manual", ...data }]);
        });

        stompClient.subscribe("/topic/speedWarning", (msg) => {
          const text = msg.body;
          setWarnings((prev) => [...prev, { type: "speed", text }]);
        });
      },
    });

    stompClient.activate();

    return () => {
      if (stompClient) {
        stompClient.deactivate();
      }
    };
  }, []);

  return (
    <div>
      <h2>⚠️ Қауіпті вагондар</h2>
      {warnings.length === 0 && <p>Бәрі жақсы 🙂</p>}
      <ul>
        {warnings.map((warn, i) => (
          <li key={i}>
            {warn.type === "manual" ? (
              <>
                🚨 Қолмен тексеру керек: Вагон № {warn.wagonNumber}
                <br />
                <video width="400" controls src={warn.video}></video>
              </>
            ) : (
              <span>{warn.text}</span>
            )}
          </li>
        ))}
      </ul>
    </div>
  );
}
