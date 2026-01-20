// src/pages/HomePage.jsx
import React from "react";
import "./HomePage.css"; // Стильді бөлек жаса

export default function HomePage() {
  return (
    <div className="homepage-main">
      <img
        src={require("../assets/main_bg.png")} // немесе "/main_bg.png"
        alt="Қазақстан теміржолы"
        className="homepage-banner"
      />
      <div className="homepage-overlay">
        <h1>
          ҚАЗАҚСТАННЫҢ ТЕМІРЖОЛ ЖЕЛІЛЕРІНДЕ<br />
          <span className="accent">АСТЫҚ ВАГОНДАРЫ ЛОГИСТИКАСЫНЫҢ</span><br />
          ЗИЯТКЕРЛІК АҚПАРАТТЫҚ ЖҮЙЕСІН ӘЗІРЛЕУ
        </h1>
        <div className="author">Орындаған: Таубай Ернұр</div>
      </div>
    </div>
  );
}
