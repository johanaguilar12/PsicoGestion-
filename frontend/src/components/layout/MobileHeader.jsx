import { useState } from "react";
import { TextAlignJustify } from "lucide-react";
import { Link } from "react-router-dom";
import { useAuth } from "../auth/authContext.js";

function MobileHeader({
  isMenuOpen,
  onMenuOpen,
}) {
  const { user } = useAuth();
  const [failedPhoto, setFailedPhoto] = useState(null);
  const showPhoto = user.fotoPerfil && failedPhoto !== user.fotoPerfil;

  return (
    <header className="mobile-header">
      <Link
        to="/"
        className="mobile-header__logo-link"
        aria-label="Ir al inicio"
      >
        <img
          src="/uady-logo-blue.svg"
          alt="Universidad Autónoma de Yucatán"
          className="mobile-header__logo"
        />
      </Link>

      {showPhoto && (
        <Link
          to={`/usuarios/${user.id}`}
          className="mobile-header__profile-link"
          aria-label={`Ver información de ${user.nombreCompleto}`}
        >
          <img
            src={user.fotoPerfil}
            alt=""
            className="mobile-header__profile-photo"
            onError={() => setFailedPhoto(user.fotoPerfil)}
          />
        </Link>
      )}

      <button
        type="button"
        className="mobile-header__menu-button"
        onClick={onMenuOpen}
        aria-label="Abrir menú de navegación"
        aria-controls="main-navigation"
        aria-expanded={isMenuOpen}
      >
        <TextAlignJustify
          size={41}
          strokeWidth={2}
          aria-hidden="true"
        />
      </button>
    </header>
  );
}

export default MobileHeader;