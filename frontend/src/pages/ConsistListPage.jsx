import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import API from "../api";

export default function ConsistListPage() {
  const [consists, setConsists] = useState([]);

  useEffect(() => {
    const fetchConsists = async () => {
      try {
        const res = await API.get("/consists");
        setConsists(res.data || []);
      } catch (err) {
        console.error("❌ Составтарды жүктеу қатесі:", err);
      }
    };
    fetchConsists();
  }, []);

  const handleDeleteConsist = async (id) => {
    if (!window.confirm("Составты өшіргіңіз келеді ме? Бұл қайтымсыз!")) return;
    try {
      await API.delete(`/consists/${id}`);
      setConsists((prev) => prev.filter((c) => c.id !== id));
    } catch (err) {
      alert("Составты өшіру қатесі: " + err.message);
    }
  };

  return (
    <div className="panel">
      <h2>🚆 Барлық составтар</h2>
      {consists.length === 0 ? (
        <p>⏳ Жүктелуде немесе составтар жоқ...</p>
      ) : (
        <table className="custom-table">
          <thead>
            <tr>
              <th>#</th>
              <th>Состав нөмірі</th>
              <th>Локомотив</th>
              <th>Станция</th>
              <th>Статус</th>
              <th>Вагон саны</th>
              <th>Толығырақ</th>
            </tr>
          </thead>
          <tbody>
            {consists.map((c, index) => (
              <tr key={c.id}>
                <td>{index + 1}</td>
                <td>{c.consistNumber}</td>
                <td>{c.locomotive ? c.locomotive.locomotiveNumber : "—"}</td>
                <td>{c.station || "—"}</td>
                <td>{c.status || "—"}</td>
                <td>{c.wagons?.length || 0}</td>
                <td>
                  {c.locomotive ? (
                    <>
                      <Link to={`/consists/${c.locomotive.locomotiveNumber}`}>
                        <button>👁️ Көру</button>
                      </Link>
                      &nbsp;
                      <button
                        style={{
                          background: "#e74c3c",
                          color: "#fff",
                          border: "none",
                          borderRadius: "4px",
                          padding: "4px 10px",
                          marginLeft: "4px",
                          cursor: "pointer",
                        }}
                        onClick={() => handleDeleteConsist(c.id)}
                      >
                        🗑️ Жою
                      </button>
                    </>
                  ) : (
                    "—"
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
