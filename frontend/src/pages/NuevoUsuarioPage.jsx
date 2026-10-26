import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { apiFetch } from "../services/api";
import CreateFormPage from "../components/forms/CreateFormPage";
import FormField from "../components/forms/FormField";
import PhotoUpload from "../components/forms/PhotoUpload";

function NuevoUsuarioPage() {
  const navigate = useNavigate();
  const [errorMessage, setErrorMessage] = useState(null);
  
  
  const [showVerificationModal, setShowVerificationModal] = useState(false);
  const [pendingFormData, setPendingFormData] = useState(null);
  const [verificationCode, setVerificationCode] = useState("");

  const handleSubmit = (event) => {
    event.preventDefault();
    setErrorMessage(null);

    const pass = event.target.password.value;
    const confirmPass = event.target.confirmPassword.value;

    
    if (pass !== confirmPass) {
      setErrorMessage("Las contraseñas no coinciden. Por favor, verifícalas.");
      return;
    }

    const formData = new FormData();
    formData.append("nombreCompleto", event.target.name.value);
    formData.append("correoElectronico", event.target.email.value);
    formData.append("telefono", event.target.phone.value);
    formData.append("contrasena", pass);

    const fileInput = document.querySelector('input[type="file"]');
    if (fileInput && fileInput.files[0]) {
      formData.append("foto", fileInput.files[0]);
    }

    
    setPendingFormData(formData);
    setShowVerificationModal(true);
  };

  const confirmarRegistro = async () => {
    
    if (!verificationCode || verificationCode.length < 4) {
      alert("Por favor ingresa el código válido que enviamos a tu correo.");
      return;
    }

    try {
      const response = await apiFetch("http://localhost:8081/api/v1/cuentas/registro", {
        method: "POST",
        body: pendingFormData, 
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText);
      }
      setShowVerificationModal(false);
      navigate("/usuarios");
    } catch (error) {
      setShowVerificationModal(false);
      setErrorMessage(error.message);
    }
  };

  return (
    <>
      <CreateFormPage
        breadcrumb={[{ label: "Usuarios", to: "/usuarios" }, { label: "Nuevo usuario" }]}
        title="Nuevo usuario"
        onSubmit={handleSubmit}
        onCancel={() => navigate("/usuarios")}
      >
        <section className="create-section">
          <h2 className="create-section__title">Información del usuario</h2>
          
          {errorMessage && (
            <div style={{ backgroundColor: "#fee2e2", color: "#991b1b", padding: "1rem", borderRadius: "8px", marginBottom: "1.5rem" }}>
              <strong>Error:</strong> {errorMessage}
            </div>
          )}

          <div className="create-profile-layout">
            <div className="create-profile-layout__fields">
              <FormField label="Nombre completo" name="name" placeholder="Ej. María Silva Pineda" maxLength={50} required />
              <FormField label="Correo institucional" name="email" type="email" placeholder="ejemplo@correo.uady.mx" required />
              <FormField label="Teléfono" name="phone" type="tel" placeholder="999 999 9999" required />
            </div>
            <div className="create-profile-layout__photo">
              <PhotoUpload />
            </div>
          </div>

          <div className="create-user__role">
            <FormField label="Rol" name="role" defaultValue="Administrador" readOnly required />
          </div>

          <div className="form-grid form-grid--two">
            <FormField label="Contraseña" name="password" type="password" placeholder="Mínimo 8 caracteres" required />
            <FormField label="Confirmar contraseña" name="confirmPassword" type="password" placeholder="Confirma la contraseña" required />
          </div>
        </section>
      </CreateFormPage>

      {/* Modal de confirmación de correo RF 1.1.9 - 1.1.11 */}
      {showVerificationModal && (
        <div style={{ position: "fixed", top: 0, left: 0, width: "100%", height: "100%", backgroundColor: "rgba(0,0,0,0.5)", display: "flex", justifyContent: "center", alignItems: "center", zIndex: 1000 }}>
          <div style={{ backgroundColor: "white", padding: "2rem", borderRadius: "8px", maxWidth: "400px", width: "90%" }}>
            <h3 style={{ marginTop: 0 }}>Verificación de cuenta</h3>
            <p style={{ fontSize: "14px", color: "#4b5563", marginBottom: "1.5rem" }}>
              Para asegurar que eres dueño del correo electrónico, hemos enviado un <strong>código de verificación</strong> a tu bandeja de entrada institucional.
            </p>
            <input 
              type="text" 
              placeholder="Ingresa tu código" 
              value={verificationCode}
              onChange={(e) => setVerificationCode(e.target.value)}
              style={{ width: "100%", padding: "0.75rem", border: "1px solid #d1d5db", borderRadius: "6px", marginBottom: "1rem" }}
            />
            <div style={{ display: "flex", gap: "1rem", justifyContent: "flex-end" }}>
              <button type="button" onClick={() => setShowVerificationModal(false)} style={{ padding: "0.5rem 1rem", border: "none", backgroundColor: "transparent", cursor: "pointer", fontWeight: "500" }}>Cancelar</button>
              <button type="button" onClick={confirmarRegistro} style={{ padding: "0.5rem 1rem", border: "none", backgroundColor: "#0f172a", color: "white", borderRadius: "6px", cursor: "pointer", fontWeight: "500" }}>Verificar código</button>
            </div>
          </div>
        </div>
      )}
    </>
  );
}

export default NuevoUsuarioPage;