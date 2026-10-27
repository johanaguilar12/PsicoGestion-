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

function TerapeutaDetallePage() {
  const navigate = useNavigate();
  const { therapistId } = useParams();
  
  const [terapeuta, setTerapeuta] = useState(null);
  const [cargando, setCargando] = useState(true);
  
  const [menuOpen, setMenuOpen] = useState(false);
  const [menuPosition, setMenuPosition] = useState({ top: 0, left: 0 });
  const [confirmDelete, setConfirmDelete] = useState(false);

  useEffect(() => {
    const cargarDetalle = async () => {
      try {
        const response = await apiFetch(`http://localhost:8081/api/v1/terapeutas/${therapistId}`);
        if (response.ok) {
          const data = await response.json();
          setTerapeuta({
            id: data.idTerapeuta,
            name: data.nombreCompleto,
            correo: data.correoElectronico,
            telefono: data.telefono,
            fechaNacimiento: data.fechaNacimiento,
            sexo: data.sexo === "F" ? "Mujer" : "Hombre",
            cedula: data.cedulaProfesional,
            foto: data.fotoPerfil || "/images/terapeuta-ejemplo.jpg",
            status: data.activo ? "Activo" : "Inactivo",
          });
        }
      } catch (error) {
        console.error("Error al obtener terapeuta:", error);
      } finally {
        setCargando(false);
      }
    };
    cargarDetalle();
  }, [therapistId]);

  const handleMenu = (event) => {
    setMenuPosition(getMenuPosition(event.currentTarget));
    setMenuOpen(true);
  };

  // 🚨 Función modificada: Alterna entre Activo e Inactivo dinámicamente
  const handleToggleStatus = async () => {
    try {
      const response = await apiFetch(`http://localhost:8081/api/v1/terapeutas/${therapistId}/baja`, { method: "PUT" });
      if (response.ok) {
        setTerapeuta((current) => ({ 
            ...current, 
            status: current.status === "Activo" ? "Inactivo" : "Activo" 
        }));
      }
    } catch (error) {
      console.error("Error al cambiar estatus:", error);
    }
    setMenuOpen(false);
  };

  if (cargando) return <div className="create-page detail-page"><h1 className="page-title">Cargando perfil...</h1></div>;
  if (!terapeuta) return <div className="create-page detail-page"><h1 className="page-title">Error: Terapeuta no encontrado</h1></div>;

  return (
    <div className="create-page detail-page">
      <Breadcrumb items={[{ label: "Terapeutas", to: "/terapeutas" }, { label: terapeuta.name }]} />
      <DetailHeader title="Detalle terapeuta" onEdit={() => navigate(`/terapeutas/${therapistId}/editar`)} onMenu={handleMenu} />

      <section className="create-form detail-card">
        <div className="create-form__content">
          <section className="create-section">
            <h2 className="create-section__title">Información personal</h2>
            <div className="create-profile-layout">
              <div className="create-profile-layout__fields">
                <DetailField label="Nombre completo" value={terapeuta.name} />
                <DetailField label="Correo electrónico" value={terapeuta.correo} />
                <DetailField label="Teléfono" value={terapeuta.telefono} />
                <DetailField label="Estatus actual" value={terapeuta.status} />
              </div>
              <div className="create-profile-layout__photo detail-profile-photo">
                <img src={terapeuta.foto} alt={`Fotografía de ${terapeuta.name}`} className="detail-profile-photo__image" />
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
        onDelete={() => { setMenuOpen(false); setConfirmDelete(true); }}
        onClose={() => setMenuOpen(false)}
      />

      <ConfirmDialog
        open={confirmDelete}
        title="Eliminar terapeuta"
        message={`¿Deseas eliminar a ${terapeuta.name}? Esta acción no se puede deshacer.`}
        confirmLabel="Eliminar"
        danger
        onConfirm={async () => {
          await apiFetch(`http://localhost:8081/api/v1/terapeutas/${therapistId}`, { method: "DELETE" });
          navigate("/terapeutas");
        }}
        onCancel={() => setConfirmDelete(false)}
      />
    </div>
  );
}

export default TerapeutaDetallePage;