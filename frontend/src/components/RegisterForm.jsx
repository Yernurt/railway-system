import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import API from "../api";
import "../components/RegisterForm.css";

export default function RegisterForm() {
  const [form, setForm] = useState({
    username: "",      // 👈 Бұл email болады
    password: "",
    name: "",
    sname: "",
    phone: "",
    station: "",
    roleRequest: "ROLE_OPERATOR"
  });

  const [error, setError] = useState("");
  const navigate = useNavigate();

  const roles = [
    { value: "ROLE_OPERATOR", label: "Оператор" },
    { value: "ROLE_DISPATCHER", label: "Диспетчер" }
  ];

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
    setError("");
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    try {
      await API.post("/register", form);
      alert("✅ Өтініш сәтті жіберілді. Админ бекіткеннен кейін жүйеге кіре аласыз.");
      navigate("/login");
    } catch (err) {
      console.error(err);
      setError("❌ Тіркелу кезінде қате шықты.");
    }
  };

  return (
    <div className="login-page-bg">
      <div className="login-container">
        <div className="login-left">
          <h2>REGISTER</h2>
          <form onSubmit={handleSubmit}>
            <label>EMAIL (логин)</label>
            <input
              type="email"
              name="username"
              placeholder="example@email.com"
              value={form.username}
              onChange={handleChange}
              required
            />

            <label>PASSWORD</label>
            <input
              type="password"
              name="password"
              placeholder="Құпиясөз"
              value={form.password}
              onChange={handleChange}
              required
            />

            <label>АТЫ</label>
            <input
              type="text"
              name="name"
              placeholder="Аты"
              value={form.name}
              onChange={handleChange}
              required
            />

            <label>ТЕГІ</label>
            <input
              type="text"
              name="sname"
              placeholder="Тегі"
              value={form.sname}
              onChange={handleChange}
              required
            />

            <label>ТЕЛЕФОН</label>
            <input
              type="text"
              name="phone"
              placeholder="8707XXXXXXX"
              value={form.phone}
              onChange={handleChange}
              required
            />

            <label>СТАНЦИЯ (Қала)</label>
            <input
              type="text"
              name="station"
              placeholder="Астана"
              value={form.station}
              onChange={handleChange}
              required
            />

            <label>РӨЛ СҰРАУ:</label>
            <select
              name="roleRequest"
              value={form.roleRequest}
              onChange={handleChange}
              required
            >
              {roles.map((r) => (
                <option key={r.value} value={r.value}>
                  {r.label}
                </option>
              ))}
            </select>

            {error && <div className="login-error">{error}</div>}

            <button className="login-btn" type="submit">
              REGISTER &gt;&gt;
            </button>
          </form>
        </div>

        <div className="login-right">
          <h2>ALREADY MEMBER?</h2>
          <p>Жүйеге тіркелгенсіз бе? Аккаунтыңызға кіріңіз.</p>
          <Link to="/login">
            <button className="register-btn">LOGIN &gt;&gt;</button>
          </Link>
        </div>
      </div>
    </div>
  );
}
