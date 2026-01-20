import { useEffect, useState } from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { useNavigate, Link } from "react-router-dom";
import API from "../api";
import "../style/OperatorPanel.css";

export default function OperatorPanel() {
  const [wagons, setWagons] = useState([]);
  const [selectedWagon, setSelectedWagon] = useState(null);
  const [suspiciousList, setSuspiciousList] = useState([]);
  const [manualFixNumber, setManualFixNumber] = useState("");
  const [filters, setFilters] = useState({
    wagonType: "",
    status: "",
    departureStation: "",
    destinationStation: "",
  });

  const navigate = useNavigate();
  const station = localStorage.getItem("station");

  const fetchWagons = () => {
    let query = `/wagons/filter?`;
    Object.entries(filters).forEach(([key, value]) => {
      if (value) query += `${key}=${encodeURIComponent(value)}&`;
    });
    if (station) {
      query += `station=${encodeURIComponent(station)}&`;
    }
    API.get(query)
      .then((res) => setWagons(res.data))
      .catch((err) => console.error("Қате:", err));
  };

  useEffect(() => {
    fetchWagons();
    const socket = new SockJS("http://localhost:8090/ws");
    const stompClient = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000,
      onConnect: () => {
        console.log("📡 WebSocket қосылды");
        stompClient.subscribe("/topic/wagonUpdates", (message) => {
          const updatedWagon = JSON.parse(message.body);
          setWagons((prevWagons) =>
            prevWagons.map((w) =>
              w.wagonNumber === updatedWagon.wagonNumber ? updatedWagon : w
            )
          );
        });
        stompClient.subscribe("/topic/manualCheck", (message) => {
          const suspiciousData = JSON.parse(message.body);
          setSuspiciousList((prev) => [suspiciousData, ...prev]);
        });
      },
    });
    stompClient.activate();
    return () => stompClient.deactivate();
  }, []);

  const handleManualFix = async () => {
    if (!manualFixNumber || !selectedWagon) return;
    const payload = {
      suspiciousNumber: selectedWagon.wagonNumber,
      correctNumber: manualFixNumber,
      station: selectedWagon.station || "",
      lastUpdated: selectedWagon.lastUpdated || "",
      speedKmh: selectedWagon.speedKmh || "",
      identificationStatus: selectedWagon.identificationStatus || "",
      video: selectedWagon.video || "",
    };
    try {
      const res = await API.post("/wagons/manual-fix", payload);
      alert(res.data);
      setSuspiciousList((prev) =>
        prev.filter((item) => item.wagonNumber !== selectedWagon.wagonNumber)
      );
      setSelectedWagon(null);
      setManualFixNumber("");
      fetchWagons();
    } catch (err) {
      alert("❌ Сақтау қатесі");
      console.error(err);
    }
  };

  return (
    <div className="operator-container">
      <div className="left-panel">
        <h2>🚂 Оператор панелі</h2>
        <div style={{ marginBottom: "1rem" }}>
          <Link to="/consists">
            <button className="consist-link-btn">📋 Составтарды көру</button>
          </Link>
        </div>
        <table className="custom-table">
          <thead>
            <tr>
              <th>№</th>
              <th>Нөмір</th>
              <th>Статус</th>
              <th>Скан статус</th>
              <th>Станция</th>
              <th>Жылдамдық</th>
              <th>Қауіп</th>
            </tr>
          </thead>
          <tbody>
            {wagons.map((w, index) => (
              <tr
                key={w.id}
                onClick={() => setSelectedWagon(w)}
                className={w.suspicious ? "suspicious-row" : ""}
              >
                <td>{index + 1}</td>
                <td>{w.wagonNumber}</td>
                <td>{w.status}</td>
                <td className="clickable-status">{w.identificationStatus}</td>
                <td>{w.station}</td>
                <td>{w.speedKmh} км/сағ</td>
                <td>{w.suspicious ? "⚠️ Күдікті" : "✅"}</td>
              </tr>
            ))}
          </tbody>
        </table>

        {selectedWagon?.locomotive?.locomotiveNumber && (
          <button
            onClick={() => {
              if (selectedWagon?.locomotive?.locomotiveNumber) {
                navigate(`/consists/${selectedWagon.locomotive.locomotiveNumber}`);
              } else {
                alert("Бұл вагонға байланысты состав табылмады!");
              }
            }}
          >
            🔍 Составты қарау
          </button>
        )}

        {suspiciousList.length > 0 && (
          <div className="alert-box">
            <h3>⚠️ Жаңа күдікті вагондар</h3>
            {suspiciousList.map((item, index) => (
              <div className="suspicious-alert" key={index}>
                <p>🚨 Вагон № <strong>{item.wagonNumber}</strong></p>
                <p>📝 Себеп: {item.reason}</p>
                {item.video ? (
                  item.video.includes("youtube.com") || item.video.includes("youtu.be") ? (
                    <iframe
                      width="100%"
                      height="315"
                      src={item.video.replace("watch?v=", "embed/")}
                      title="YouTube video player"
                      frameBorder="0"
                      allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                      allowFullScreen
                    ></iframe>
                  ) : item.video.includes("t.me") ? (
                    <p>
                      📲 Видео Telegram-да:{" "}
                      <a href={item.video} target="_blank" rel="noopener noreferrer">
                        {item.video}
                      </a>
                    </p>
                  ) : (
                    <video src={item.video} controls width="100%" />
                  )
                ) : (
                  <p>🎞️ Видео табылмады</p>
                )}
                <button onClick={() => setSelectedWagon(item)}>
                  Қолмен өңдеу
                </button>
              </div>
            ))}
          </div>
        )}
      </div>

      <div className="right-panel">
        {selectedWagon ? (
          <div className="video-box">
            <h3>🎥 Вагон № {selectedWagon.wagonNumber}</h3>
            {selectedWagon.suspicious && (
              <p style={{ color: "red", fontWeight: "bold" }}>
                ⚠️ Бұл күдікті вагон
              </p>
            )}

            {selectedWagon.video ? (
              selectedWagon.video.includes("youtube.com") || selectedWagon.video.includes("youtu.be") ? (
                <iframe
                  width="100%"
                  height="315"
                  src={selectedWagon.video.replace("watch?v=", "embed/")}
                  title="YouTube video player"
                  frameBorder="0"
                  allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                  allowFullScreen
                ></iframe>
              ) : selectedWagon.video.includes("t.me") ? (
                <p>
                  📲 Видео Telegram-да:{" "}
                  <a href={selectedWagon.video} target="_blank" rel="noopener noreferrer">
                    {selectedWagon.video}
                  </a>
                </p>
              ) : (
                <video src={selectedWagon.video} controls width="100%" />
              )
            ) : (
              <p>🎞️ Видео табылмады.</p>
            )}

            <div className="manual-fix-form">
              <p>✏️ Дұрыс вагон нөмірін енгізіңіз:</p>
              <input
                type="text"
                placeholder="Мысалы: 98004229"
                value={manualFixNumber}
                onChange={(e) => setManualFixNumber(e.target.value)}
              />
              <button onClick={handleManualFix}>✅ Сақтау</button>
            </div>
          </div>
        ) : (
          <p>👉 Оң жақта бейнені көру үшін кестеден немесе күдікті тізімнен таңдаңыз.</p>
        )}
      </div>
    </div>
  );
}
