import { useNavigate } from "react-router-dom";
import CreateFormPage from "../components/forms/CreateFormPage";
import FormField from "../components/forms/FormField";
import PhotoUpload from "../components/forms/PhotoUpload";

function NuevoTerapeutaPage() {
  const navigate = useNavigate();

  const handleSubmit = (event) => {
    event.preventDefault();

  };

  return (
    <CreateFormPage
      breadcrumb={[
        {
          label: "Terapeutas",
          to: "/terapeutas",
        },
        {
          label: "Nuevo terapeuta",
        },
      ]}
      title="Nuevo terapeuta"
      onSubmit={handleSubmit}
      onCancel={() => navigate("/terapeutas")}
    >
      <section className="create-section">
        <h2 className="create-section__title">
          Información personal
        </h2>

        <div className="create-profile-layout">
          <div className="create-profile-layout__fields">
            <FormField
              label="Nombre completo"
              name="name"
              placeholder="Ej. María Silva Pineda"
              required
            />

            <FormField
              label="Correo electrónico"
              name="email"
              type="email"
              placeholder="ejemplo@correo.uady.mx"
              required
            />

            <FormField
              label="Teléfono"
              name="phone"
              type="tel"
              placeholder="999 999 9999"
              required
            />
          </div>

          <div className="create-profile-layout__photo">
            <PhotoUpload />
          </div>
        </div>

        <div className="form-grid form-grid--two">
          <FormField
            label="Fecha de nacimiento"
            name="birthDate"
            type="date"
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
        </div>

        <div className="create-section__subsection">
          <h2 className="create-section__title">
            Información profesional
          </h2>

          <FormField
            label="Cédula profesional"
            name="professionalLicense"
            placeholder="Ej. 12345678"
            required
          />
        </div>
      </section>
    </CreateFormPage>
  );
}

export default NuevoTerapeutaPage;