import { useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import ActionsMenu from "../components/data-list/ActionsMenu";
import ConfirmDialog from "../components/data-list/ConfirmDialog";
import Breadcrumb from "../components/forms/Breadcrumb";
import DetailField from "../components/forms/DetailField";
import DetailHeader from "../components/forms/DetailHeader";

const getMenuPosition = (target) => {
  const rect = target.getBoundingClientRect();
  const menuWidth = 190;
  const menuHeight = 104;
  const margin = 12;

  return {
    left: Math.min(
      Math.max(margin, rect.right - menuWidth),
      window.innerWidth - menuWidth - margin,
    ),
    top:
      rect.bottom + 8 + menuHeight <= window.innerHeight - margin
        ? rect.bottom + 8
        : Math.max(margin, rect.top - menuHeight - 8),
  };
};

function TerapeutaDetallePage() {
  const navigate = useNavigate();
  const { therapistId } = useParams();
  const [terapeuta, setTerapeuta] = useState({
    id: therapistId,
    name: "Francisco Suárez León",
    correo: "ejemplo@correo.uady.mx",
    telefono: "999 245 4554",
    fechaNacimiento: "03/03/1970",
    sexo: "Hombre",
    cedula: "45646797",
    foto: "/images/terapeuta-ejemplo.jpg",
    status: "Activo",
  });
  const [menuOpen, setMenuOpen] = useState(false);
  const [menuPosition, setMenuPosition] = useState({ top: 0, left: 0 });
  const [confirmDelete, setConfirmDelete] = useState(false);

  const handleMenu = (event) => {
    setMenuPosition(getMenuPosition(event.currentTarget));
    setMenuOpen(true);
  };

  const handleToggleStatus = () => {
    setTerapeuta((current) => ({
      ...current,
      status: current.status === "Activo" ? "Inactivo" : "Activo",
    }));
    setMenuOpen(false);
  };

  return (
    <div className="create-page detail-page">
      <Breadcrumb items={[{ label: "Terapeutas", to: "/terapeutas" }, { label: terapeuta.name }]} />

      <DetailHeader
        title="Detalle terapeuta"
        onEdit={() => navigate(`/terapeutas/${therapistId}/editar`)}
        onMenu={handleMenu}
      />

      <section className="create-form detail-card">
        <div className="create-form__content">
          <section className="create-section">
            <h2 className="create-section__title">Información personal</h2>

            <div className="create-profile-layout">
              <div className="create-profile-layout__fields">
                <DetailField label="Nombre completo" value={terapeuta.name} />
                <DetailField label="Correo electrónico" value={terapeuta.correo} />
                <DetailField label="Teléfono" value={terapeuta.telefono} />
              </div>

              <div className="create-profile-layout__photo detail-profile-photo">
                <img
                  src={terapeuta.foto}
                  alt={`Fotografía de ${terapeuta.name}`}
                  className="detail-profile-photo__image"
                />
              </div>
            </div>

            <div className="form-grid form-grid--two">
              <DetailField label="Fecha de nacimiento" value={terapeuta.fechaNacimiento} />
              <DetailField label="Sexo" value={terapeuta.sexo} />
            </div>

            <div className="create-section__subsection">
              <h2 className="create-section__title">Información profesional</h2>
              <DetailField label="Cédula profesional" value={terapeuta.cedula} />
            </div>
          </section>
        </div>
      </section>

      <ActionsMenu
        item={menuOpen ? terapeuta : null}
        position={menuPosition}
        onToggleStatus={handleToggleStatus}
        onDelete={() => {
          setMenuOpen(false);
          setConfirmDelete(true);
        }}
        onClose={() => setMenuOpen(false)}
      />

      <ConfirmDialog
        open={confirmDelete}
        title="Eliminar terapeuta"
        message={`¿Deseas eliminar a ${terapeuta.name}? Esta acción no se puede deshacer.`}
        confirmLabel="Eliminar"
        danger
        onConfirm={() => navigate("/terapeutas")}
        onCancel={() => setConfirmDelete(false)}
      />
    </div>
  );
}

export default TerapeutaDetallePage;
