import { useState } from "react";
import { useNavigate } from "react-router-dom";
import CreateFormPage from "../components/forms/CreateFormPage";
import FormField from "../components/forms/FormField";

function NuevoPacientePage() {
  const navigate = useNavigate();
  const [errorMessage, setErrorMessage] = useState(null);

  const handleSubmit = async (event) => {
    event.preventDefault();
    setErrorMessage(null);
    console.log("Paso 1: Botón presionado. Extrayendo datos...");

    try {
      const formData = new FormData(event.target);
      
      const pacienteData = {
        nombreCompleto: formData.get("name"),
        curp: formData.get("curp"),
        sexo: formData.get("sex") === "mujer" ? "F" : "M",
        fechaNacimiento: formData.get("birthDate"),
        telefono: formData.get("phone")
      };

      console.log("Paso 2: Datos listos para enviar:", pacienteData);

      const response = await fetch("http://localhost:8081/api/v1/pacientes", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(pacienteData),
      });

      console.log("Paso 3: Servidor respondió con código:", response.status);

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText);
      }

      console.log("Paso 4: ¡Guardado exitoso! Redirigiendo...");
      navigate("/pacientes");

    } catch (error) {
      console.error("Paso 5: Error detectado:", error);
      setErrorMessage(error.message);
    }
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

        {errorMessage && (
          <div style={{ backgroundColor: "#fee2e2", color: "#991b1b", padding: "1rem", borderRadius: "8px", marginBottom: "1.5rem" }}>
            <strong>Error:</strong> {errorMessage}
          </div>
        )}

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