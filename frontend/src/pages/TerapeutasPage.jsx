import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { apiFetch } from "../services/api";
import DataListPage from "../components/data-list/DataListPage";
import "../styles/data-list.css";

const columns = [
  { key: "name", label: "Nombre" },
  { key: "professionalId", label: "Cédula profesional" },
  { key: "phone", label: "Teléfono" },
  { key: "email", label: "Correo" },
  { key: "status", label: "Estatus" },
];

function TerapeutasPage() {
  const navigate = useNavigate();
  const [therapists, setTherapists] = useState([]);
  const [cargando, setCargando] = useState(true);

  useEffect(() => {
    const cargarTerapeutas = async () => {
      try {
        const response = await apiFetch("http://localhost:8081/api/v1/terapeutas");
        if (response.ok) {
          const data = await response.json();
          const terapeutasFormateados = data.map(terapeuta => ({
            id: terapeuta.idTerapeuta,
            name: terapeuta.nombreCompleto,
            professionalId: terapeuta.cedulaProfesional,
            phone: terapeuta.telefono,
            email: terapeuta.correoElectronico,
            status: terapeuta.activo ? "Activo" : "Inactivo"
          }));
          setTherapists(terapeutasFormateados);
        }
      } catch (error) {
        console.error("Error al obtener los terapeutas:", error);
      } finally {
        setCargando(false);
      }
    };
    cargarTerapeutas();
  }, []);

  const handleStatusChange = async (terapeuta) => {
    try {
      const response = await apiFetch(`http://localhost:8081/api/v1/terapeutas/${terapeuta.id}/baja`, {
        method: "PUT",
      });
      return response.ok;
    } catch (error) {
      console.error("Error de red al cambiar estatus:", error);
      return false;
    }
  };

  const handleDelete = async (terapeuta) => {
    try {
      const response = await apiFetch(`http://localhost:8081/api/v1/terapeutas/${terapeuta.id}`, {
        method: "DELETE",
      });
      return response.ok;
    } catch (error) {
      console.error("Error de red al eliminar:", error);
      return false;
    }
  };

  if (cargando) return <div className="create-page"><h1 className="page-title">Cargando terapeutas...</h1></div>;

  return (
    <DataListPage
      title="Terapeutas"
      searchPlaceholder="Buscar por nombre, cédula profesional o correo"
      createLabel="Nuevo terapeuta"
      onCreate={() => navigate("/terapeutas/nuevo")}
      columns={columns}
      data={therapists}
      basePath="/terapeutas"
      searchKeys={["name", "professionalId", "email"]}
      getMobileFields={(therapist) => ({
        title: therapist.name,
        details: [`Cédula: ${therapist.professionalId}`],
      })}
      onStatusChange={handleStatusChange}
      onDelete={handleDelete}
    />
  );
}

export default TerapeutasPage;