import {
  CalendarDays,
  ChevronDown,
  Clock3,
  Eye,
  EyeOff,
} from "lucide-react";
import { useRef, useState } from "react";

function FormField({
  label,
  name,
  type = "text",
  placeholder = "",
  required = false,
  options = [],
  value,
  defaultValue,
  readOnly = false,
  onChange,
  autoComplete,
}) {
  const [showPassword, setShowPassword] = useState(false);
  const inputRef = useRef(null);

  const isPassword = type === "password";
  const isSelect = type === "select";
  const isDate = type === "date";
  const isTime = type === "time";
  const hasPickerIcon = isDate || isTime;

  const inputType =
    isPassword && showPassword ? "text" : type;

  const openPicker = () => {
    const input = inputRef.current;

    if (!input || readOnly) return;

    input.focus();

    if (typeof input.showPicker === "function") {
      try {
        input.showPicker();
      } catch {
        return;
      }
    }
  };

  return (
    <div className="form-field">
      <label
        className="form-field__label"
        htmlFor={name}
      >
        {label}

        {required && (
          <span
            className="form-field__required"
            aria-hidden="true"
          >
            {" "}*
          </span>
        )}
      </label>

      {isSelect ? (
        <div className="form-field__control">
          <select
            id={name}
            name={name}
            className="form-field__input form-field__select"
            value={value}
            defaultValue={
              value === undefined
                ? defaultValue ?? ""
                : undefined
            }
            onChange={onChange}
            required={required}
            disabled={readOnly}
            autoComplete={autoComplete}
          >
            <option value="" disabled>
              {placeholder}
            </option>

            {options.map((option) => (
              <option
                key={option.value}
                value={option.value}
              >
                {option.label}
              </option>
            ))}
          </select>

          <ChevronDown
            className="form-field__select-icon"
            size={20}
            strokeWidth={1.8}
            aria-hidden="true"
          />
        </div>
      ) : (
        <div className="form-field__control">
          <input
            ref={inputRef}
            id={name}
            name={name}
            type={inputType}
            className={[
              "form-field__input",
              isPassword
                ? "form-field__input--with-action"
                : "",
              hasPickerIcon
                ? "form-field__input--with-picker"
                : "",
            ]
              .filter(Boolean)
              .join(" ")}
            placeholder={placeholder}
            required={required}
            readOnly={readOnly}
            value={value}
            defaultValue={
              value === undefined
                ? defaultValue
                : undefined
            }
            onChange={onChange}
            autoComplete={autoComplete}
          />

          {hasPickerIcon && (
            <button
              type="button"
              className="form-field__picker"
              onClick={openPicker}
              aria-label={`Seleccionar ${label.toLowerCase()}`}
              tabIndex={-1}
            >
              {isDate ? (
                <CalendarDays
                  size={22}
                  strokeWidth={1.8}
                  aria-hidden="true"
                />
              ) : (
                <Clock3
                  size={22}
                  strokeWidth={1.8}
                  aria-hidden="true"
                />
              )}
            </button>
          )}

          {isPassword && (
            <button
              type="button"
              className="form-field__action"
              onClick={() =>
                setShowPassword(
                  (current) => !current
                )
              }
              aria-label={
                showPassword
                  ? "Ocultar contraseña"
                  : "Mostrar contraseña"
              }
            >
              {showPassword ? (
                <EyeOff
                  size={20}
                  strokeWidth={1.8}
                  aria-hidden="true"
                />
              ) : (
                <Eye
                  size={20}
                  strokeWidth={1.8}
                  aria-hidden="true"
                />
              )}
            </button>
          )}
        </div>
      )}
    </div>
  );
}

export default FormField;