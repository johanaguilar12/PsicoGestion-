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

function SalaDetallePage() {
  const navigate = useNavigate();
  const { roomId } = useParams();
  const [sala, setSala] = useState({
    id: roomId,
    name: "Sala 2",
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
    setSala((current) => ({
      ...current,
      status: current.status === "Activo" ? "Inactivo" : "Activo",
    }));
    setMenuOpen(false);
  };

  return (
    <div className="create-page detail-page">
      <Breadcrumb items={[{ label: "Salas", to: "/salas" }, { label: sala.name }]} />

      <DetailHeader
        title="Detalle sala"
        onEdit={() => navigate(`/salas/${roomId}/editar`)}
        onMenu={handleMenu}
      />

      <section className="create-form detail-card">
        <div className="create-form__content">
          <section className="create-section">
            <h2 className="create-section__title">Información de la sala</h2>
            <DetailField label="Nombre de la sala" value={sala.name} />
          </section>
        </div>
      </section>

      <ActionsMenu
        item={menuOpen ? sala : null}
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
        title="Eliminar sala"
        message={`¿Deseas eliminar ${sala.name}? Esta acción no se puede deshacer.`}
        confirmLabel="Eliminar"
        danger
        onConfirm={() => navigate("/salas")}
        onCancel={() => setConfirmDelete(false)}
      />
    </div>
  );
}

export default SalaDetallePage;
