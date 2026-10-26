import { useState } from "react";
import {
  Eye,
  EyeOff,
  LockKeyhole,
  Mail,
} from "lucide-react";
import { useNavigate } from "react-router-dom";
import { API_BASE_URL, apiFetch } from "../../services/api";

function LoginForm() {
  const navigate = useNavigate();
  const [credentials, setCredentials] = useState({
    identifier: "",
    password: "",
  });
  const [showPassword, setShowPassword] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState("");

  const handleChange = (event) => {
    const { name, value } = event.target;

    setCredentials((current) => ({
      ...current,
      [name]: value,
    }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");
    setIsSubmitting(true);

    try {
      const response = await apiFetch(`${API_BASE_URL}/api/v1/auth/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(credentials),
      });

      if (!response.ok) {
        const body = await response.json().catch(() => ({}));
        setError(body.message ?? "No fue posible iniciar sesión. Verifica tus credenciales.");
        return;
      }

      navigate("/", { replace: true });
    } catch {
      setError("No se pudo conectar con el servidor. Inténtalo de nuevo.");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <form
      className="login-form"
      onSubmit={handleSubmit}
    >
      <div className="login-form__heading">
        <h1 className="login-form__title">
          Iniciar sesión
        </h1>

        <p className="login-form__description">
          Ingresa con tus credenciales institucionales.
        </p>
      </div>

      <div className="login-form__fields">
        <div className="login-field">
          <label
            className="login-field__label"
            htmlFor="identifier"
          >
            Correo o teléfono
          </label>

          <div className="login-field__control">
            <Mail
              className="login-field__icon"
              size={24}
              strokeWidth={1.8}
              aria-hidden="true"
            />

            <input
              id="identifier"
              name="identifier"
              type="text"
              className="login-field__input"
              value={credentials.identifier}
              onChange={handleChange}
              placeholder="admin@correo.uady.mx"
              autoComplete="username"
              required
            />
          </div>
        </div>

        <div className="login-field">
          <label
            className="login-field__label"
            htmlFor="password"
          >
            Contraseña
          </label>

          <div className="login-field__control">
            <LockKeyhole
              className="login-field__icon"
              size={24}
              strokeWidth={1.8}
              aria-hidden="true"
            />

            <input
              id="password"
              name="password"
              type={showPassword ? "text" : "password"}
              className="login-field__input login-field__input--password"
              value={credentials.password}
              onChange={handleChange}
              placeholder="Ingresa tu contraseña"
              autoComplete="current-password"
              required
            />

            <button
              type="button"
              className="login-field__visibility"
              onClick={() => setShowPassword((current) => !current)}
              aria-label={
                showPassword
                  ? "Ocultar contraseña"
                  : "Mostrar contraseña"
              }
              aria-pressed={showPassword}
            >
              {showPassword ? (
                <EyeOff
                  size={24}
                  strokeWidth={1.8}
                  aria-hidden="true"
                />
              ) : (
                <Eye
                  size={24}
                  strokeWidth={1.8}
                  aria-hidden="true"
                />
              )}
            </button>
          </div>

          <a
            href="/recuperar-contrasena"
            className="login-form__forgot"
          >
            ¿Olvidaste tu contraseña?
          </a>
        </div>
      </div>

      {error && <p className="login-form__error" role="alert">{error}</p>}

      <button
        type="submit"
        className="login-form__submit"
        disabled={isSubmitting}
      >
        {isSubmitting ? "Iniciando sesión..." : "Iniciar sesión"}
      </button>
    </form>
  );
}

export default LoginForm;