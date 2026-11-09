
import { MoreVertical } from "lucide-react";
import StatusBadge from "./StatusBadge";
import RowActions from "./RowActions";

function MobileDataList({
  data,
  getFields,
  onView,
  onEdit,
  onMenu,
  deleteOnly = false,
  onDelete,
}) {
  return (
    <div className="mobile-data-list">
      {data.map((item) => {
        const fields = getFields(item);

        return (
          <article
            className={`mobile-data-card${deleteOnly ? " mobile-data-card--rooms" : ""}`}
            key={item.id}
          >
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

            <div className="mobile-data-card__actions">
              {deleteOnly ? (
                <RowActions
                  item={item}
                  showView={item.showView !== false}
                  onView={onView}
                  onEdit={onEdit}
                  onMenu={onMenu}
                  deleteOnly={deleteOnly}
                  onDelete={onDelete}
                />
              ) : (
                <>
                  {item.status && (
                    <StatusBadge status={item.status} />
                  )}

                  <button
                    type="button"
                    className="mobile-data-card__menu"
                    aria-label={`Más acciones para ${fields.title}`}
                  >
                    <MoreVertical
                      size={24}
                      strokeWidth={1.8}
                      aria-hidden="true"
                    />
                  </button>
                </>
              )}
            </div>
          </article>
        );
      })}
    </div>
  );
}

export default MobileDataList;
