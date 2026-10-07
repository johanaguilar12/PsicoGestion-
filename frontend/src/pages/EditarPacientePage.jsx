import { useState } from "react";
import { Info } from "lucide-react";
import { useNavigate, useParams } from "react-router-dom";

import Breadcrumb from "../components/forms/Breadcrumb";
import FormField from "../components/forms/FormField";

function EditarPacientePage() {
  const navigate = useNavigate();
  const { patientId } = useParams();

  const [formData, setFormData] = useState({
    nombre: "Raúl Diego Pinto López",
    curp: "PILR030824HYNRPS06",
    fechaNacimiento: "2003-08-24",
    sexo: "Hombre",
    correo: "19186424@alumnos.uady.mx",
    telefono: "999 653 4988",
  });

  const handleChange = (event) => {
    const { name, value } = event.target;

    setFormData((current) => ({
      ...current,
      [name]: value,
    }));
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    navigate(`/pacientes/${patientId}`);
  };

  return (
    <div className="create-page">
      <Breadcrumb
        items={[
          {
            label: "Pacientes",
            to: "/pacientes",
          },
          {
            label: "Raúl Diego Pinto López",
            to: `/pacientes/${patientId}`,
          },
          {
            label: "Editar paciente",
          },
        ]}
      />

      <h1 className="page-title create-page__title">
        Editar paciente
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

            <div className="form-grid form-grid--patient">
              <FormField
                label="Nombre completo"
                name="nombre"
                value={formData.nombre}
                onChange={handleChange}
                autoComplete="name"
                required
              />

              <FormField
                label="CURP"
                name="curp"
                value={formData.curp}
                onChange={handleChange}
                required
              />

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

              <FormField
                label="Correo institucional"
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
                  navigate(`/pacientes/${patientId}`)
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

export default EditarPacientePage;