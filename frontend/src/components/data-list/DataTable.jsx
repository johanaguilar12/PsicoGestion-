import RowActions from "./RowActions";
import StatusBadge from "./StatusBadge";

function DataTable({ columns, data, variant, onView, onEdit, onMenu }) {
  const tableClassName = [
    "data-table",
    variant ? `data-table--${variant}` : "",
  ]
    .filter(Boolean)
    .join(" ");

  return (
    <div className="data-table-wrapper">
      <table className={tableClassName}>
        <thead>
          <tr>
            {columns.map((column) => (
              <th
                key={column.key}
                scope="col"
                className={
                  column.key === "status"
                    ? "data-table__status-heading"
                    : undefined
                }
              >
                {column.label}
              </th>
            ))}
            <th scope="col" className="data-table__actions-heading">
              Acciones
            </th>
          </tr>
        </thead>

        <tbody>
          {data.map((item) => (
            <tr key={item.id}>
              {columns.map((column) => (
                <td
                  key={column.key}
                  className={
                    column.key === "status"
                      ? "data-table__status-cell"
                      : undefined
                  }
                >
                  {column.key === "status" ? (
                    <StatusBadge status={item.status} />
                  ) : (
                    item[column.key]
                  )}
                </td>
              ))}

              <td className="data-table__actions-cell">
                <RowActions
                  item={item}
                  showView={item.showView !== false}
                  onView={onView}
                  onEdit={onEdit}
                  onMenu={onMenu}
                />
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default DataTable;
