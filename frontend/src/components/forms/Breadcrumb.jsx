import { ChevronRight } from "lucide-react";
import { Link } from "react-router-dom";

function Breadcrumb({ items = [] }) {
  return (
    <nav
      className="breadcrumb"
      aria-label="Ruta de navegación"
    >
      <ol className="breadcrumb__list">
        {items.map((item, index) => {
          const isLast = index === items.length - 1;

          return (
            <li
              className="breadcrumb__item"
              key={`${item.label}-${index}`}
            >
              {index > 0 && (
                <ChevronRight
                  className="breadcrumb__separator"
                  size={18}
                  strokeWidth={1.8}
                  aria-hidden="true"
                />
              )}

              {item.to && !isLast ? (
                <Link
                  to={item.to}
                  className="breadcrumb__link"
                >
                  {item.label}
                </Link>
              ) : (
                <span
                  className="breadcrumb__current"
                  aria-current={isLast ? "page" : undefined}
                >
                  {item.label}
                </span>
              )}
            </li>
          );
        })}
      </ol>
    </nav>
  );
}

export default Breadcrumb;