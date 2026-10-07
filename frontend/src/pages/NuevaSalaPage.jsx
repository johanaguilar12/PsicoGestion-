import { useNavigate } from "react-router-dom";
import CreateFormPage from "../components/forms/CreateFormPage";
import FormField from "../components/forms/FormField";

function NuevaSalaPage() {
  const navigate = useNavigate();

  const handleSubmit = (event) => {
    event.preventDefault();

  };

  return (
    <CreateFormPage
      breadcrumb={[
        {
          label: "Salas",
          to: "/salas",
        },
        {
          label: "Nueva sala",
        },
      ]}
      title="Nueva sala"
      onSubmit={handleSubmit}
      onCancel={() => navigate("/salas")}
    >
      <section className="create-section">
        <h2 className="create-section__title">
          Información de la sala
        </h2>

        <FormField
          label="Nombre de la sala"
          name="name"
          placeholder="Ej. Sala 1"
          required
        />
      </section>
    </CreateFormPage>
  );
}

export default NuevaSalaPage;