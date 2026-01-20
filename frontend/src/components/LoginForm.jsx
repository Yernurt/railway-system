import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import API from "../api";
import { saveToken } from "../auth";

import "../components/LoginForm.css";

export default function LoginForm() {
  const [form, setForm] = useState({ username: "", password: "" });
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
    setError("");
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    try {
      const res = await API.post("/login", form);

      // ❗❗❗ Егер backend бірден string қайтарса:
      saveToken(res.data);

      // Егер backend { token: ... } деп қайтарса:
      // saveToken(res.data.token);

      // Жаңа: Рөлге байланысты маршрутқа бағыттау
      const role = localStorage.getItem("role");
      if (role === "ROLE_ADMIN") navigate("/admin");
      else if (role === "ROLE_DISPATCHER") navigate("/dispatch");
      else if (role === "ROLE_OPERATOR") navigate("/operator");
      else navigate("/dashboard"); // дефолт
      
    } catch (err) {
      setError("Қате логин немесе пароль!");
    }
  };

  return (
    <div className="login-page-bg">
      <div className="login-container">
        {/* Сол жақ: Логин */}
        <div className="login-left">
          <h2>LOGIN</h2>
          <form onSubmit={handleSubmit}>
            <label htmlFor="username">LOGIN</label>
            <input
              id="username"
              name="username"
              type="text"
              placeholder="YOUR LOGIN"
              value={form.username}
              onChange={handleChange}
              required
            />
            <label htmlFor="password">PASSWORD</label>
            <input
              id="password"
              name="password"
              type="password"
              placeholder="ENTER VALID PASSWORD"
              value={form.password}
              onChange={handleChange}
              required
            />
            {error && <div className="login-error">{error}</div>}
            <button className="login-btn" type="submit">LOGIN &gt;&gt;</button>
          </form>
        </div>
        {/* Оң жақ: Register call-to-action */}
        <div className="login-right">
          <h2>NOT A MEMBER?</h2>
          <p>
            Жаңа пайдаланушысыз ба? Жүйеге тіркелу арқылы барлық мүмкіндіктерді пайдаланыңыз.
          </p>
          <Link to="/register">
            <button className="register-btn">REGISTER NOW &gt;&gt;</button>
          </Link>
        </div>
      </div>
    </div>
  );
}
