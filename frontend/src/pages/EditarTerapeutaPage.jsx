import { useState } from "react";
import { Info } from "lucide-react";
import { useNavigate, useParams } from "react-router-dom";

import Breadcrumb from "../components/forms/Breadcrumb";
import FormField from "../components/forms/FormField";

function EditarTerapeutaPage() {
  const navigate = useNavigate();
  const { therapistId } = useParams();

  const [formData, setFormData] = useState({
    nombre: "Francisco Suárez León",
    correo: "ejemplo@correo.uady.mx",
    telefono: "999 245 4554",
    fechaNacimiento: "1970-03-03",
    sexo: "Hombre",
    cedula: "45646797",
  });

  const [photoPreview, setPhotoPreview] = useState(
    "/images/terapeuta-ejemplo.jpg"
  );

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
    navigate(`/terapeutas/${therapistId}`);
  };

  return (
    <div className="create-page">
      <Breadcrumb
        items={[
          {
            label: "Terapeutas",
            to: "/terapeutas",
          },
          {
            label: "Francisco Suárez León",
            to: `/terapeutas/${therapistId}`,
          },
          {
            label: "Editar terapeuta",
          },
        ]}
      />

      <h1 className="page-title create-page__title">
        Editar terapeuta
      </h1>

      <form
        className="create-form"
        onSubmit={handleSubmit}
      >
        <div className="create-form__content">
          <section className="create-section">
            <h2 className="create-section__title">
              Información personal
            </h2>

            <div className="create-profile-layout">
              <div className="create-profile-layout__fields">
                <FormField
                  label="Nombre completo"
                  name="nombre"
                  value={formData.nombre}
                  onChange={handleChange}
                  autoComplete="name"
                  required
                />

                <FormField
                  label="Correo electrónico"
                  name="correo"
                  type="email"
                  value={formData.correo}
                  onChange={handleChange}
                  autoComplete="email"
                  required
                />

                <FormField
                  label="Teléfono"
                  name="telefono"
                  type="tel"
                  value={formData.telefono}
                  onChange={handleChange}
                  autoComplete="tel"
                  required
                />
              </div>

              <div className="create-profile-layout__photo">
                <div className="photo-upload">
                  <img
                    src={photoPreview}
                    alt="Fotografía del terapeuta"
                    className="photo-upload__preview photo-upload__preview--image"
                  />

                  <input
                    id="foto-terapeuta"
                    type="file"
                    className="photo-upload__input"
                    accept=".jpg,.jpeg,.png,image/jpeg,image/png"
                    onChange={handlePhotoChange}
                  />

                  <label
                    htmlFor="foto-terapeuta"
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

            <div className="form-grid form-grid--two">
              <FormField
                label="Fecha de nacimiento"
                name="fechaNacimiento"
                type="date"
                value={formData.fechaNacimiento}
                onChange={handleChange}
                autoComplete="bday"
                required
              />

              <FormField
                label="Sexo"
                name="sexo"
                type="select"
                placeholder="Selecciona una opción"
                value={formData.sexo}
                onChange={handleChange}
                options={[
                  {
                    value: "Mujer",
                    label: "Mujer",
                  },
                  {
                    value: "Hombre",
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
                name="cedula"
                value={formData.cedula}
                onChange={handleChange}
                required
              />
            </div>

            <div className="form-notice">
              <Info
                className="form-notice__icon"
                size={20}
                strokeWidth={1.8}
                aria-hidden="true"
              />

              <p>
                Todos los campos marcados con{" "}
                <strong>*</strong> son obligatorios.
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
                onClick={() =>
                  navigate(`/terapeutas/${therapistId}`)
                }
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

export default EditarTerapeutaPage;