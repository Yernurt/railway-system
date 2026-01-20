import { useState } from "react";
import API from "../api";
import "./Form.css";

export default function LocomotiveAddForm({ onAdd }) {
  const [locomotive, setLocomotive] = useState({
    locomotiveNumber: "",
    locomotiveType: "",
    statusLocomotive: "",
    departureStationLocomotive: "",
    destinationStationLocomotive: "",
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setLocomotive((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    API.post("/locomotives", locomotive)
      .then((res) => {
        alert("✅ Локомотив сәтті қосылды!");
        if (onAdd) onAdd(); // тізімді жаңарту үшін
      })
      .catch((err) => {
        console.error("Қате:", err);
        alert("❌ Қосу кезінде қате шықты.");
      });
  };

  return (
    <form onSubmit={handleSubmit} className="form">
      <h3>🚂 Жаңа локомотив қосу</h3>
      <input
        type="text"
        name="locomotiveNumber"
        placeholder="Локомотив нөмірі"
        onChange={handleChange}
        required
      />
      <input
        type="text"
        name="locomotiveType"
        placeholder="Локомотив типі"
        onChange={handleChange}
        required
      />
      <input
        type="text"
        name="statusLocomotive"
        placeholder="Статус (жөнелтілді, жолда...)"
        onChange={handleChange}
      />
      <input
        type="text"
        name="departureStationLocomotive"
        placeholder="Шығу станциясы"
        onChange={handleChange}
      />
      <input
        type="text"
        name="destinationStationLocomotive"
        placeholder="Бару станциясы"
        onChange={handleChange}
      />
      <button type="submit">Қосу</button>
    </form>
  );
}
