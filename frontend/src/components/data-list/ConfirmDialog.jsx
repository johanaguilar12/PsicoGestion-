import { AlertTriangle, X } from "lucide-react";

function ConfirmDialog({
  open,
  title,
  message,
  confirmLabel = "Confirmar",
  danger = false,
  onConfirm,
  onCancel,
}) {
  if (!open) return null;

  return (
    <div
      className="confirm-dialog"
      role="dialog"
      aria-modal="true"
      aria-labelledby="confirm-dialog-title"
    >
      <button
        type="button"
        className="confirm-dialog__backdrop"
        onClick={onCancel}
        aria-label="Cerrar confirmación"
      />

      <div className="confirm-dialog__card">
        <div className="confirm-dialog__header">
          <div className="confirm-dialog__icon">
            <AlertTriangle size={22} strokeWidth={1.8} aria-hidden="true" />
          </div>

          <button
            type="button"
            className="confirm-dialog__close"
            onClick={onCancel}
            aria-label="Cerrar"
          >
            <X size={20} strokeWidth={1.8} aria-hidden="true" />
          </button>
        </div>

        <h2 id="confirm-dialog-title" className="confirm-dialog__title">
          {title}
        </h2>

        <p className="confirm-dialog__message">{message}</p>

        <div className="confirm-dialog__actions">
          <button
            type="button"
            className="confirm-dialog__button confirm-dialog__button--secondary"
            onClick={onCancel}
          >
            Cancelar
          </button>

          <button
            type="button"
            className={`confirm-dialog__button ${
              danger
                ? "confirm-dialog__button--danger"
                : "confirm-dialog__button--primary"
            }`}
            onClick={onConfirm}
          >
            {confirmLabel}
          </button>
        </div>
      </div>
    </div>
  );
}

export default ConfirmDialog;
