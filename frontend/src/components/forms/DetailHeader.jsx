
import { MoreVertical, Pencil, Trash2 } from "lucide-react";

function DetailHeader({ title, onEdit, onMenu, deleteOnly = false }) {
  return (
    <div className="detail-header">
      <h1 className="page-title create-page__title detail-header__title">
        {title}
      </h1>

      <div className="detail-header__actions">
        <button
          type="button"
          className="detail-header__edit"
          onClick={onEdit}
        >
          <Pencil
            className="detail-header__edit-icon"
            size={24}
            strokeWidth={1.8}
            aria-hidden="true"
          />
          <span>Editar</span>
        </button>

        <button
          type="button"
          className={`detail-header__menu ${deleteOnly ? "row-actions__button--delete" : ""}`}
          onClick={onMenu}
          aria-label={deleteOnly ? "Eliminar sala" : "Más acciones"}
          title={deleteOnly ? "Eliminar sala" : "Más acciones"}
          aria-haspopup={deleteOnly ? undefined : "menu"}
        >
          {deleteOnly ? (
            <Trash2
              size={24}
              strokeWidth={1.8}
              aria-hidden="true"
            />
          ) : (
            <MoreVertical
              size={24}
              strokeWidth={1.8}
              aria-hidden="true"
            />
          )}
        </button>
      </div>
    </div>
  );
}

export default DetailHeader;
