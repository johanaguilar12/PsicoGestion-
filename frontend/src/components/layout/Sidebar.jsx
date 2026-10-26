import { useState } from "react";
import { X } from "lucide-react";
import {
  NavLink,
  useLocation,
  useNavigate,
} from "react-router-dom";

import { navigationItems } from "../../config/navigation";
import { useAuth } from "../auth/authContext.js";

import Brand from "./Brand";
import UserProfile from "./UserProfile";

function Sidebar({
  isOpen,
  onClose,
}) {
  const navigate = useNavigate();
  const location = useLocation();
  const { user, logout } = useAuth();
  const [logoutError, setLogoutError] = useState("");

  const currentUserPath = `/usuarios/${user.id}`;

  const isCurrentUserProfile =
    location.pathname === currentUserPath;

  const handleProfileClick = () => {
    onClose();
    navigate(currentUserPath);
  };

  const handleLogout = async () => {
    setLogoutError("");
    try {
      await logout();
      onClose();
    } catch {
      setLogoutError("No se pudo cerrar la sesión. Inténtalo de nuevo.");
    }
  };

  const currentUser = {
    id: user.id,
    name: user.nombreCompleto,
    role: user.rol ?? "Usuario",
    photo: user.fotoPerfil,
  };

  return (
    <aside
      id="main-navigation"
      className={`sidebar ${
        isOpen ? "sidebar--open" : ""
      }`}
      aria-label="Navegación principal"
    >
      <div className="sidebar__top">
        <Brand />

        <button
          type="button"
          className="sidebar__close"
          onClick={onClose}
          aria-label="Cerrar menú de navegación"
        >
          <X
            size={41}
            strokeWidth={2}
            aria-hidden="true"
          />
        </button>
      </div>

      <nav
        className="sidebar__navigation"
        aria-label="Módulos de PsicoGestión"
      >
        <ul className="sidebar__list">
          {navigationItems.map((item) => {
            const Icon = item.icon;

            return (
              <li key={item.path}>
                <NavLink
                  to={item.path}
                  end={item.path === "/"}
                  onClick={onClose}
                  className={({ isActive }) =>
                    [
                      "sidebar__link",
                      isActive
                        ? "sidebar__link--active"
                        : "",
                    ]
                      .filter(Boolean)
                      .join(" ")
                  }
                >
                  <Icon
                    className="sidebar__icon"
                    strokeWidth={1.8}
                    aria-hidden="true"
                  />

                  <span>{item.label}</span>
                </NavLink>
              </li>
            );
          })}
        </ul>
      </nav>

      <UserProfile
        user={currentUser}
        isActive={isCurrentUserProfile}
        onProfileClick={handleProfileClick}
        onLogout={handleLogout}
      />
      {logoutError && <p role="alert" className="sidebar__error">{logoutError}</p>}
    </aside>
  );
}

export default Sidebar;