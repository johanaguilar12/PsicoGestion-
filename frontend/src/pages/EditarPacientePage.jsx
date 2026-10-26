import { useState, useEffect } from "react";
import { Info } from "lucide-react";
import { useNavigate, useParams } from "react-router-dom";
import { apiFetch } from "../services/api";

import Breadcrumb from "../components/forms/Breadcrumb";
import FormField from "../components/forms/FormField";

function EditarPacientePage() {
  const navigate = useNavigate();
  const { patientId } = useParams();

  const [formData, setFormData] = useState({
    nombre: "",
    curp: "",
    fechaNacimiento: "",
    sexo: "",
    correo: "",
    telefono: "",
  });
  
  const [cargando, setCargando] = useState(true);
  const [errorMessage, setErrorMessage] = useState(null);

  
  useEffect(() => {
    const cargarPaciente = async () => {
      try {
        const response = await apiFetch(`http://localhost:8081/api/v1/pacientes/${patientId}`);
        if (response.ok) {
          const data = await response.json();
          setFormData({
            nombre: data.nombreCompleto || "",
            curp: data.curp || "",
            fechaNacimiento: data.fechaNacimiento || "",
            sexo: data.sexo === "F" ? "Mujer" : "Hombre",
            correo: "No registrado", 
            telefono: data.telefono || "",
          });
        } else {
          setErrorMessage("No se pudo cargar la información del paciente.");
        }
      } catch (error) {
        setErrorMessage("Error de conexión al cargar el paciente.");
      } finally {
        setCargando(false);
      }
    };

    cargarPaciente();
  }, [patientId]);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setFormData((current) => ({
      ...current,
      [name]: value,
    }));
  };

  
  const handleSubmit = async (event) => {
    event.preventDefault();
    setErrorMessage(null);

    const pacienteActualizado = {
      idPaciente: patientId, 
      nombreCompleto: formData.nombre,
      curp: formData.curp,
      sexo: formData.sexo === "Mujer" ? "F" : "M",
      fechaNacimiento: formData.fechaNacimiento,
      telefono: formData.telefono
    };

    try {
      
      const response = await apiFetch(`http://localhost:8081/api/v1/pacientes/${patientId}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(pacienteActualizado),
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText);
      }

      
      navigate(`/pacientes/${patientId}`);
    } catch (error) {
      setErrorMessage(error.message);
    }
  };

  if (cargando) {
    return (
      <div className="create-page">
        <h1 className="page-title">Cargando datos del paciente...</h1>
      </div>
    );
  }

  return (
    <div className="create-page">
      <Breadcrumb
        items={[
          { label: "Pacientes", to: "/pacientes" },
          { label: formData.nombre || "Detalle", to: `/pacientes/${patientId}` },
          { label: "Editar paciente" },
        ]}
      />

      <h1 className="page-title create-page__title">Editar paciente</h1>

      <form className="create-form" onSubmit={handleSubmit}>
        <div className="create-form__content">
          <section className="create-section">
            <h2 className="create-section__title">Información personal</h2>

            {errorMessage && (
              <div style={{ backgroundColor: "#fee2e2", color: "#991b1b", padding: "1rem", borderRadius: "8px", marginBottom: "1.5rem" }}>
                <strong>Error:</strong> {errorMessage}
              </div>
            )}

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
                  { value: "Mujer", label: "Mujer" },
                  { value: "Hombre", label: "Hombre" },
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
              <Info className="form-notice__icon" size={20} strokeWidth={1.8} aria-hidden="true" />
              <p>Todos los campos marcados con <strong>*</strong> son obligatorios.</p>
            </div>

            <div className="create-form__actions">
              <button type="submit" className="create-form__button create-form__button--primary">
                Guardar cambios
              </button>

              <button
                type="button"
                className="create-form__button create-form__button--secondary"
                onClick={() => navigate(`/pacientes/${patientId}`)}
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