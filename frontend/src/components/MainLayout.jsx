import AdminSidebar from "./AdminSidebar";
import AdminNavbar from "./AdminNavbar";
import { getRole } from "../auth"; 
import { Outlet, useLocation } from "react-router-dom";

export default function MainLayout() {
  const location = useLocation();
  const hideLayoutPaths = ["/login", "/register", "/unauthorized"];
  const role = getRole();
  if (hideLayoutPaths.includes(location.pathname)) {
    return <Outlet />;
  }

  return (
    <div className="admin-container">
         {role === "ROLE_ADMIN" && <AdminSidebar />}
      <div className="admin-main">
        <AdminNavbar />
        <div className="admin-content">
          <Outlet />
        </div>
      </div>
    </div>
  );
}
