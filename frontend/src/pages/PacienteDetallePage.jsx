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

function PacienteDetallePage() {
  const navigate = useNavigate();
  const { patientId } = useParams();
  
  const [paciente, setPaciente] = useState(null);
  const [cargando, setCargando] = useState(true);
  
  const [menuOpen, setMenuOpen] = useState(false);
  const [menuPosition, setMenuPosition] = useState({ top: 0, left: 0 });
  const [confirmDelete, setConfirmDelete] = useState(false);

  useEffect(() => {
    const cargarDetallePaciente = async () => {
      try {
        const response = await apiFetch(`http://localhost:8081/api/v1/pacientes/${patientId}`);
        if (response.ok) {
          const data = await response.json();
          
          setPaciente({
            id: data.idPaciente,
            name: data.nombreCompleto,
            curp: data.curp,
            fechaNacimiento: data.fechaNacimiento,
            sexo: data.sexo === "F" ? "Mujer" : "Hombre",
            correo: "No registrado", 
            telefono: data.telefono,
            status: data.activo ? "Activo" : "Inactivo",
          });
        } else {
          console.error("Paciente no encontrado");
        }
      } catch (error) {
        console.error("Error al obtener el paciente:", error);
      } finally {
        setCargando(false);
      }
    };

    cargarDetallePaciente();
  }, [patientId]);

  const handleMenu = (event) => {
    setMenuPosition(getMenuPosition(event.currentTarget));
    setMenuOpen(true);
  };

   
  const handleToggleStatus = async () => {
    try {
      const response = await apiFetch(`http://localhost:8081/api/v1/pacientes/${patientId}/baja`, {
        method: "PUT",
      });

      if (response.ok) {
        setPaciente((current) => ({
          ...current,
          status: current.status === "Activo" ? "Inactivo" : "Activo",
        }));
      } else {
        console.error("No se pudo cambiar el estatus del paciente");
      }
    } catch (error) {
      console.error("Error de red al cambiar estatus:", error);
    }
    setMenuOpen(false);
  };

  if (cargando) {
    return (
      <div className="create-page detail-page">
        <h1 className="page-title">Cargando perfil...</h1>
      </div>
    );
  }

  if (!paciente) {
    return (
      <div className="create-page detail-page">
        <h1 className="page-title">Error: Paciente no encontrado</h1>
        <button onClick={() => navigate("/pacientes")}>Volver a la lista</button>
      </div>
    );
  }

  return (
    <div className="create-page detail-page">
      <Breadcrumb items={[{ label: "Pacientes", to: "/pacientes" }, { label: paciente.name }]} />

      <DetailHeader
        title="Detalle paciente"
        onEdit={() => navigate(`/pacientes/${patientId}/editar`)}
        onMenu={handleMenu}
      />

      <section className="create-form detail-card">
        <div className="create-form__content">
          <section className="create-section">
            <h2 className="create-section__title">Información personal</h2>
            <div className="form-grid form-grid--two">
              <DetailField label="Nombre completo" value={paciente.name} />
              <DetailField label="CURP" value={paciente.curp} />
              <DetailField label="Fecha de nacimiento" value={paciente.fechaNacimiento} />
              <DetailField label="Sexo" value={paciente.sexo} />
              <DetailField label="Correo institucional" value={paciente.correo} />
              <DetailField label="Teléfono" value={paciente.telefono} />
              <DetailField label="Estatus" value={paciente.status} />
            </div>
          </section>
        </div>
      </section>

      <ActionsMenu
        item={menuOpen ? paciente : null}
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
        title="Eliminar paciente"
        message={`¿Deseas eliminar a ${paciente.name}? Esta acción no se puede deshacer.`}
        confirmLabel="Eliminar"
        danger
        onConfirm={async () => {
          try {
            const response = await apiFetch(`http://localhost:8081/api/v1/pacientes/${patientId}`, {
              method: "DELETE",
            });

            if (response.ok) {
              navigate("/pacientes"); 
            } else {
              console.error("No se pudo eliminar el paciente");
              setConfirmDelete(false);
            }
          } catch (error) {
            console.error("Error de red al eliminar:", error);
            setConfirmDelete(false);
          }
        }}
        onCancel={() => setConfirmDelete(false)}
      />
    </div>
  );
}

export default PacienteDetallePage;