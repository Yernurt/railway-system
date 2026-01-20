// src/pages/WagonAddFormPage.jsx
import WagonAddForm from "../components/WagonAddForm";
import { useNavigate } from "react-router-dom";

export default function WagonAddFormPage() {
  const navigate = useNavigate();

  const handleAddSuccess = () => {
    navigate("/dispatch"); // Қосу сәтті болған соң диспетчер панеліне қайта оралу
  };

  return (
    <div className="page">
      <h2>➕ Жаңа вагон қосу беті</h2>
      <WagonAddForm onAdd={handleAddSuccess} />
    </div>
  );
}
