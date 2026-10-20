import { useState, useEffect } from "react";
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
    left: Math.min(Math.max(margin, rect.right - menuWidth), window.innerWidth - menuWidth - margin),
    top: rect.bottom + 8 + menuHeight <= window.innerHeight - margin ? rect.bottom + 8 : Math.max(margin, rect.top - menuHeight - 8),
  };
};

function UsuarioDetallePage() {
  const navigate = useNavigate();
  const { userId } = useParams();
  
  const [usuario, setUsuario] = useState(null);
  const [cargando, setCargando] = useState(true);
  
  const [menuOpen, setMenuOpen] = useState(false);
  const [menuPosition, setMenuPosition] = useState({ top: 0, left: 0 });
  const [confirmDelete, setConfirmDelete] = useState(false);

  useEffect(() => {
    const cargarDetalle = async () => {
      try {
        const response = await fetch(`http://localhost:8081/api/v1/cuentas/${userId}`);
        if (response.ok) {
          const data = await response.json();
          setUsuario({
            id: data.idSecretaria,
            name: data.nombreCompleto,
            correo: data.correoElectronico,
            telefono: data.telefono,
            rol: data.rol || "Administrador",
            fechaRegistro: data.fechaRegistro ? new Date(data.fechaRegistro).toLocaleDateString() : "Reciente",
            ultimaActualizacion: "N/A",
            foto: data.fotoPerfil || "/images/usuario-ejemplo.jpg",
            status: data.activo ? "Activo" : "Inactivo",
          });
        }
      } catch (error) {
        console.error("Error al obtener usuario:", error);
      } finally {
        setCargando(false);
      }
    };
    cargarDetalle();
  }, [userId]);

  const handleMenu = (event) => {
    setMenuPosition(getMenuPosition(event.currentTarget));
    setMenuOpen(true);
  };

  const handleToggleStatus = async () => {
    try {
      const response = await fetch(`http://localhost:8081/api/v1/cuentas/${userId}/baja`, { method: "PUT" });
      if (response.ok) {
        setUsuario((current) => ({ 
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
  if (!usuario) return <div className="create-page detail-page"><h1 className="page-title">Error: Usuario no encontrado</h1></div>;

  return (
    <div className="create-page detail-page">
      <Breadcrumb items={[{ label: "Usuarios", to: "/usuarios" }, { label: usuario.name }]} />
      <DetailHeader title="Detalle usuario" onEdit={() => navigate(`/usuarios/${userId}/editar`)} onMenu={handleMenu} />

      <section className="create-form detail-card">
        <div className="create-form__content">
          <section className="create-section">
            <h2 className="create-section__title">Información del usuario</h2>

            <div className="create-profile-layout">
              <div className="create-profile-layout__fields">
                <DetailField label="Nombre completo" value={usuario.name} />
                <DetailField label="Correo institucional" value={usuario.correo} />
                <DetailField label="Teléfono" value={usuario.telefono} />
              </div>

              <div className="create-profile-layout__photo detail-profile-photo">
                <img src={usuario.foto} alt={`Fotografía de ${usuario.name}`} className="detail-profile-photo__image" />
              </div>
            </div>

            <div className="form-grid form-grid--two">
              <DetailField label="Rol" value={usuario.rol} />
              <DetailField label="Estatus" value={usuario.status} />
              <DetailField label="Fecha de registro" value={usuario.fechaRegistro} />
              <DetailField label="Última actualización" value={usuario.ultimaActualizacion} />
            </div>
          </section>
        </div>
      </section>

      <ActionsMenu
        item={menuOpen ? usuario : null}
        position={menuPosition}
        onToggleStatus={handleToggleStatus}
        onDelete={() => { setMenuOpen(false); setConfirmDelete(true); }}
        onClose={() => setMenuOpen(false)}
      />

      <ConfirmDialog
        open={confirmDelete}
        title="Eliminar usuario"
        message={`¿Deseas eliminar a ${usuario.name}? Esta acción no se puede deshacer.`}
        confirmLabel="Eliminar"
        danger
        onConfirm={async () => {
          await fetch(`http://localhost:8081/api/v1/cuentas/${userId}`, { method: "DELETE" });
          navigate("/usuarios");
        }}
        onCancel={() => setConfirmDelete(false)}
      />
    </div>
  );
}

export default UsuarioDetallePage;