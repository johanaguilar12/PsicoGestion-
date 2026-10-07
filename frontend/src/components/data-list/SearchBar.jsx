import { Search, X } from "lucide-react";

function SearchBar({ placeholder, value, onChange }) {
  return (
    <div className="data-search">
      <Search className="data-search__icon" size={20} strokeWidth={1.8} aria-hidden="true" />

      <input
        type="search"
        className="data-search__input"
        placeholder={placeholder}
        value={value}
        onChange={(event) => onChange(event.target.value)}
        aria-label={placeholder}
      />

      {value && (
        <button
          type="button"
          className="data-search__clear"
          onClick={() => onChange("")}
          aria-label="Limpiar búsqueda"
        >
          <X size={18} strokeWidth={1.8} aria-hidden="true" />
        </button>
      )}
    </div>
  );
}

export default SearchBar;
