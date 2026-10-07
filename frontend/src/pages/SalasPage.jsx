import { useNavigate } from "react-router-dom";
import DataListPage from "../components/data-list/DataListPage";
import "../styles/data-list.css";

const rooms = [
  { id: 1, name: "Sala 1", status: "Activo", showView: false },
  { id: 2, name: "Sala 2", status: "Activo", showView: false },
  { id: 3, name: "Sala 3", status: "Inactivo", showView: false },
];

const columns = [
  { key: "name", label: "Nombre de la sala" },
  { key: "status", label: "Estatus" },
];

function SalasPage() {
  const navigate = useNavigate();

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
    />
  );
}

export default SalasPage;
