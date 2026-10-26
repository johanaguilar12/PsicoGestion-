import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { apiFetch } from "../services/api";

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
    left: Math.min(Math.max(margin, rect.right - menuWidth), window.innerWidth - menuWidth - margin),
    top: rect.bottom + 8 + menuHeight <= window.innerHeight - margin ? rect.bottom + 8 : Math.max(margin, rect.top - menuHeight - 8),
  };
};

function SalaDetallePage() {
  const navigate = useNavigate();
  const { roomId } = useParams();
  
  const [sala, setSala] = useState(null);
  const [cargando, setCargando] = useState(true);
  
  const [menuOpen, setMenuOpen] = useState(false);
  const [menuPosition, setMenuPosition] = useState({ top: 0, left: 0 });
  const [confirmDelete, setConfirmDelete] = useState(false);

  useEffect(() => {
    const cargarDetalle = async () => {
      try {
        const response = await apiFetch(`http://localhost:8081/api/v1/salas/${roomId}`);
        if (response.ok) {
          const data = await response.json();
          setSala({
            id: data.idSala,
            name: data.nombreSala,
            status: "Libre", 
          });
        }
      } catch (error) {
        console.error("Error al obtener la sala:", error);
      } finally {
        setCargando(false);
      }
    };
    cargarDetalle();
  }, [roomId]);

  const handleMenu = (event) => {
    setMenuPosition(getMenuPosition(event.currentTarget));
    setMenuOpen(true);
  };

  if (cargando) return <div className="create-page detail-page"><h1 className="page-title">Cargando sala...</h1></div>;
  if (!sala) return <div className="create-page detail-page"><h1 className="page-title">Error: Sala no encontrada</h1></div>;

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
            <DetailField label="Disponibilidad actual" value={sala.status} />
          </section>
        </div>
      </section>

      <ActionsMenu
        item={menuOpen ? sala : null}
        position={menuPosition}
        
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
        onConfirm={async () => {
          await apiFetch(`http://localhost:8081/api/v1/salas/${roomId}`, { method: "DELETE" });
          navigate("/salas");
        }}
        onCancel={() => setConfirmDelete(false)}
      />
    </div>
  );
}

export default SalaDetallePage;