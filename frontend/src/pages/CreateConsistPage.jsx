import { useState, useEffect } from "react";
import API from "../api";

export default function CreateConsistPage() {
  const [wagons, setWagons] = useState([]);
  const [locomotives, setLocomotives] = useState([]);
  const [selectedWagonIds, setSelectedWagonIds] = useState([]);
  const [selectedLocomotive, setSelectedLocomotive] = useState(null);
  const [filters, setFilters] = useState({
    wagonType: "",
    totalVolume: "", // ✅ нақты көлем
    locomotiveType: "",
  });

  const station = localStorage.getItem("station");

 const fetchWagons = async () => {
  if (!filters.wagonType || !filters.totalVolume) return;

  const token = localStorage.getItem("token");
  const query = `http://localhost:8090/wagons/auto-select?type=${filters.wagonType}&volume=${filters.totalVolume}&station=${station}`;

  console.log("📤 /wagons/auto-select сұранысы жіберілуде");
  console.log("🔐 Token:", token?.slice(0, 30) + "...");

  try {
    const res = await fetch(query, {
      method: "GET",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json"
      }
    });

    if (res.status === 403) {
      console.error("🚫 403 – Рұқсат жоқ (ROLE_DISPATCHER жетіспейді)");
      throw new Error("403 – Forbidden");
    }

    if (!res.ok) {
      throw new Error("Сервер қатесі");
    }

    const data = await res.json();
    setWagons(data);
    setSelectedWagonIds(data.map((w) => w.id));
  } catch (err) {
    console.error("❌ Вагондарды жүктеу қатесі:", err);
  }
};


  const fetchLocomotives = async () => {
    try {
      const query = `/locomotives?type=${filters.locomotiveType}&station=${station}`;
      const res = await API.get(query);
      setLocomotives(res.data);
    } catch (err) {
      console.error("Локомотивтерді жүктеу қатесі:", err);
    }
  };

  const handleSubmit = async () => {
    if (!selectedLocomotive || selectedWagonIds.length === 0) {
      alert("Локомотив пен кемінде бір вагон таңдаңыз!");
      return;
    }

    const payload = {
      locomotiveNumber: selectedLocomotive.locomotiveNumber,
      wagonIds: selectedWagonIds,
    };

    try {
      await API.post("/consists", payload);
      alert("✅ Состав сәтті құрылды!");
    } catch (err) {
      alert("❌ Қате: Составты құру сәтсіз");
      console.error(err);
    }
  };

  useEffect(() => {
    fetchWagons();
    fetchLocomotives();
  }, [filters]);

  return (
    <div className="panel">
      <h2>🚂 Состав құру</h2>

      <div className="filter-form">
        <input
          placeholder="Вагон типі"
          value={filters.wagonType}
          onChange={(e) => setFilters({ ...filters, wagonType: e.target.value })}
        />
        <input
          placeholder="Қажетті жалпы көлем (тонна)"
          type="number"
          value={filters.totalVolume}
          onChange={(e) => setFilters({ ...filters, totalVolume: e.target.value })}
        />
        <select
          value={filters.locomotiveType}
          onChange={(e) => setFilters({ ...filters, locomotiveType: e.target.value })}
        >
          <option value="">-- Локомотив типі --</option>
          <option value="дизель">Дизель</option>
          <option value="электровоз">Электровоз</option>
        </select>
      </div>

      <h3>🧲 Локомотив таңдау</h3>
      <ul>
        {locomotives.map((loc) => (
          <li key={loc.id}>
            <input
              type="radio"
              name="locomotive"
              onChange={() => setSelectedLocomotive(loc)}
            />
            {loc.locomotiveNumber} - {loc.locomotiveType}
          </li>
        ))}
      </ul>

      <h3>🚃 Таңдалған вагондар</h3>
      <ul>
        {wagons.map((w) => (
          <li key={w.id}>
            ✅ {w.wagonNumber} - {w.wagonType} ({w.cargoVolume} т)
          </li>
        ))}
      </ul>

      <button onClick={handleSubmit}>✅ Состав құру</button>
    </div>
  );
}
