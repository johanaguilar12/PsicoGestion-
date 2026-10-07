import { useState } from "react";
import { Eye, EyeOff, Info } from "lucide-react";
import { useNavigate, useParams } from "react-router-dom";

import Breadcrumb from "../components/forms/Breadcrumb";

function EditarUsuarioPage() {
  const navigate = useNavigate();
  const { userId } = useParams();

  const [formData, setFormData] = useState({
    nombre: "Celia Díaz Ramos",
    correo: "admin@correo.uady.mx",
    telefono: "999 999 9999",
    rol: "Administrador",
    nuevaContrasena: "",
    confirmarContrasena: "",
  });

  const [photoPreview, setPhotoPreview] = useState(
    "/images/usuario-ejemplo.jpg"
  );

  const [showNewPassword, setShowNewPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  const handleChange = (event) => {
    const { name, value } = event.target;

    setFormData((current) => ({
      ...current,
      [name]: value,
    }));
  };

  const handlePhotoChange = (event) => {
    const file = event.target.files?.[0];

    if (!file) return;

    setPhotoPreview(URL.createObjectURL(file));
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    navigate(`/usuarios/${userId}`);
  };

  return (
    <div className="create-page">
      <Breadcrumb
        items={[
          { label: "Usuarios", to: "/usuarios" },
          {
            label: "Celia Díaz Ramos",
            to: `/usuarios/${userId}`,
          },
          { label: "Editar usuario" },
        ]}
      />

      <h1 className="page-title create-page__title">
        Editar usuario
      </h1>

      <form className="create-form" onSubmit={handleSubmit}>
        <div className="create-form__content">
          <section className="create-section">
            <h2 className="create-section__title">
              Información del usuario
            </h2>

            <div className="create-profile-layout">
              <div className="create-profile-layout__fields">
                <div className="form-field">
                  <label className="form-field__label" htmlFor="nombre">
                    Nombre completo{" "}
                    <span className="form-field__required">*</span>
                  </label>

                  <div className="form-field__control">
                    <input
                      id="nombre"
                      name="nombre"
                      type="text"
                      className="form-field__input"
                      value={formData.nombre}
                      onChange={handleChange}
                      required
                    />
                  </div>
                </div>

                <div className="form-field">
                  <label className="form-field__label" htmlFor="correo">
                    Correo institucional{" "}
                    <span className="form-field__required">*</span>
                  </label>

                  <div className="form-field__control">
                    <input
                      id="correo"
                      name="correo"
                      type="email"
                      className="form-field__input"
                      value={formData.correo}
                      onChange={handleChange}
                      required
                    />
                  </div>
                </div>

                <div className="form-field">
                  <label className="form-field__label" htmlFor="telefono">
                    Teléfono{" "}
                    <span className="form-field__required">*</span>
                  </label>

                  <div className="form-field__control">
                    <input
                      id="telefono"
                      name="telefono"
                      type="tel"
                      className="form-field__input"
                      value={formData.telefono}
                      onChange={handleChange}
                      required
                    />
                  </div>
                </div>
              </div>

              <div className="create-profile-layout__photo">
                <div className="photo-upload">
                  <img
                    src={photoPreview}
                    alt="Fotografía del usuario"
                    className="photo-upload__preview photo-upload__preview--image"
                  />

                  <input
                    id="foto-usuario"
                    type="file"
                    className="photo-upload__input"
                    accept=".jpg,.jpeg,.png,image/jpeg,image/png"
                    onChange={handlePhotoChange}
                  />

                  <label
                    htmlFor="foto-usuario"
                    className="photo-upload__button"
                  >
                    Editar foto
                  </label>

                  <p className="photo-upload__help">
                    JPG, PNG, JPEG (máx. 2MB)
                  </p>
                </div>
              </div>
            </div>

            <div className="form-field create-user__role">
              <label className="form-field__label" htmlFor="rol">
                Rol{" "}
                <span className="form-field__required">*</span>
              </label>

              <div className="form-field__control">
                <select
                  id="rol"
                  name="rol"
                  className="form-field__input form-field__select"
                  value={formData.rol}
                  onChange={handleChange}
                  required
                >
                  <option value="Administrador">Administrador</option>
                  <option value="Secretaria">Secretaria</option>
                </select>
              </div>
            </div>

            <div className="form-grid form-grid--two">
              <div className="form-field">
                <label
                  className="form-field__label"
                  htmlFor="nuevaContrasena"
                >
                  Nueva contraseña
                </label>

                <div className="form-field__control">
                  <input
                    id="nuevaContrasena"
                    name="nuevaContrasena"
                    type={showNewPassword ? "text" : "password"}
                    className="form-field__input form-field__input--with-action"
                    value={formData.nuevaContrasena}
                    onChange={handleChange}
                    placeholder="Mínimo 8 caracteres"
                    autoComplete="new-password"
                    minLength={8}
                  />

                  <button
                    type="button"
                    className="form-field__action"
                    onClick={() =>
                      setShowNewPassword((current) => !current)
                    }
                    aria-label={
                      showNewPassword
                        ? "Ocultar nueva contraseña"
                        : "Mostrar nueva contraseña"
                    }
                  >
                    {showNewPassword ? (
                      <EyeOff
                        size={20}
                        strokeWidth={1.8}
                        aria-hidden="true"
                      />
                    ) : (
                      <Eye
                        size={20}
                        strokeWidth={1.8}
                        aria-hidden="true"
                      />
                    )}
                  </button>
                </div>
              </div>

              <div className="form-field">
                <label
                  className="form-field__label"
                  htmlFor="confirmarContrasena"
                >
                  Confirmar nueva contraseña
                </label>

                <div className="form-field__control">
                  <input
                    id="confirmarContrasena"
                    name="confirmarContrasena"
                    type={showConfirmPassword ? "text" : "password"}
                    className="form-field__input form-field__input--with-action"
                    value={formData.confirmarContrasena}
                    onChange={handleChange}
                    placeholder="Confirma la contraseña"
                    autoComplete="new-password"
                    minLength={8}
                  />

                  <button
                    type="button"
                    className="form-field__action"
                    onClick={() =>
                      setShowConfirmPassword((current) => !current)
                    }
                    aria-label={
                      showConfirmPassword
                        ? "Ocultar confirmación de contraseña"
                        : "Mostrar confirmación de contraseña"
                    }
                  >
                    {showConfirmPassword ? (
                      <EyeOff
                        size={20}
                        strokeWidth={1.8}
                        aria-hidden="true"
                      />
                    ) : (
                      <Eye
                        size={20}
                        strokeWidth={1.8}
                        aria-hidden="true"
                      />
                    )}
                  </button>
                </div>
              </div>
            </div>

            <div className="form-notice">
              <Info
                className="form-notice__icon"
                size={20}
                strokeWidth={1.8}
                aria-hidden="true"
              />

              <p>
                Todos los campos marcados con <strong>*</strong> son
                obligatorios.
                <br />
                Si no deseas cambiar tu contraseña, deja estos campos en
                blanco.
              </p>
            </div>

            <div className="create-form__actions">
              <button
                type="submit"
                className="create-form__button create-form__button--primary"
              >
                Guardar cambios
              </button>

              <button
                type="button"
                className="create-form__button create-form__button--secondary"
                onClick={() => navigate(`/usuarios/${userId}`)}
              >
                Cancelar
              </button>
            </div>
          </section>
        </div>
      </form>
    </div>
  );
}

export default EditarUsuarioPage;