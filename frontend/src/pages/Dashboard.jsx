import { logout, getRole } from "../auth";
import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";

export default function Dashboard() {
  const navigate = useNavigate();
  const role = getRole(); // 👈 Рөлді аламыз

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <div>
      <Navbar />
      <div style={{ padding: "20px" }}>
        <h1>Басты бет (Dashboard)</h1>
        <p>Сіздің рөліңіз: <strong>{role}</strong></p>

        {/* Рөлге байланысты панельдер */}
        {role === "ROLE_ADMIN" && (
          <div>
            <h3>👑 Админ панелі</h3>
            <p>Сіз барлық жүйені басқаруға толық құқыққа иесіз.</p>
          </div>
        )}

        {role === "ROLE_DISPATCHER" && (
          <div>
            <h3>🚂 Диспетчер панелі</h3>
            <p>Маршруттар мен пойыз қозғалысын бақылау мүмкіндігі бар.</p>
          </div>
        )}

        {role === "ROLE_OPERATOR" && (
          <div>
            <h3>🛠 Оператор панелі</h3>
            <p>Вагон статустарын өңдеуге рұқсатыңыз бар.</p>
          </div>
        )}

        {role === "ROLE_VIEWER" && (
          <div>
            <h3>👁 Қараушы панелі</h3>
            <p>Тек көру режимі. Өзгертуге рұқсат жоқ.</p>
          </div>
        )}
      </div>
    </div>
  );
}
