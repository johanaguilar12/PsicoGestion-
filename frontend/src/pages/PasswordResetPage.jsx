import { useState } from "react";
import { LockKeyhole } from "lucide-react";
import { Link, useLocation } from "react-router-dom";
import LoginBrand from "../components/auth/LoginBrand";
import { API_BASE_URL, apiFetch } from "../services/api";
import "../styles/login.css";
import "../styles/password-recovery.css";

const PASSWORD_RULES =
  "La contraseña debe tener entre 8 y 25 caracteres, una mayúscula, un número y un carácter especial.";

function PasswordResetPage() {
  const location = useLocation();
  const token = new URLSearchParams(location.search).get("token") ?? "";
  const [password, setPassword] = useState("");
  const [confirmation, setConfirmation] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isComplete, setIsComplete] = useState(false);
  const [error, setError] = useState("");

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");

    if (!token) {
      setError("El enlace de recuperación no es válido o expiró.");
      return;
    }

    const hasValidLength = password.length >= 8 && password.length <= 25;
    const hasUppercase = password !== password.toLowerCase();
    const hasNumber = /\d/.test(password);
    const hasSpecialCharacter = /[!@#$%^&*(),.?":{}|<>]/.test(password);

    if (!hasValidLength || !hasUppercase || !hasNumber || !hasSpecialCharacter) {
      setError(PASSWORD_RULES);
      return;
    }

    if (password !== confirmation) {
      setError("Las contraseñas no coinciden.");
      return;
    }

    setIsSubmitting(true);

    try {
      const response = await apiFetch(
        `${API_BASE_URL}/api/v1/auth/password-reset/confirm`,
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ token, newPassword: password }),
        },
      );

      if (!response.ok) {
        const body = await response.json();
        setError(body.message ?? "No fue posible cambiar la contraseña.");
        return;
      }

      setIsComplete(true);
      setPassword("");
      setConfirmation("");
    } catch {
      setError("No se pudo conectar con el servidor. Inténtalo de nuevo.");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <main className="login-page">
      <div className="login-container">
        <LoginBrand />

        <div className="login-card">
          {isComplete ? (
            <div className="password-recovery__complete">
              <h1 className="login-form__title">Contraseña actualizada</h1>
              <p className="login-form__description">
                Ya puedes iniciar sesión con tu nueva contraseña.
              </p>
              <Link className="login-form__submit password-recovery__submit-link" to="/login">
                Ir al inicio de sesión
              </Link>
            </div>
          ) : !token ? (
            <div className="password-recovery__complete">
              <h1 className="login-form__title">Enlace no válido</h1>
              <p className="login-form__description">
                Solicita un nuevo enlace para recuperar tu contraseña.
              </p>
              <Link className="password-recovery__back" to="/recuperar-contrasena">
                Solicitar otro enlace
              </Link>
            </div>
          ) : (
            <form className="login-form" onSubmit={handleSubmit}>
              <div className="login-form__heading">
                <h1 className="login-form__title">Crea una nueva contraseña</h1>
                <p className="login-form__description">
                  {PASSWORD_RULES}
                </p>
              </div>

              <div className="login-form__fields">
                <div className="login-field">
                  <label className="login-field__label" htmlFor="new-password">
                    Nueva contraseña
                  </label>
                  <div className="login-field__control">
                    <LockKeyhole
                      className="login-field__icon"
                      size={24}
                      strokeWidth={1.8}
                      aria-hidden="true"
                    />
                    <input
                      id="new-password"
                      className="login-field__input"
                      type="password"
                      value={password}
                      onChange={(event) => setPassword(event.target.value)}
                      autoComplete="new-password"
                      minLength={8}
                      maxLength={25}
                      required
                    />
                  </div>
                </div>

                <div className="login-field">
                  <label className="login-field__label" htmlFor="confirm-password">
                    Confirma la contraseña
                  </label>
                  <div className="login-field__control">
                    <LockKeyhole
                      className="login-field__icon"
                      size={24}
                      strokeWidth={1.8}
                      aria-hidden="true"
                    />
                    <input
                      id="confirm-password"
                      className="login-field__input"
                      type="password"
                      value={confirmation}
                      onChange={(event) => setConfirmation(event.target.value)}
                      autoComplete="new-password"
                      minLength={8}
                      maxLength={25}
                      required
                    />
                  </div>
                </div>
              </div>

              {error && <p className="login-form__error" role="alert">{error}</p>}

              <button
                type="submit"
                className="login-form__submit"
                disabled={isSubmitting}
              >
                {isSubmitting ? "Actualizando..." : "Actualizar contraseña"}
              </button>
            </form>
          )}
        </div>
      </div>
    </main>
  );
}

export default PasswordResetPage;
