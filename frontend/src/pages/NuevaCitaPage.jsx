import { useNavigate } from "react-router-dom";
import CreateFormPage from "../components/forms/CreateFormPage";
import FormField from "../components/forms/FormField";

function NuevaCitaPage() {
  const navigate = useNavigate();

  const handleSubmit = (event) => {
    event.preventDefault();
  };

  return (
    <CreateFormPage
      breadcrumb={[
        {
          label: "Agenda",
          to: "/agenda",
        },
        {
          label: "Nueva cita",
        },
      ]}
      title="Nueva cita"
      onSubmit={handleSubmit}
      onCancel={() => navigate("/agenda")}
    >
      <section className="create-section">
        <h2 className="create-section__title">
          Información de la cita
        </h2>

        <FormField
          label="Nombre del paciente"
          name="patient"
          type="select"
          placeholder="Selecciona una opción"
          options={[]}
          required
        />

        <FormField
          label="Nombre del terapeuta"
          name="therapist"
          type="select"
          placeholder="Selecciona una opción"
          options={[]}
          required
        />

        <FormField
          label="Sala"
          name="room"
          type="select"
          placeholder="Selecciona una opción"
          options={[]}
          required
        />

        <div className="form-grid form-grid--two">
          <FormField
            label="Fecha"
            name="date"
            type="date"
            required
          />

          <FormField
            label="Horario"
            name="time"
            type="time"
            required
          />
        </div>

        <FormField
          label="Notas"
          name="notes"
          placeholder="Agrega notas relevantes de la cita..."
        />
      </section>
    </CreateFormPage>
  );
}

export default NuevaCitaPage;