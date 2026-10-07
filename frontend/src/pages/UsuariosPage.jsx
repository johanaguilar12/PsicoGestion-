import { useNavigate } from "react-router-dom";
import DataListPage from "../components/data-list/DataListPage";
import "../styles/data-list.css";

const users = [
  { id: 1, name: "Celia Díaz Ramos", email: "ejemplo@correo.uady.mx", phone: "999 999 9999", role: "Administrador", status: "Activo" },
];

const columns = [
  { key: "name", label: "Nombre" },
  { key: "email", label: "Correo" },
  { key: "phone", label: "Teléfono" },
  { key: "role", label: "Rol" },
  { key: "status", label: "Estatus" },
];

function UsuariosPage() {
  const navigate = useNavigate();

  return (
    <DataListPage
      title="Usuarios"
      searchPlaceholder="Buscar por nombre o correo"
      createLabel="Nuevo usuario"
      onCreate={() => navigate("/usuarios/nuevo")}
      columns={columns}
      data={users}
      basePath="/usuarios"
      searchKeys={["name", "email"]}
      getMobileFields={(user) => ({
        title: user.name,
        details: [user.email, user.role],
      })}
    />
  );
}

export default UsuariosPage;
