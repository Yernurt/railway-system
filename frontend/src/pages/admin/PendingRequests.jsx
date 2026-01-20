import React, { useEffect, useState } from "react";
import API from "../../api"; // ✅ өз API instance
import "../../styles.css"; // Қалауыңа қарай стиль қос
import { Link } from "react-router-dom"; // <-- Бұл жол керек

export default function PendingRequests() {
  const [pendingUsers, setPendingUsers] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchPendingUsers = async () => {
    try {
      const res = await API.get("/admin/pending-users");
      setPendingUsers(res.data);
    } catch (err) {
      console.error("Қате:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPendingUsers();
  }, []);

  const handleApprove = async (id) => {
    try {
      await API.post(`/admin/approve-user/${id}`);
      fetchPendingUsers(); // Қайта жүктеу
    } catch (err) {
      console.error("Бекіту қатесі:", err);
    }
  };

  const handleReject = async (id) => {
    try {
      await API.post(`/admin/reject-user/${id}`);
      fetchPendingUsers(); // Қайта жүктеу
    } catch (err) {
      console.error("Қабылдамау қатесі:", err);
    }
  };

  if (loading) return <p>Жүктелуде...</p>;

  return (
    <div>
      <h2>📥 Бекітілмеген қолданушылар</h2>
      {pendingUsers.length === 0 ? (
        <p>Барлық өтініштер өңделген.</p>
      ) : (
        <table className="custom-table">
          <thead>
            <tr>
              <th>№</th>
              <th>Аты</th>
              <th>Қолданушы аты</th>
              <th>Қала (Станция)</th> 
              <th>Сұралған рөл</th>
              <th>Іс-әрекет</th>
            </tr>
          </thead>
          <tbody>
            {pendingUsers.map((user, index) => (
              <tr key={user.id}>
                <td>{index + 1}</td>
                <td>{user.name}</td>
                <td>{user.username}</td>
                <td>{user.station}</td>
                <td>{user.roleRequest?.replace("ROLE_", "")}</td>
                <td>
                  <button onClick={() => handleApprove(user.id)}>✅ Бекіту</button>{" "}
                  <button onClick={() => handleReject(user.id)}>❌ Бас тарту</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
