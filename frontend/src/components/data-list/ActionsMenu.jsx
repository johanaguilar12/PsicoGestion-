import { useEffect } from "react";
import { Power, Trash2 } from "lucide-react";

function ActionsMenu({
  item,
  position,
  onToggleStatus,
  onDelete,
  onClose,
}) {
  useEffect(() => {
    if (!item) return undefined;

    const previousOverflow = document.body.style.overflow;
    const previousPaddingRight = document.body.style.paddingRight;
    const scrollbarWidth =
      window.innerWidth - document.documentElement.clientWidth;

    document.body.style.overflow = "hidden";

    if (scrollbarWidth > 0) {
      document.body.style.paddingRight = `${scrollbarWidth}px`;
    }

    const handleKeyDown = (event) => {
      if (event.key === "Escape") {
        onClose();
      }
    };

    window.addEventListener("keydown", handleKeyDown);

    return () => {
      document.body.style.overflow = previousOverflow;
      document.body.style.paddingRight = previousPaddingRight;
      window.removeEventListener("keydown", handleKeyDown);
    };
  }, [item, onClose]);

  if (!item) return null;

  const normalizedStatus = item.status?.toLowerCase();
  const isActive =
    normalizedStatus === "activo" ||
    normalizedStatus === "activa";

  const handleToggleStatus = () => {
    onClose();
    onToggleStatus(item);
  };

  const handleDelete = () => {
    onClose();
    onDelete(item);
  };

  return (
    <>
      <button
        type="button"
        className="actions-menu__backdrop"
        onClick={onClose}
        aria-label="Cerrar menú de acciones"
      />

      <div
        className="actions-menu"
        style={{
          top: position.top,
          left: position.left,
        }}
        role="menu"
        aria-label="Acciones"
      >
        <button
          type="button"
          className="actions-menu__item"
          onClick={handleToggleStatus}
          role="menuitem"
        >
          <Power
            size={18}
            strokeWidth={1.8}
            aria-hidden="true"
          />
          <span>
            {isActive ? "Desactivar" : "Activar"}
          </span>
        </button>

        <button
          type="button"
          className="actions-menu__item actions-menu__item--danger"
          onClick={handleDelete}
          role="menuitem"
        >
          <Trash2
            size={18}
            strokeWidth={1.8}
            aria-hidden="true"
          />
          <span>Eliminar</span>
        </button>
      </div>
    </>
  );
}

export default ActionsMenu;