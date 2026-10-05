import { Link } from "react-router-dom";

function Brand() {
  return (
    <div className="sidebar-brand">
      <Link
        to="/"
        className="sidebar-brand__logo-link"
        aria-label="Ir al inicio"
      >
        <img
          src="/uady-logo-white.svg"
          alt="Universidad Autónoma de Yucatán"
          className="sidebar-brand__uady"
        />
      </Link>

      <div className="sidebar-brand__product">
        <span className="sidebar-brand__name">
          Psico<span>Gestión</span>
        </span>

        <span className="sidebar-brand__subtitle">
          CLÍNICA PSICOLÓGICA
        </span>

        <span className="sidebar-brand__subtitle">
          SEAP <span aria-hidden="true">•</span> UADY
        </span>
      </div>
    </div>
  );
}

export default Brand;