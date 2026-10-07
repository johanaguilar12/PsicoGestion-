import { useNavigate } from "react-router-dom";
import DataListPage from "../components/data-list/DataListPage";
import "../styles/data-list.css";

const patients = [
  { id: 1, name: "María Silva Pineda", curp: "SIPM040315MYNLNR08", phone: "999 245 4554", email: "19203742@alumnos.uady.mx", status: "Activo" },
  { id: 2, name: "Raúl Diego Pinto López", curp: "PILR030824HYNRPS06", phone: "999 653 4988", email: "19186424@alumnos.uady.mx", status: "Activo" },
  { id: 3, name: "Luis Fernando Cruz Zapata", curp: "CUZL020512HYNRPS04", phone: "999 945 9401", email: "18253756@alumnos.uady.mx", status: "Inactivo" },
];

const columns = [
  { key: "name", label: "Nombre" },
  { key: "curp", label: "CURP" },
  { key: "phone", label: "Teléfono" },
  { key: "email", label: "Correo" },
  { key: "status", label: "Estatus" },
];

function PacientesPage() {
  const navigate = useNavigate();

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
    />
  );
}

export default PacientesPage;
