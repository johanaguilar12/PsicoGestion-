
import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { apiFetch } from "../services/api";

import ConfirmDialog from "../components/data-list/ConfirmDialog";
import Breadcrumb from "../components/forms/Breadcrumb";
import DetailField from "../components/forms/DetailField";
import DetailHeader from "../components/forms/DetailHeader";

function SalaDetallePage() {
  const navigate = useNavigate();
  const { roomId } = useParams();

  const [sala, setSala] = useState(null);
  const [cargando, setCargando] = useState(true);
  const [confirmDelete, setConfirmDelete] = useState(false);
  const [errorMessage, setErrorMessage] = useState(null);

  useEffect(() => {
    const cargarDetalle = async () => {
      try {
        const response = await apiFetch(
          `http://localhost:8081/api/v1/salas/${roomId}`
        );

        if (response.ok) {
          const data = await response.json();

          setSala({
            id: data.idSala,
            name: data.nombreSala,
          });
        }
      } catch (error) {
        console.error("Error al obtener la sala:", error);
      } finally {
        setCargando(false);
      }
    };

    cargarDetalle();
  }, [roomId]);

  const handleDelete = async () => {
    try {
      setErrorMessage(null);

      const response = await apiFetch(
        `http://localhost:8081/api/v1/salas/${roomId}`,
        { method: "DELETE" }
      );

      if (!response.ok) {
        throw new Error(await response.text());
      }

      setConfirmDelete(false);
      navigate("/salas");
    } catch (error) {
      setConfirmDelete(false);
      setErrorMessage(error.message || "No se pudo eliminar la sala.");
    }
  };

  if (cargando) {
    return (
      <div className="create-page detail-page">
        <h1 className="page-title">Cargando sala...</h1>
      </div>
    );
  }

  if (!sala) {
    return (
      <div className="create-page detail-page">
        <h1 className="page-title">Error: Sala no encontrada</h1>
      </div>
    );
  }

  return (
    <div className="create-page detail-page">
      <Breadcrumb
        items={[
          { label: "Salas", to: "/salas" },
          { label: sala.name },
        ]}
      />

      <DetailHeader
        title="Detalle sala"
        onEdit={() => navigate(`/salas/${roomId}/editar`)}
        onMenu={() => setConfirmDelete(true)}
        deleteOnly
      />

      {errorMessage && (
        <div
          role="alert"
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

      <section className="create-form detail-card">
        <div className="create-form__content">
          <section className="create-section">
            <h2 className="create-section__title">
              Información de la sala
            </h2>

            <DetailField
              label="Nombre de la sala"
              value={sala.name}
            />
          </section>
        </div>
      </section>

      <ConfirmDialog
        open={confirmDelete}
        title="Eliminar sala"
        message={`¿Deseas eliminar ${sala.name}? Esta acción no se puede deshacer.`}
        confirmLabel="Eliminar"
        danger
        onConfirm={handleDelete}
        onCancel={() => setConfirmDelete(false)}
      />
    </div>
  );
}

export default SalaDetallePage;
