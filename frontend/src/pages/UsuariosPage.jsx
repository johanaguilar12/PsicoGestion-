import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import DataListPage from "../components/data-list/DataListPage";
import "../styles/data-list.css";

const columns = [
  { key: "name", label: "Nombre" },
  { key: "email", label: "Correo" },
  { key: "phone", label: "Teléfono" },
  { key: "role", label: "Rol" },
  { key: "status", label: "Estatus" },
];

function UsuariosPage() {
  const navigate = useNavigate();
  const [users, setUsers] = useState([]);
  const [cargando, setCargando] = useState(true);

  useEffect(() => {
    const cargarUsuarios = async () => {
      try {
        const response = await fetch("http://localhost:8081/api/v1/cuentas");
        if (response.ok) {
          const data = await response.json();
          const usuariosFormateados = data.map(user => ({
            id: user.idSecretaria,
            name: user.nombreCompleto,
            email: user.correoElectronico,
            phone: user.telefono,
            role: user.rol || "Administrador",
            status: user.activo ? "Activo" : "Inactivo"
          }));
          setUsers(usuariosFormateados);
        }
      } catch (error) {
        console.error("Error al obtener usuarios:", error);
      } finally {
        setCargando(false);
      }
    };
    cargarUsuarios();
  }, []);

  const handleStatusChange = async (user) => {
    try {
      const response = await fetch(`http://localhost:8081/api/v1/cuentas/${user.id}/baja`, { method: "PUT" });
      return response.ok;
    } catch (error) {
      console.error("Error de red al cambiar estatus:", error);
      return false;
    }
  };

  const handleDelete = async (user) => {
    try {
      const response = await fetch(`http://localhost:8081/api/v1/cuentas/${user.id}`, { method: "DELETE" });
      return response.ok;
    } catch (error) {
      console.error("Error de red al eliminar:", error);
      return false;
    }
  };

  if (cargando) return <div className="create-page"><h1 className="page-title">Cargando usuarios...</h1></div>;

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
      onStatusChange={handleStatusChange}
      onDelete={handleDelete}
    />
  );
}

export default UsuariosPage;