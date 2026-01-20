import { jwtDecode } from "jwt-decode";


export function saveToken(token) {
  localStorage.setItem("token", token);
  const decoded = jwtDecode(token);
  console.log(decoded);
  localStorage.setItem("role", decoded.role); // 🔥 рөлді сақтау
  localStorage.setItem("username", decoded.sub); // 🔹 қолданушы аты
  localStorage.setItem("station", decoded.station); 
}


export function getRole() {
  return localStorage.getItem("role");
}

  export function getToken() {
    return localStorage.getItem("token");
  }
  
export function logout() {
  localStorage.removeItem("token");
  localStorage.removeItem("role");
  localStorage.removeItem("username");
  localStorage.removeItem("station");
}

  export function isAuthenticated() {
    return !!localStorage.getItem("token");
  }
  