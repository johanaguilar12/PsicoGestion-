import { useState } from "react";
import { useNavigate } from "react-router-dom";
import CreateFormPage from "../components/forms/CreateFormPage";
import FormField from "../components/forms/FormField";

function NuevaSalaPage() {
  const navigate = useNavigate();
  const [errorMessage, setErrorMessage] = useState(null);

  const handleSubmit = async (event) => {
    event.preventDefault();
    setErrorMessage(null);

    const payload = {
      nombreSala: event.target.name.value
    };

    try {
      const response = await fetch("http://localhost:8081/api/v1/salas", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText);
      }
      navigate("/salas");
    } catch (error) {
      setErrorMessage(error.message);
    }
  };

  return (
    <CreateFormPage
      breadcrumb={[{ label: "Salas", to: "/salas" }, { label: "Nueva sala" }]}
      title="Nueva sala"
      onSubmit={handleSubmit}
      onCancel={() => navigate("/salas")}
    >
      <section className="create-section">
        <h2 className="create-section__title">Información de la sala</h2>
        {errorMessage && (
          <div style={{ backgroundColor: "#fee2e2", color: "#991b1b", padding: "1rem", borderRadius: "8px", marginBottom: "1.5rem" }}>
            <strong>Error:</strong> {errorMessage}
          </div>
        )}
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