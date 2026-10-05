import { X } from "lucide-react";
import {
  NavLink,
  useLocation,
  useNavigate,
} from "react-router-dom";

import { navigationItems } from "../../config/navigation";

import Brand from "./Brand";
import UserProfile from "./UserProfile";

const currentUser = {
  id: 1,
  name: "Celia Díaz",
  role: "Administrador",
  photo: null,
};

function Sidebar({
  isOpen,
  onClose,
}) {
  const navigate = useNavigate();
  const location = useLocation();

  const currentUserPath = `/usuarios/${currentUser.id}`;

  const isCurrentUserProfile =
    location.pathname === currentUserPath;

  const handleProfileClick = () => {
    onClose();
    navigate(currentUserPath);
  };

  const handleLogout = () => {
    onClose();

    navigate("/login", {
      replace: true,
    });
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
    </aside>
  );
}

export default Sidebar;