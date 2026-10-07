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

function PacienteDetallePage() {
  const navigate = useNavigate();
  const { patientId } = useParams();
  const [paciente, setPaciente] = useState({
    id: patientId,
    name: "Raúl Diego Pinto López",
    curp: "PILR030824HYNRPS06",
    fechaNacimiento: "24/08/2003",
    sexo: "Hombre",
    correo: "19186424@alumnos.uady.mx",
    telefono: "999 653 4988",
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
    setPaciente((current) => ({
      ...current,
      status: current.status === "Activo" ? "Inactivo" : "Activo",
    }));
    setMenuOpen(false);
  };

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
        onConfirm={() => navigate("/pacientes")}
        onCancel={() => setConfirmDelete(false)}
      />
    </div>
  );
}

export default PacienteDetallePage;
