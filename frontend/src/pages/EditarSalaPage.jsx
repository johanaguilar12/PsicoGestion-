import { useState, useEffect } from "react";
import { Info } from "lucide-react";
import { useNavigate, useParams } from "react-router-dom";
import Breadcrumb from "../components/forms/Breadcrumb";

function EditarSalaPage() {
  const navigate = useNavigate();
  const { roomId } = useParams();

  const [nombre, setNombre] = useState("");
  const [cargando, setCargando] = useState(true);
  const [errorMessage, setErrorMessage] = useState(null);

  useEffect(() => {
    const cargarDatos = async () => {
      try {
        const response = await fetch(`http://localhost:8081/api/v1/salas/${roomId}`);
        if (response.ok) {
          const data = await response.json();
          setNombre(data.nombreSala);
        }
      } catch (error) {
        setErrorMessage("Error al cargar datos de la sala.");
      } finally {
        setCargando(false);
      }
    };
    cargarDatos();
  }, [roomId]);

  const handleSubmit = async (event) => {
    event.preventDefault();
    setErrorMessage(null);

    const payload = { idSala: roomId, nombreSala: nombre };

    try {
      const response = await fetch(`http://localhost:8081/api/v1/salas/${roomId}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText);
      }
      navigate(`/salas/${roomId}`);
    } catch (error) {
      setErrorMessage(error.message);
    }
  };

  if (cargando) return <div className="create-page"><h1 className="page-title">Cargando datos...</h1></div>;

  return (
    <div className="create-page">
      <Breadcrumb items={[{ label: "Salas", to: "/salas" }, { label: nombre, to: `/salas/${roomId}` }, { label: "Editar sala" }]} />
      <h1 className="page-title create-page__title">Editar sala</h1>

      <form className="create-form" onSubmit={handleSubmit}>
        <div className="create-form__content">
          <section className="create-section">
            <h2 className="create-section__title">Información de la sala</h2>
            {errorMessage && (
              <div style={{ backgroundColor: "#fee2e2", color: "#991b1b", padding: "1rem", borderRadius: "8px", marginBottom: "1.5rem" }}>
                <strong>Error:</strong> {errorMessage}
              </div>
            )}
            <div className="form-field">
              <label className="form-field__label" htmlFor="nombre">
                Nombre de la sala <span className="form-field__required">*</span>
              </label>
              <div className="form-field__control">
                <input
                  id="nombre"
                  name="nombre"
                  type="text"
                  className="form-field__input"
                  value={nombre}
                  onChange={(event) => setNombre(event.target.value)}
                  maxLength={10} 
                  required
                />
              </div>
            </div>

            <div className="form-notice">
              <Info className="form-notice__icon" size={20} strokeWidth={1.8} aria-hidden="true" />
              <p>Todos los campos marcados con <strong>*</strong> son obligatorios.</p>
            </div>

            <div className="create-form__actions">
              <button type="submit" className="create-form__button create-form__button--primary">Guardar cambios</button>
              <button type="button" className="create-form__button create-form__button--secondary" onClick={() => navigate(`/salas/${roomId}`)}>Cancelar</button>
            </div>
          </section>
        </div>
      </form>
    </div>
  );
}

export default EditarSalaPage;