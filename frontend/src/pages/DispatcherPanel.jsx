import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import API from "../api";
import WagonEditForm from "../components/WagonEditForm";
import "../styles.css";

export default function DispatcherPanel() {
  const [wagons, setWagons] = useState([]);
  const [selectedWagon, setSelectedWagon] = useState(null);
  const [filters, setFilters] = useState({
    wagonType: "",
    status: "",
    departureStation: "",
    destinationStation: "",
  });

  const station = localStorage.getItem("station"); // ✅ юзер станциясы

  const fetchWagons = () => {
    let query = `/wagons/filter?`;
  
    // Қолмен енгізілген фильтрлер
    Object.entries(filters).forEach(([key, value]) => {
      if (value) query += `${key}=${encodeURIComponent(value)}&`;
    });
  
    // ✅ автоматты түрде өз станциясын қосу
    if (station) {
      query += `station=${encodeURIComponent(station)}&`;
    }
  
    API.get(query)
      .then((res) => setWagons(res.data))
      .catch((err) => console.error("Қате:", err));
  };
  

  useEffect(() => {
    fetchWagons();
  }, []);

  const handleEdit = (wagon) => {
    setSelectedWagon(wagon);
  };

  const closeEditForm = () => {
    setSelectedWagon(null);
  };

  const handleFilterChange = (e) => {
    setFilters({ ...filters, [e.target.name]: e.target.value });
  };

  const handleFilterSubmit = (e) => {
    e.preventDefault();
    fetchWagons();
  };

  const handleClearFilters = () => {
    setFilters({
      wagonType: "",
      status: "",
      departureStation: "",
      destinationStation: "",
    });
    fetchWagons(); // Барлығын қайта жүктеу
  };

  return (
    <div className="panel">
      <h2>🚆 Вагондар тізімі (Диспетчер)</h2>

      {/* 🔍 Сүзгілеу формасы */}
      <form onSubmit={handleFilterSubmit} className="filter-form">
        <input
          name="wagonType"
          placeholder="Типі"
          value={filters.wagonType}
          onChange={handleFilterChange}
        />
        <input
          name="status"
          placeholder="Статус"
          value={filters.status}
          onChange={handleFilterChange}
        />
        <input
          name="departureStation"
          placeholder="Бастапқы станция"
          value={filters.departureStation}
          onChange={handleFilterChange}
        />
        <input
          name="destinationStation"
          placeholder="Мақсатты станция"
          value={filters.destinationStation}
          onChange={handleFilterChange}
        />
        <button type="submit">🔎 Сүзу</button>
        <button type="button" onClick={handleClearFilters}>🧹 Тазарту</button>
      </form>

      {/* ➕ Вагон қосу батырмасы */}
      <div style={{ margin: "1rem 0" }}>
        <Link to="/wagons/add">
          <button>➕ Жаңа вагон қосу</button>
        </Link>
      </div>
      {/* 👇 Локомотив қосу батырмасы */}
      <div style={{ marginBottom: "1rem" }}>
        <Link to="/locomotives/add">
          <button>🚂 Жаңа локомотив қосу</button>
        </Link>
      </div>
      {/* 🚆 Состав құру батырмасы */}
      <div style={{ marginBottom: "1rem" }}>
  <Link to="/consist/create">
    <button>🚆 Состав құру</button>
  </Link>
</div>
<div style={{ marginBottom: "2rem" }}>
  <Link to="/consists">
    <button>📋 Составтарды көру</button>
  </Link>
</div>

      
      {/* 📋 Вагондар кестесі */}
      <table className="custom-table">
        <thead>
          <tr>
            <th>№</th>
            <th>Нөмір</th>
            <th>Тип</th>
            <th>Статус</th>
            <th>Бастапқы станция</th>
            <th>Мақсатты станция</th>
            <th>Ағымдағы станция</th>
            <th>Жүк</th>
            <th>Көлем</th>
            <th>Скан статус</th>
            <th>Жылдамдық</th>
            <th>Жаңартылған</th>
            <th>Өңдеу</th>
          </tr>
        </thead>
        <tbody>
          {wagons.map((wagon, index) => (
            <tr key={wagon.id}>
              <td>{index + 1}</td>
              <td>{wagon.wagonNumber}</td>
              <td>{wagon.wagonType}</td>
              <td>{wagon.status}</td>
              <td>{wagon.departureStation}</td>
              <td>{wagon.destinationStation}</td>
              <td>{wagon.station}</td>
              <td>{wagon.cargoType}</td>
              <td>{wagon.cargoVolume}</td>
              <td>{wagon.identificationStatus}</td>
              <td>{wagon.speedKmh}</td>
              <td>{new Date(wagon.lastUpdated).toLocaleString()}</td>
              <td>
                <button onClick={() => handleEdit(wagon)}>Өңдеу</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {/* 🛠 Өңдеу формасы */}
      {selectedWagon && (
        <WagonEditForm
          wagon={selectedWagon}
          onClose={closeEditForm}
          onUpdated={fetchWagons}
        />
      )}
    </div>
  );
}
