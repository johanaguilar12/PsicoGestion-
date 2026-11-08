
import { useState, useEffect } from "react";
import { Info, CircleUserRound } from "lucide-react";
import { useNavigate, useParams } from "react-router-dom";
import { apiFetch } from "../services/api";

import Breadcrumb from "../components/forms/Breadcrumb";
import FormField from "../components/forms/FormField";

function EditarTerapeutaPage() {
  const navigate = useNavigate();
  const { therapistId } = useParams();

  const [formData, setFormData] = useState({
    nombre: "",
    correo: "",
    telefono: "",
    fechaNacimiento: "",
    sexo: "",
    cedula: "",
  });

  const [photoPreview, setPhotoPreview] = useState(null);
  const [selectedPhoto, setSelectedPhoto] = useState(null);
  const [photoError, setPhotoError] = useState(false);
  const [cargando, setCargando] = useState(true);
  const [errorMessage, setErrorMessage] = useState(null);

  useEffect(() => {
    const cargarDatos = async () => {
      try {
        const response = await apiFetch(
          `http://localhost:8081/api/v1/terapeutas/${therapistId}`
        );

        if (!response.ok) {
          throw new Error("No se pudieron cargar los datos del terapeuta.");
        }

        const data = await response.json();

        setFormData({
          nombre: data.nombreCompleto || "",
          correo: data.correoElectronico || "",
          telefono: data.telefono || "",
          fechaNacimiento: data.fechaNacimiento || "",
          sexo: data.sexo === "F" ? "Mujer" : "Hombre",
          cedula: data.cedulaProfesional || "",
        });

        setPhotoPreview(data.fotoPerfil || null);
        setPhotoError(false);
      } catch (error) {
        setErrorMessage("Error al cargar datos del terapeuta.");
      } finally {
        setCargando(false);
      }
    };

    cargarDatos();
  }, [therapistId]);

  useEffect(() => {
    return () => {
      if (selectedPhoto && photoPreview?.startsWith("blob:")) {
        URL.revokeObjectURL(photoPreview);
      }
    };
  }, [selectedPhoto, photoPreview]);

  const handleChange = (event) => {
    const { name, value } = event.target;

    setFormData((current) => ({
      ...current,
      [name]: value,
    }));
  };

  const handlePhotoChange = (event) => {
    const file = event.target.files?.[0];

    if (file) {
      setSelectedPhoto(file);
      setPhotoPreview(URL.createObjectURL(file));
      setPhotoError(false);
    }
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setErrorMessage(null);

    const datosActualizados = new FormData();

    datosActualizados.append("nombreCompleto", formData.nombre);
    datosActualizados.append("correoElectronico", formData.correo);
    datosActualizados.append("telefono", formData.telefono);
    datosActualizados.append("fechaNacimiento", formData.fechaNacimiento);
    datosActualizados.append(
      "sexo",
      formData.sexo === "Mujer" ? "F" : "M"
    );
    datosActualizados.append("cedulaProfesional", formData.cedula);

    if (selectedPhoto) {
      datosActualizados.append("foto", selectedPhoto);
    }

    try {
      const response = await apiFetch(
        `http://localhost:8081/api/v1/terapeutas/${therapistId}`,
        {
          method: "PUT",
          body: datosActualizados,
        }
      );

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText);
      }

      navigate(`/terapeutas/${therapistId}`);
    } catch (error) {
      setErrorMessage(error.message);
    }
  };

  if (cargando) {
    return (
      <div className="create-page">
        <h1 className="page-title">Cargando datos...</h1>
      </div>
    );
  }

  return (
    <div className="create-page">
      <Breadcrumb
        items={[
          { label: "Terapeutas", to: "/terapeutas" },
          {
            label: formData.nombre,
            to: `/terapeutas/${therapistId}`,
          },
          { label: "Editar terapeuta" },
        ]}
      />

      <h1 className="page-title create-page__title">
        Editar terapeuta
      </h1>

      <form className="create-form" onSubmit={handleSubmit}>
        <div className="create-form__content">
          <section className="create-section">
            <h2 className="create-section__title">
              Información personal
            </h2>

            {errorMessage && (
              <div
                style={{
                  backgroundColor: "#fee2e2",
                  color: "#991b1b",
                  padding: "1rem",
                  borderRadius: "8px",
                  marginBottom: "1.5rem",
                }}
              >
                <strong>Error:</strong> {errorMessage}
              </div>
            )}

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
                  {photoPreview && !photoError ? (
                    <img
                      src={photoPreview}
                      alt="Fotografía del terapeuta"
                      className="photo-upload__preview photo-upload__preview--image"
                      onError={() => setPhotoError(true)}
                    />
                  ) : (
                    <CircleUserRound
                      className="photo-upload__preview"
                      size={128}
                      strokeWidth={0.5}
                      aria-hidden="true"
                    />
                  )}

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
                    JPG, PNG, JPEG (máx. 1MB)
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
                value={formData.sexo}
                onChange={handleChange}
                options={[
                  { value: "Mujer", label: "Mujer" },
                  { value: "Hombre", label: "Hombre" },
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
                Todos los campos marcados con <strong>*</strong> son
                obligatorios.
              </p>
            </div>

            <div className="create-form__actions">
              <button
                type="button"
                className="create-form__button create-form__button--secondary"
                onClick={() =>
                  navigate(`/terapeutas/${therapistId}`)
                }
              >
                Cancelar
              </button>

              <button
                type="submit"
                className="create-form__button create-form__button--primary"
              >
                Guardar cambios
              </button>
            </div>
          </section>
        </div>
      </form>
    </div>
  );
}

export default EditarTerapeutaPage;
