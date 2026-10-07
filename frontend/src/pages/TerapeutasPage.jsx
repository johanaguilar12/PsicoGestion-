import { useNavigate } from "react-router-dom";
import DataListPage from "../components/data-list/DataListPage";
import "../styles/data-list.css";

const therapists = [
  { id: 1, name: "Francisco Suárez León", professionalId: "45646797", phone: "999 245 4554", email: "ejemplo@correo.uady.mx", status: "Activo" },
  { id: 2, name: "Martha Carolina Rivero Paz", professionalId: "22458643", phone: "999 653 4988", email: "ejemplo@correo.uady.mx", status: "Activo" },
  { id: 3, name: "Armando Quintal Palma", professionalId: "66532486", phone: "999 945 9401", email: "ejemplo@correo.uady.mx", status: "Activo" },
];

const columns = [
  { key: "name", label: "Nombre" },
  { key: "professionalId", label: "Cédula profesional" },
  { key: "phone", label: "Teléfono" },
  { key: "email", label: "Correo" },
  { key: "status", label: "Estatus" },
];

function TerapeutasPage() {
  const navigate = useNavigate();

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
    />
  );
}

export default TerapeutasPage;
