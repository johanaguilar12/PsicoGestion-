import { useNavigate } from "react-router-dom";
import CreateFormPage from "../components/forms/CreateFormPage";
import FormField from "../components/forms/FormField";

function NuevoPacientePage() {
  const navigate = useNavigate();

  const handleSubmit = (event) => {
    event.preventDefault();

  };

  return (
    <CreateFormPage
      breadcrumb={[
        {
          label: "Pacientes",
          to: "/pacientes",
        },
        {
          label: "Nuevo paciente",
        },
      ]}
      title="Nuevo paciente"
      onSubmit={handleSubmit}
      onCancel={() => navigate("/pacientes")}
    >
      <section className="create-section">
        <h2 className="create-section__title">
          Información personal
        </h2>

        <div className="form-grid form-grid--patient">
          <FormField
            label="Nombre completo"
            name="name"
            placeholder="Ej. María Silva Pineda"
            autoComplete="name"
            required
          />

          <FormField
            label="CURP"
            name="curp"
            placeholder="Ej. SIPM040315MYNLNR08"
            required
          />

          <FormField
            label="Fecha de nacimiento"
            name="birthDate"
            type="date"
            autoComplete="bday"
            required
          />

          <FormField
            label="Sexo"
            name="sex"
            type="select"
            placeholder="Selecciona una opción"
            options={[
              {
                value: "mujer",
                label: "Mujer",
              },
              {
                value: "hombre",
                label: "Hombre",
              },
            ]}
            required
          />

          <FormField
            label="Correo institucional"
            name="email"
            type="email"
            placeholder="ejemplo@correo.uady.mx"
            autoComplete="email"
            required
          />

          <FormField
            label="Teléfono"
            name="phone"
            type="tel"
            placeholder="999 999 9999"
            autoComplete="tel"
            required
          />
        </div>
      </section>
    </CreateFormPage>
  );
}

export default NuevoPacientePage;