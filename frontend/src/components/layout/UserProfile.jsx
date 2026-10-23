import {
  ChevronRight,
  CircleUserRound,
  LogOut,
} from "lucide-react";

function UserProfile({
  user,
  isActive = false,
  onProfileClick,
  onLogout,
}) {
  return (
    <div className="sidebar-user">
      <button
        type="button"
        className={[
          "sidebar-user__profile",
          isActive
            ? "sidebar-user__profile--active"
            : "",
        ]
          .filter(Boolean)
          .join(" ")}
        onClick={onProfileClick}
        aria-label={`Ver información de ${user.name}`}
        aria-current={isActive ? "page" : undefined}
      >
        <span className="sidebar-user__avatar-container">
          {user.photo ? (
            <img
              src={user.photo}
              alt=""
              className="sidebar-user__avatar-image"
            />
          ) : (
            <CircleUserRound
              className="sidebar-user__avatar"
              aria-hidden="true"
            />
          )}
        </span>

        <span className="sidebar-user__information">
          <span className="sidebar-user__name">
            {user.name}
          </span>

          <span className="sidebar-user__role">
            {user.role}
          </span>
        </span>

        <ChevronRight
          className="sidebar-user__chevron"
          aria-hidden="true"
        />
      </button>

      <button
        type="button"
        className="sidebar-user__logout"
        onClick={onLogout}
      >
        <LogOut
          size={24}
          strokeWidth={2}
          aria-hidden="true"
        />

        <span>Cerrar sesión</span>
      </button>
    </div>
  );
}

export default UserProfile;