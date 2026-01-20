import React from "react";
import { Link, useNavigate } from "react-router-dom";
import { logout } from "../auth";
import "./AdminNavbar.css";

export default function AdminNavbar() {
  const username = localStorage.getItem("username") || "";
  const role = localStorage.getItem("role") || "";
  const station = localStorage.getItem("station") || "";
  const navigate = useNavigate();

  function formatRole(role) {
    switch (role) {
      case "ROLE_ADMIN": return "Админ";
      case "ROLE_DISPATCHER": return "Диспетчер";
      case "ROLE_OPERATOR": return "Оператор";
      default: return "";
    }
  }

  const isGuest = !role || formatRole(role) === "";

  return (
    <div className="admin-navbar">
      <div className="navbar-left">
        <span className="navbar-title">
          {isGuest ? "Басты бет" : `${formatRole(role)} Панелі`}
        </span>
      </div>
      <div className="navbar-right">
        {!isGuest ? (
          <>
            <span className="user-info">
              👤 {username || "—"} | <b>{formatRole(role)}</b> | 🏢 {station || "—"}
            </span>
            <button
              className="logout-btn"
              onClick={() => {
                logout();
                navigate("/login");
              }}
              style={{ marginLeft: 16 }}
            >
              Шығу
            </button>
          </>
        ) : (
          <>
            <span className="user-info">Қош келдіңіз!</span>
            <Link to="/login">
              <button className="login-btn" style={{ marginLeft: 16 }}>Кіру</button>
            </Link>
          </>
        )}
      </div>
    </div>
  );
}
