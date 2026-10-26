import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { apiFetch } from "../services/api";
import DataListPage from "../components/data-list/DataListPage";
import "../styles/data-list.css";

const columns = [
  { key: "name", label: "Nombre" },
  { key: "curp", label: "CURP" },
  { key: "phone", label: "Teléfono" },
  { key: "email", label: "Correo" },
  { key: "status", label: "Estatus" },
];

function PacientesPage() {
  const navigate = useNavigate();
  const [patients, setPatients] = useState([]);
  const [cargando, setCargando] = useState(true);

  useEffect(() => {
    const cargarPacientes = async () => {
      try {
        const response = await apiFetch("http://localhost:8081/api/v1/pacientes");
        if (response.ok) {
          const data = await response.json();
          
          if (Array.isArray(data)) {
            const pacientesFormateados = data.map(paciente => ({
              id: paciente.idPaciente,
              name: paciente.nombreCompleto,
              curp: paciente.curp,
              phone: paciente.telefono,
              email: "No registrado", 
              status: paciente.activo ? "Activo" : "Inactivo" 
            }));
            setPatients(pacientesFormateados);
          }
        }
      } catch (error) {
        console.error("Error al obtener los pacientes:", error);
      } finally {
        setCargando(false);
      }
    };

    cargarPacientes();
  }, []);

  
  const handleStatusChange = async (paciente) => {
    try {
      const response = await apiFetch(`http://localhost:8081/api/v1/pacientes/${paciente.id}/baja`, {
        method: "PUT",
      });
      if (response.ok) return true;
      
      console.error("No se pudo cambiar el estatus del paciente");
      return false;
    } catch (error) {
      console.error("Error de red al cambiar estatus:", error);
      return false;
    }
  };

  
  const handleDelete = async (paciente) => {
    try {
      const response = await apiFetch(`http://localhost:8081/api/v1/pacientes/${paciente.id}`, {
        method: "DELETE",
      });
      if (response.ok) return true;

      console.error("No se pudo eliminar el paciente");
      return false;
    } catch (error) {
      console.error("Error de red al eliminar:", error);
      return false;
    }
  };

  if (cargando) {
    return (
      <div className="create-page">
        <h1 className="page-title">Cargando pacientes...</h1>
      </div>
    );
  }

  return (
    <DataListPage
      title="Pacientes"
      searchPlaceholder="Buscar por nombre, CURP o correo"
      createLabel="Nuevo paciente"
      onCreate={() => navigate("/pacientes/nuevo")}
      columns={columns}
      data={patients}
      basePath="/pacientes"
      searchKeys={["name", "curp", "email"]}
      getMobileFields={(patient) => ({
        title: patient.name,
        details: [`CURP: ${patient.curp}`],
      })}
      onStatusChange={handleStatusChange} 
      onDelete={handleDelete}             
    />
  );
}

export default PacientesPage;