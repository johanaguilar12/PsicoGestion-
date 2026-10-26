import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { apiFetch } from "../services/api";
import DataListPage from "../components/data-list/DataListPage";
import "../styles/data-list.css";

const columns = [
  { key: "name", label: "Nombre de la sala" },
  { key: "status", label: "Estatus" },
];

function SalasPage() {
  const navigate = useNavigate();
  const [rooms, setRooms] = useState([]);
  const [cargando, setCargando] = useState(true);

  useEffect(() => {
    const cargarSalas = async () => {
      try {
        const response = await apiFetch("http://localhost:8081/api/v1/salas");
        if (response.ok) {
          const data = await response.json();
          const salasFormateadas = data.map(sala => ({
            id: sala.idSala,
            name: sala.nombreSala,
            status: "Libre" // Por RF 4.3.3. Se volverá dinámico con el módulo de Citas
          }));
          setRooms(salasFormateadas);
        }
      } catch (error) {
        console.error("Error al obtener las salas:", error);
      } finally {
        setCargando(false);
      }
    };
    cargarSalas();
  }, []);

  const handleDelete = async (sala) => {
    try {
      const response = await apiFetch(`http://localhost:8081/api/v1/salas/${sala.id}`, {
        method: "DELETE",
      });
      return response.ok;
    } catch (error) {
      console.error("Error de red al eliminar:", error);
      return false;
    }
  };

  if (cargando) return <div className="create-page"><h1 className="page-title">Cargando salas...</h1></div>;

  return (
    <DataListPage
      title="Salas"
      searchPlaceholder="Buscar por nombre"
      createLabel="Nueva sala"
      onCreate={() => navigate("/salas/nueva")}
      columns={columns}
      data={rooms}
      tableVariant="rooms"
      basePath="/salas"
      searchKeys={["name"]}
      getMobileFields={(room) => ({ title: room.name, details: [] })}
      onDelete={handleDelete}
    />
  );
}

export default SalasPage;