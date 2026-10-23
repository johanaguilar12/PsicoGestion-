import { useState } from "react";
import {
  Eye,
  EyeOff,
  LockKeyhole,
  Mail,
} from "lucide-react";

function LoginForm() {
  const [credentials, setCredentials] = useState({
    identifier: "",
    password: "",
  });

  const [showPassword, setShowPassword] = useState(false);

  const handleChange = (event) => {
    const { name, value } = event.target;

    setCredentials((current) => ({
      ...current,
      [name]: value,
    }));
  };

  const handleSubmit = (event) => {
    event.preventDefault();

    console.log("Login:", credentials.identifier);
  };

  return (
    <form
      className="login-form"
      onSubmit={handleSubmit}
      noValidate
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

      <button
        type="submit"
        className="login-form__submit"
      >
        Iniciar sesión
      </button>
    </form>
  );
}

export default LoginForm;