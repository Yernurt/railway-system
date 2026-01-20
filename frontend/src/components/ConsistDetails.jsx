import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import API from "../api";

export default function ConsistDetails() {
  const { locomotiveNumber } = useParams();
  const navigate = useNavigate();
  const [consist, setConsist] = useState(null);

  useEffect(() => {
    fetchData();
  }, [locomotiveNumber]);

  const fetchData = async () => {
    try {
      const res = await API.get(`/consists/by-locomotive/${locomotiveNumber}`);
      setConsist(res.data);
    } catch (err) {
      console.error("❌ Составты жүктеу қатесі:", err);
    }
  };

  const handleRemoveWagon = async (wagonId) => {
    if (!window.confirm("Сенімдісің бе? Бұл вагон составтан шығарылады.")) return;

    try {
      const res = await API.delete(`/consists/remove-wagon/${wagonId}`);
      alert(res.data);
      const updated = await API.get(`/consists/by-locomotive/${locomotiveNumber}`);
      if (!updated.data.wagons || updated.data.wagons.length === 0) {
        alert("Составтағы соңғы вагон шығарылды. Состав өшірілді.");
        navigate("/consists");
      } else {
        setConsist(updated.data);
      }
    } catch (err) {
      alert("Қате: " + (err.response?.data || err.message));
    }
  };

  if (!consist) return <p>⏳ Жүктелуде...</p>;

  return (
    <div className="consist-page">
      <h2>🚆 Состав № {consist.locomotive?.locomotiveNumber || "—"}</h2>
      <p>🚉 Станция: {consist.station || "—"}</p>
      <p>🚀 Жылдамдық: {consist.locomotive?.speedKmhLocomotive || "—"} км/сағ</p>

      <h3>🎞️ Локомотив видеосы</h3>
      <div className="video-grid">
        {consist.locomotive?.videoLocomotive ? (
          [1, 2].map((i) => (
            <video key={i} src={consist.locomotive.videoLocomotive} controls width="45%" />
          ))
        ) : (
          <p>🎥 Видео қолжетімді емес.</p>
        )}
      </div>

      <h3>📦 Вагондар тізімі</h3>

      {Array.isArray(consist.wagons) && consist.wagons.length > 0 ? (
        <table className="custom-table">
          <thead>
            <tr>
              <th>№</th>
              <th>Нөмір</th>
              <th>Тип</th>
              <th>Статус</th>
              <th>Жылдамдық</th>
              <th>Әрекет</th>
            </tr>
          </thead>
          <tbody>
            {consist.wagons.map((wagon, index) => (
              <tr key={wagon.id}>
                <td>{index + 1}</td>
                <td>{wagon.wagonNumber}</td>
                <td>{wagon.wagonType}</td>
                <td>{wagon.status}</td>
                <td>{wagon.speedKmh} км/сағ</td>
                <td>
                  <button onClick={() => handleRemoveWagon(wagon.id)}>
                    ❌ Шығару
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : (
        <p>📭 Бұл составқа тіркелген вагондар жоқ.</p>
      )}
    </div>
  );
}
