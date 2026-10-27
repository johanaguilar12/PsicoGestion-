import { useState } from "react";
import { Link } from "react-router-dom";
import { Mail } from "lucide-react";
import LoginBrand from "../components/auth/LoginBrand";
import { API_BASE_URL, apiFetch } from "../services/api";
import "../styles/login.css";
import "../styles/password-recovery.css";

function PasswordRecoveryPage() {
  const [email, setEmail] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");
    setMessage("");
    setIsSubmitting(true);

    try {
      const response = await apiFetch(
        `${API_BASE_URL}/api/v1/auth/password-reset/request`,
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ email }),
        },
      );

      if (!response.ok) {
        const body = await response.json();
        setError(body.message ?? "No fue posible procesar la solicitud.");
        return;
      }

      setMessage(
        "Si la cuenta está registrada, recibirás instrucciones para recuperar tu contraseña.",
      );
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
          <form className="login-form" onSubmit={handleSubmit}>
            <div className="login-form__heading">
              <h1 className="login-form__title">Recuperar contraseña</h1>
              <p className="login-form__description">
                Ingresa el correo asociado a tu cuenta y te enviaremos un enlace
                para crear una nueva contraseña.
              </p>
            </div>

            <div className="login-form__fields">
              <div className="login-field">
                <label className="login-field__label" htmlFor="recovery-email">
                  Correo electrónico
                </label>
                <div className="login-field__control">
                  <Mail
                    className="login-field__icon"
                    size={24}
                    strokeWidth={1.8}
                    aria-hidden="true"
                  />
                  <input
                    id="recovery-email"
                    className="login-field__input"
                    type="email"
                    value={email}
                    onChange={(event) => setEmail(event.target.value)}
                    placeholder="nombre@correo.uady.mx"
                    autoComplete="email"
                    required
                  />
                </div>
              </div>
            </div>

            {error && <p className="login-form__error" role="alert">{error}</p>}
            {message && (
              <p className="password-recovery__message" role="status">
                {message}
              </p>
            )}

            <button
              type="submit"
              className="login-form__submit"
              disabled={isSubmitting}
            >
              {isSubmitting ? "Enviando..." : "Enviar instrucciones"}
            </button>

            <Link className="password-recovery__back" to="/login">
              Volver al inicio de sesión
            </Link>
          </form>
        </div>
      </div>
    </main>
  );
}

export default PasswordRecoveryPage;
