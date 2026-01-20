import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import LoginForm from "./components/LoginForm";
import RegisterForm from "./components/RegisterForm";
import ProtectedRoute from "./components/ProtectedRoute";
import Dashboard from "./pages/Dashboard";
import RoutesPage from "./pages/RoutesPage";
import DispatcherPanel from "./pages/DispatcherPanel";
import OperatorPanel from "./pages/OperatorPanel";
import Unauthorized from "./pages/Unauthorized";
import WagonAddFormPage from "./pages/WagonAddFormPage";
import AdminLayout from "./pages/admin/AdminLayout";
import AdminDashboard from "./pages/admin/AdminDashboard";
import Users from "./pages/admin/Users";
import Wagons from "./pages/admin/Wagons";
import Settings from "./pages/admin/Settings";
import ConsistDetails from "./components/ConsistDetails"; 
import LocomotiveAddFormPage from "./pages/LocomotiveAddFormPage";
import ConsistCreatePage from "./pages/CreateConsistPage";
import ConsistListPage from "./pages/ConsistListPage"
import HomePage from "./pages/HomePage";
import MainLayout from "./components/MainLayout";
import "./styles.css";
import PendingRequests from "./pages/admin/PendingRequests";

export default function App() {
  return (
 <Router>
  <Routes>
    {/* 🔓 Жалпы Layout – барлығына ортақ, login/register де ішінде */}
    <Route element={<MainLayout />}>
      <Route path="/" element={<HomePage />} />
      <Route path="/login" element={<LoginForm />} />
      <Route path="/register" element={<RegisterForm />} />
      <Route path="/unauthorized" element={<Unauthorized />} />

      {/* ✅ Корпоратив беттер */}
      <Route
        path="/dashboard"
        element={
          <ProtectedRoute roles={["ROLE_ADMIN", "ROLE_OPERATOR", "ROLE_DISPATCHER"]}>
            <Dashboard />
          </ProtectedRoute>
        }
      />
      <Route
        path="/routes"
        element={
          <ProtectedRoute roles={["ROLE_ADMIN"]}>
            <RoutesPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/dispatch"
        element={
          <ProtectedRoute roles={["ROLE_DISPATCHER", "ROLE_ADMIN", "ROLE_OPERATOR"]}>
            <DispatcherPanel />
          </ProtectedRoute>
        }
      />
      <Route
        path="/operator"
        element={
          <ProtectedRoute roles={["ROLE_OPERATOR", "ROLE_ADMIN"]}>
            <OperatorPanel />
          </ProtectedRoute>
        }
      />
      <Route
        path="/wagons/add"
        element={
          <ProtectedRoute roles={["ROLE_DISPATCHER", "ROLE_ADMIN"]}>
            <WagonAddFormPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/locomotives/add"
        element={
          <ProtectedRoute roles={["ROLE_DISPATCHER", "ROLE_ADMIN"]}>
            <LocomotiveAddFormPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/consist/create"
        element={
          <ProtectedRoute roles={["ROLE_ADMIN", "ROLE_DISPATCHER"]}>
            <ConsistCreatePage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/consists"
        element={
          <ProtectedRoute roles={["ROLE_ADMIN", "ROLE_DISPATCHER"]}>
            <ConsistListPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/consists/:locomotiveNumber"
        element={
          <ProtectedRoute roles={["ROLE_OPERATOR", "ROLE_ADMIN", "ROLE_DISPATCHER"]}>
            <ConsistDetails />
          </ProtectedRoute>
        }
      />
      
    </Route>

    {/* 🔒 Admin Layout тек Admin үшін жеке */}
    <Route
      path="/admin"
      element={
        <ProtectedRoute roles={["ROLE_ADMIN"]}>
          <AdminLayout />
        </ProtectedRoute>
      }
    >
      <Route index element={<AdminDashboard />} />
      <Route path="users" element={<Users />} />
      <Route path="wagons" element={<Wagons />} />
      <Route path="pending" element={<PendingRequests />} />
      <Route path="settings" element={<Settings />} />
    </Route>
  </Routes>
</Router>

  );
}
