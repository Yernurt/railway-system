import React from "react";
import { Link, useLocation } from "react-router-dom";
import "./AdminSidebar.css";

export default function AdminSidebar() {
  const location = useLocation();
  const isActive = (path) => location.pathname === path;

  return (
    <div className="admin-sidebar">
      <div className="sidebar-header">
        <h2>FAB Admin</h2>
      </div>
      <ul className="sidebar-menu">
        <li className={isActive("/admin") ? "active" : ""}>
          <Link to="/admin">📊 Басты бет</Link>
        </li>
        <li className={isActive("/admin/users") ? "active" : ""}>
          <Link to="/admin/users">👥 Пайдаланушылар</Link>
        </li>
        <li className={isActive("/admin/wagons") ? "active" : ""}>
          <Link to="/admin/wagons">🚂 Вагондар</Link>
        </li>
        <li className={isActive("/admin/settings") ? "active" : ""}>
          <Link to="/admin/settings">⚙️ Параметрлер</Link>
        </li>
      </ul>
    </div>
  );
}
