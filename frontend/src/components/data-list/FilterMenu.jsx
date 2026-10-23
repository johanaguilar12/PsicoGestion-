function FilterMenu({ value, onChange, onClose }) {
  const options = [
    { value: "Todos", label: "Todos" },
    { value: "Activo", label: "Activos" },
    { value: "Inactivo", label: "Inactivos" },
  ];

  const handleChange = (status) => {
    onChange(status);
    onClose();
  };

  return (
    <>
      <button
        type="button"
        className="data-overlay"
        onClick={onClose}
        aria-label="Cerrar filtros"
      />

      <div className="filter-menu" role="dialog" aria-label="Filtros">
        <p className="filter-menu__title">Filtrar por estatus</p>

        {options.map((option) => (
          <label className="filter-menu__option" key={option.value}>
            <input
              type="radio"
              name="status-filter"
              value={option.value}
              checked={value === option.value}
              onChange={() => handleChange(option.value)}
            />
            <span>{option.label}</span>
          </label>
        ))}
      </div>
    </>
  );
}

export default FilterMenu;
