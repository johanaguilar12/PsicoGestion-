import { TextAlignJustify } from "lucide-react";
import { Link } from "react-router-dom";

function MobileHeader({
  isMenuOpen,
  onMenuOpen,
}) {
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