import { useNavigate } from "react-router-dom";
import CreateFormPage from "../components/forms/CreateFormPage";
import FormField from "../components/forms/FormField";
import PhotoUpload from "../components/forms/PhotoUpload";

function NuevoUsuarioPage() {
  const navigate = useNavigate();

  const handleSubmit = (event) => {
    event.preventDefault();

  };

  return (
    <CreateFormPage
      breadcrumb={[
        {
          label: "Usuarios",
          to: "/usuarios",
        },
        {
          label: "Nuevo usuario",
        },
      ]}
      title="Nuevo usuario"
      onSubmit={handleSubmit}
      onCancel={() => navigate("/usuarios")}
    >
      <section className="create-section">
        <h2 className="create-section__title">
          Información del usuario
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
              label="Correo institucional"
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

        <div className="create-user__role">
          <FormField
            label="Rol"
            name="role"
            defaultValue="Administrador"
            readOnly
            required
          />
        </div>

        <div className="form-grid form-grid--two">
          <FormField
            label="Contraseña"
            name="password"
            type="password"
            placeholder="Mínimo 8 caracteres"
            required
          />

          <FormField
            label="Confirmar contraseña"
            name="confirmPassword"
            type="password"
            placeholder="Confirma la contraseña"
            required
          />
        </div>
      </section>
    </CreateFormPage>
  );
}

export default NuevoUsuarioPage;