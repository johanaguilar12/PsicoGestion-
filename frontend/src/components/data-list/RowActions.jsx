import { Eye, MoreVertical, Pencil } from "lucide-react";

function RowActions({ item, showView = true, onView, onEdit, onMenu }) {
  return (
    <div className="row-actions">
      {showView && (
        <button
          type="button"
          className="row-actions__button"
          onClick={() => onView(item)}
          aria-label={`Ver detalle de ${item.name}`}
        >
          <Eye size={20} strokeWidth={1.8} aria-hidden="true" />
        </button>
      )}

      <button
        type="button"
        className="row-actions__button"
        onClick={() => onEdit(item)}
        aria-label={`Editar ${item.name}`}
      >
        <Pencil size={20} strokeWidth={1.8} aria-hidden="true" />
      </button>

      <button
        type="button"
        className="row-actions__button"
        onClick={(event) => onMenu(event, item)}
        aria-label={`Más acciones para ${item.name}`}
        aria-haspopup="menu"
      >
        <MoreVertical size={20} strokeWidth={1.8} aria-hidden="true" />
      </button>
    </div>
  );
}

export default RowActions;
