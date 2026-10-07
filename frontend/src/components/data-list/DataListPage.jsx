import { useMemo, useState } from "react";
import { Filter, Plus } from "lucide-react";
import { useNavigate } from "react-router-dom";

import ActionsMenu from "./ActionsMenu";
import ConfirmDialog from "./ConfirmDialog";
import DataTable from "./DataTable";
import FilterMenu from "./FilterMenu";
import MobileDataList from "./MobileDataList";
import SearchBar from "./SearchBar";

const normalizeText = (value) =>
  String(value ?? "")
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .toLowerCase()
    .trim();

const onlyDigits = (value) => String(value ?? "").replace(/\D/g, "");

function matchesSearch(item, searchKeys, query) {
  const normalizedQuery = normalizeText(query);

  if (!normalizedQuery) return true;

  const digitQuery = onlyDigits(query);
  const numericQuery =
    digitQuery.length > 0 && /^[\d\s-]+$/.test(query.trim());

  return searchKeys.some((key) => {
    const value = item[key];

    if (numericQuery) {
      return onlyDigits(value).includes(digitQuery);
    }

    return normalizeText(value).includes(normalizedQuery);
  });
}

function getMenuPosition(target) {
  const rect = target.getBoundingClientRect();
  const menuWidth = 190;
  const menuHeight = 104;
  const margin = 12;

  const left = Math.min(
    Math.max(margin, rect.right - menuWidth),
    window.innerWidth - menuWidth - margin,
  );

  const preferredTop = rect.bottom + 8;
  const top =
    preferredTop + menuHeight <= window.innerHeight - margin
      ? preferredTop
      : Math.max(margin, rect.top - menuHeight - 8);

  return { top, left };
}

function DataListPage({
  title,
  searchPlaceholder,
  createLabel,
  onCreate,
  columns,
  data,
  getMobileFields,
  tableVariant,
  basePath,
  searchKeys,
}) {
  const navigate = useNavigate();
  const [items, setItems] = useState(data);
  const [query, setQuery] = useState("");
  const [statusFilter, setStatusFilter] = useState("Todos");
  const [filterOpen, setFilterOpen] = useState(false);
  const [menuItem, setMenuItem] = useState(null);
  const [menuPosition, setMenuPosition] = useState({ top: 0, left: 0 });
  const [pendingDelete, setPendingDelete] = useState(null);

  const filteredItems = useMemo(
    () =>
      items.filter((item) => {
        const matchesStatus =
          statusFilter === "Todos" || item.status === statusFilter;

        return matchesStatus && matchesSearch(item, searchKeys, query);
      }),
    [items, query, searchKeys, statusFilter],
  );

  const handleView = (item) => navigate(`${basePath}/${item.id}`);
  const handleEdit = (item) => navigate(`${basePath}/${item.id}/editar`);

  const handleMenu = (event, item) => {
    setMenuPosition(getMenuPosition(event.currentTarget));
    setMenuItem(item);
  };

  const handleToggleStatus = (item) => {
    setItems((currentItems) =>
      currentItems.map((currentItem) =>
        currentItem.id === item.id
          ? {
              ...currentItem,
              status:
                currentItem.status === "Activo" ? "Inactivo" : "Activo",
            }
          : currentItem,
      ),
    );
    setMenuItem(null);
  };

  const handleDeleteConfirm = () => {
    setItems((currentItems) =>
      currentItems.filter((item) => item.id !== pendingDelete.id),
    );
    setPendingDelete(null);
  };

  return (
    <section className="data-page">
      <div className="data-page__controls">
        <h1 className="page-title data-page__title">{title}</h1>

        <button
          type="button"
          className="data-page__create"
          onClick={onCreate}
        >
          <Plus size={20} strokeWidth={2} aria-hidden="true" />
          <span>{createLabel}</span>
        </button>

        <SearchBar
          placeholder={searchPlaceholder}
          value={query}
          onChange={setQuery}
        />

        <div className="data-filter">
          <button
            type="button"
            className={`data-page__filter ${
              statusFilter !== "Todos" ? "data-page__filter--active" : ""
            }`}
            onClick={() => setFilterOpen((open) => !open)}
            aria-expanded={filterOpen}
            aria-haspopup="dialog"
          >
            <Filter size={20} strokeWidth={1.8} aria-hidden="true" />
            <span>{statusFilter === "Todos" ? "Filtros" : statusFilter}</span>
          </button>

          {filterOpen && (
            <FilterMenu
              value={statusFilter}
              onChange={setStatusFilter}
              onClose={() => setFilterOpen(false)}
            />
          )}
        </div>
      </div>

      {filteredItems.length > 0 ? (
        <>
          <div className="data-page__desktop">
            <DataTable
              columns={columns}
              data={filteredItems}
              variant={tableVariant}
              onView={handleView}
              onEdit={handleEdit}
              onMenu={handleMenu}
            />
          </div>

          <div className="data-page__mobile">
            <MobileDataList
              data={filteredItems}
              getFields={getMobileFields}
              onView={handleView}
              onEdit={handleEdit}
              onMenu={handleMenu}
            />
          </div>
        </>
      ) : (
        <div className="data-empty" role="status">
          No se encontraron resultados con los criterios seleccionados.
        </div>
      )}

      <ActionsMenu
        item={menuItem}
        position={menuPosition}
        onToggleStatus={handleToggleStatus}
        onDelete={(item) => {
          setMenuItem(null);
          setPendingDelete(item);
        }}
        onClose={() => setMenuItem(null)}
      />

      <ConfirmDialog
        open={Boolean(pendingDelete)}
        title="Eliminar registro"
        message={
          pendingDelete
            ? `¿Deseas eliminar a ${pendingDelete.name}? Esta acción no se puede deshacer.`
            : ""
        }
        confirmLabel="Eliminar"
        danger
        onConfirm={handleDeleteConfirm}
        onCancel={() => setPendingDelete(null)}
      />
    </section>
  );
}

export default DataListPage;
