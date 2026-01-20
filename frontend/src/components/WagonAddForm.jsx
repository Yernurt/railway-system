import { useState } from "react";
import API from "../api";
import "./Form.css";

export default function WagonAddForm({ onAdd }) {
  const [wagon, setWagon] = useState({
    wagonNumber: "",
    wagonType: "",
    status: "",
    departureStation: "",
    destinationStation: "",
    cargoType: "",
    cargoVolume: "",
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setWagon((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    API.post("/wagons", wagon)
      .then((res) => {
        alert("✅ Вагон сәтті қосылды!");
        onAdd(); // тізімді жаңарту үшін
      })
      .catch((err) => {
        console.error("Қате:", err);
        alert("❌ Қосу кезінде қате шықты.");
      });
  };

  return (
    <form onSubmit={handleSubmit} className="form">
      <h3>➕ Вагон қосу</h3>
      <input type="text" name="wagonNumber" placeholder="Вагон нөмірі" onChange={handleChange} required />
      <input type="text" name="wagonType" placeholder="Вагон типі" onChange={handleChange} required />
      <input type="text" name="status" placeholder="Статус (тиеген/пустой)" onChange={handleChange} />
      <input type="text" name="departureStation" placeholder="Шығу станциясы" onChange={handleChange} />
      <input type="text" name="destinationStation" placeholder="Бару станциясы" onChange={handleChange} />
      <input type="text" name="cargoType" placeholder="Жүк түрі (бидай, арпа...)" onChange={handleChange} />
      <input type="number" step="0.1" name="cargoVolume" placeholder="Көлем (тонна)" onChange={handleChange} />
      <button type="submit">Қосу</button>
    </form>
  );
}
