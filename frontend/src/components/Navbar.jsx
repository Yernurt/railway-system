import { Link } from "react-router-dom";
import "../style/Navbar.css"; 

export default function Navbar() {
  return (
    <nav className="navbar">
      <div className="navbar-brand">Zernovoz</div>
      <ul className="navbar-menu">
        <li><Link to="/dashboard">Басты бет</Link></li>
        <li><Link to="/routes">Маршруттар</Link></li>
        <li><Link to="/users">Пайдаланушылар</Link></li>
        <li><Link to="/logout">Шығу</Link></li>
        
      </ul>
    </nav>
  );
}
