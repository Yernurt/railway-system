import { useState } from "react";
import API from "../api";

export default function WagonEditForm({ wagon, onClose, onUpdated }) {
  const [formData, setFormData] = useState(wagon);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await API.put(`/wagons/${wagon.wagonNumber}`, formData);
      onUpdated(); // Жаңарту
      onClose();   // Форма жабу
    } catch (err) {
      console.error("Өңдеу қатесі:", err);
    }
  };

  return (
    <div className="modal">
      <h3>✏️ Вагонды өңдеу</h3>
      <form onSubmit={handleSubmit} className="form">
        <input
          name="wagonType"
          value={formData.wagonType}
          onChange={handleChange}
          placeholder="Тип"
        />
        <input
          name="status"
          value={formData.status}
          onChange={handleChange}
          placeholder="Статус"
        />
        <input
          name="destinationStation"
          value={formData.destinationStation}
          onChange={handleChange}
          placeholder="Мақсатты станция"
        />
          <input
          name="cargoType"
          value={formData.cargoType}
          onChange={handleChange}
          placeholder="Жүк"
        />
        <button type="submit">Сақтау</button>
        <button type="button" onClick={onClose}>Бас тарту</button>
      </form>
    </div>
  );
}
