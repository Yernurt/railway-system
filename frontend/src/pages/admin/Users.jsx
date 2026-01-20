import { useEffect, useState } from "react";
import API from "../../api";
import "../../style/AdminDashboard.css";
import React from "react";
import { Link } from "react-router-dom"; // <-- Бұл жол керек

export default function Users() {
  
  const [users, setUsers] = useState([]);
  const [search, setSearch] = useState("");
  const [newUser, setNewUser] = useState({
    username: "",
    password: "",
    role: "ROLE_VIEWER",
    name: "",
    sname: "",
    phone: "",
    station: "", // ✅ станция қосылды
  });

  const fetchUsers = async () => {
    try {
      const res = await API.get("/users");
      setUsers(res.data);
    } catch (err) {
      alert("Қате: Пайдаланушыларды жүктеу сәтсіз");
    }
  };

  const handleSearch = async () => {
    if (!search) return fetchUsers();
    try {
      const res = await API.get(`/users/search?username=${search}`);
      setUsers(res.data);
    } catch (err) {
      alert("Қате: Іздеу орындалмады");
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Жоюға сенімдісіз бе?")) return;
    try {
      await API.delete(`/users/${id}`);
      fetchUsers();
    } catch (err) {
      alert("Қате: Жою мүмкін болмады");
    }
  };

  const handleAddUser = async () => {
    try {
      await API.post("/users/add", newUser);
      alert("✅ Жаңа пайдаланушы қосылды");
      setNewUser({
        username: "",
        password: "",
        role: "ROLE_VIEWER",
        name: "",
        sname: "",
        phone: "",
        station: "", // ✅ тазалау
      });
      fetchUsers();
    } catch (err) {
      alert("❌ Қате: Жаңа пайдаланушыны қосу мүмкін болмады");
    }
  };

  const handleRoleChange = async (id, role) => {
    try {
      await API.patch(`/users/${id}/role`, role, {
        headers: { "Content-Type": "application/json" },
      });
      fetchUsers();
    } catch (err) {
      alert("❌ Рөлді жаңарту қатесі");
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  return (
    <div className="admin-users">
      <h2>👤 Пайдаланушылар</h2>

      {/* Уведомление батырмасы */}
     
      <div className="search-add-box">
        <input
          type="text"
          placeholder="Іздеу..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
        <button onClick={handleSearch}>🔎 Іздеу</button>
      </div>
      <Link to="/admin/pending">📥 Өтініштер</Link>


      {/* 👇 Кесте */}
      <table className="user-table">
        <thead>
          <tr>
            <th>Логин</th>
            <th>Аты</th>
            <th>Тегі</th>
            <th>Телефон</th>
            <th>Станция</th> {/* ✅ қосылды */}
            <th>Рөлі</th>
            <th>Әрекеттер</th>
          </tr>
        </thead>
        <tbody>
          {users.map((user) => (
            <tr key={user.id}>
              <td>{user.username}</td>
              <td>{user.name}</td>
              <td>{user.sname}</td>
              <td>{user.phone}</td>
              <td>{user.station}</td> {/* ✅ көрсетеді */}
              <td>
                <select
                  value={user.role}
                  onChange={(e) => handleRoleChange(user.id, e.target.value)}
                >
                  <option value="ROLE_ADMIN">Админ</option>
                  <option value="ROLE_DISPATCHER">Диспетчер</option>
                  <option value="ROLE_OPERATOR">Оператор</option>
                  <option value="ROLE_VIEWER">Көрермен</option>
                </select>
              </td>
              <td>
                <button onClick={() => handleDelete(user.id)}>🗑️</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {/* 👇 Жаңа пайдаланушы қосу */}
      <h3>➕ Жаңа пайдаланушы қосу</h3>
      <div className="new-user-form">
        <input
          placeholder="Логин"
          value={newUser.username}
          onChange={(e) =>
            setNewUser({ ...newUser, username: e.target.value })
          }
        />
        <input
          placeholder="Құпиясөз"
          type="password"
          value={newUser.password}
          onChange={(e) =>
            setNewUser({ ...newUser, password: e.target.value })
          }
        />
        <input
          placeholder="Аты"
          value={newUser.name}
          onChange={(e) => setNewUser({ ...newUser, name: e.target.value })}
        />
        <input
          placeholder="Тегі"
          value={newUser.sname}
          onChange={(e) => setNewUser({ ...newUser, sname: e.target.value })}
        />
        <input
          placeholder="Телефон"
          value={newUser.phone}
          onChange={(e) => setNewUser({ ...newUser, phone: e.target.value })}
        />
        <input
          placeholder="Станция"
          value={newUser.station}
          onChange={(e) =>
            setNewUser({ ...newUser, station: e.target.value })
          }
        />
        <select
          value={newUser.role}
          onChange={(e) => setNewUser({ ...newUser, role: e.target.value })}
        >
          <option value="ROLE_ADMIN">Админ</option>
          <option value="ROLE_DISPATCHER">Диспетчер</option>
          <option value="ROLE_OPERATOR">Оператор</option>
          <option value="ROLE_VIEWER">Көрермен</option>
        </select>
        <button onClick={handleAddUser}>✅ Қосу</button>
      </div>
    </div>
  );
}
