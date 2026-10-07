import { useState } from "react";
import { Info } from "lucide-react";
import { useNavigate, useParams } from "react-router-dom";

import Breadcrumb from "../components/forms/Breadcrumb";

function EditarSalaPage() {
  const navigate = useNavigate();
  const { roomId } = useParams();

  const [nombre, setNombre] = useState("Sala 2");

  const handleSubmit = (event) => {
    event.preventDefault();
    navigate(`/salas/${roomId}`);
  };

  return (
    <div className="create-page">
      <Breadcrumb
        items={[
          { label: "Salas", to: "/salas" },
          { label: "Sala 2", to: `/salas/${roomId}` },
          { label: "Editar sala" },
        ]}
      />

      <h1 className="page-title create-page__title">
        Editar sala
      </h1>

      <form className="create-form" onSubmit={handleSubmit}>
        <div className="create-form__content">
          <section className="create-section">
            <h2 className="create-section__title">
              Información de la sala
            </h2>

            <div className="form-field">
              <label className="form-field__label" htmlFor="nombre">
                Nombre de la sala{" "}
                <span className="form-field__required">*</span>
              </label>

              <div className="form-field__control">
                <input
                  id="nombre"
                  name="nombre"
                  type="text"
                  className="form-field__input"
                  value={nombre}
                  onChange={(event) => setNombre(event.target.value)}
                  required
                />
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
                onClick={() => navigate(`/salas/${roomId}`)}
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

export default EditarSalaPage;