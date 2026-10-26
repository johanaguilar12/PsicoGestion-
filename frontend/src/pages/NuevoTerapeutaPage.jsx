import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { apiFetch } from "../services/api";
import CreateFormPage from "../components/forms/CreateFormPage";
import FormField from "../components/forms/FormField";
import PhotoUpload from "../components/forms/PhotoUpload";

function NuevoTerapeutaPage() {
  const navigate = useNavigate();
  const [errorMessage, setErrorMessage] = useState(null);

  const handleSubmit = async (event) => {
    event.preventDefault();
    setErrorMessage(null);

    
    const formData = new FormData();
    formData.append("nombreCompleto", event.target.name.value);
    formData.append("correoElectronico", event.target.email.value);
    formData.append("telefono", event.target.phone.value);
    formData.append("fechaNacimiento", event.target.birthDate.value);
    formData.append("sexo", event.target.sex.value === "mujer" ? "F" : "M");
    formData.append("cedulaProfesional", event.target.professionalLicense.value);

    
    const fileInput = document.querySelector('input[type="file"]');
    if (fileInput && fileInput.files[0]) {
      formData.append("foto", fileInput.files[0]);
    }

    try {
      
      const response = await apiFetch("http://localhost:8081/api/v1/terapeutas", {
        method: "POST",
        body: formData,
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText);
      }
      navigate("/terapeutas");
    } catch (error) {
      setErrorMessage(error.message);
    }
  };

  return (
    <CreateFormPage
      breadcrumb={[{ label: "Terapeutas", to: "/terapeutas" }, { label: "Nuevo terapeuta" }]}
      title="Nuevo terapeuta"
      onSubmit={handleSubmit}
      onCancel={() => navigate("/terapeutas")}
    >
      <section className="create-section">
        <h2 className="create-section__title">Información personal</h2>
        {errorMessage && (
          <div style={{ backgroundColor: "#fee2e2", color: "#991b1b", padding: "1rem", borderRadius: "8px", marginBottom: "1.5rem" }}>
            <strong>Error:</strong> {errorMessage}
          </div>
        )}
        <div className="create-profile-layout">
          <div className="create-profile-layout__fields">
            <FormField label="Nombre completo" name="name" placeholder="Ej. María Silva Pineda" required />
            <FormField label="Correo electrónico" name="email" type="email" placeholder="ejemplo@correo.uady.mx" required />
            <FormField label="Teléfono" name="phone" type="tel" placeholder="999 999 9999" required />
          </div>
          <div className="create-profile-layout__photo">
            <PhotoUpload />
          </div>
        </div>

        <div className="form-grid form-grid--two">
          <FormField label="Fecha de nacimiento" name="birthDate" type="date" required />
          <FormField label="Sexo" name="sex" type="select" placeholder="Selecciona una opción" options={[{ value: "mujer", label: "Mujer" }, { value: "hombre", label: "Hombre" }]} required />
        </div>

        <div className="create-section__subsection">
          <h2 className="create-section__title">Información profesional</h2>
          <FormField label="Cédula profesional" name="professionalLicense" placeholder="Ej. 12345678" required />
        </div>
      </section>
    </CreateFormPage>
  );
}

export default NuevoTerapeutaPage;