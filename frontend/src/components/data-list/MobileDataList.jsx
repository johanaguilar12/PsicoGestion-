import { Eye, MoreVertical, Pencil } from "lucide-react";
import StatusBadge from "./StatusBadge";

function MobileDataList({
  data,
  getFields,
  onView,
  onEdit,
  onMenu,
}) {
  return (
    <div className="mobile-data-list">
      {data.map((item) => {
        const fields = getFields(item);

        return (
          <article className="mobile-data-card" key={item.id}>
            <div className="mobile-data-card__content">
              <p className="mobile-data-card__name">
                {fields.title}
              </p>

              {fields.details.map((detail) => (
                <p
                  className="mobile-data-card__detail"
                  key={detail}
                >
                  {detail}
                </p>
              ))}
            </div>

            <div className="mobile-data-card__footer">
              <StatusBadge status={item.status} />

              <div className="mobile-data-card__actions">
                {item.showView !== false && (
                  <button
                    type="button"
                    className="mobile-data-card__action"
                    onClick={() => onView(item)}
                    aria-label={`Ver detalle de ${fields.title}`}
                  >
                    <Eye
                      size={22}
                      strokeWidth={1.8}
                      aria-hidden="true"
                    />
                  </button>
                )}

                <button
                  type="button"
                  className="mobile-data-card__action"
                  onClick={() => onEdit(item)}
                  aria-label={`Editar ${fields.title}`}
                >
                  <Pencil
                    size={21}
                    strokeWidth={1.8}
                    aria-hidden="true"
                  />
                </button>

                <button
                  type="button"
                  className="mobile-data-card__action"
                  onClick={(event) => onMenu(event, item)}
                  aria-label={`Más acciones para ${fields.title}`}
                  aria-haspopup="menu"
                >
                  <MoreVertical
                    size={24}
                    strokeWidth={1.8}
                    aria-hidden="true"
                  />
                </button>
              </div>
            </div>
          </article>
        );
      })}
    </div>
  );
}

export default MobileDataList;