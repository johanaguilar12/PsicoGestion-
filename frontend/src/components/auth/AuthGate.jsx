import { useEffect, useState } from "react";
import { Navigate, Outlet, useNavigate } from "react-router-dom";
import { API_BASE_URL, apiFetch } from "../../services/api";
import { AuthProvider } from "./AuthProvider";

function AuthGate() {
  const navigate = useNavigate();
  const [session, setSession] = useState({ status: "loading", user: null });

  useEffect(() => {
    let isMounted = true;

    apiFetch(`${API_BASE_URL}/api/v1/auth/me`)
      .then(async (response) => {
        if (!isMounted) return;

        if (response.ok) {
          setSession({ status: "authenticated", user: await response.json() });
        } else if (response.status === 401) {
          setSession({ status: "unauthenticated", user: null });
        } else {
          setSession({ status: "error", user: null });
        }
      })
      .catch(() => {
        if (isMounted) setSession({ status: "error", user: null });
      });

    return () => {
      isMounted = false;
    };
  }, []);

  const logout = async () => {
    const response = await apiFetch(`${API_BASE_URL}/api/v1/auth/logout`, {
      method: "POST",
    });
    if (!response.ok) {
      throw new Error("No se pudo cerrar la sesión.");
    }

    setSession({ status: "unauthenticated", user: null });
    navigate("/login", { replace: true });
  };

  if (session.status === "loading") {
    return <main aria-live="polite">Verificando sesión...</main>;
  }

  if (session.status === "unauthenticated") {
    return <Navigate to="/login" replace />;
  }

  if (session.status === "error") {
    return (
      <main role="alert">
        No se pudo verificar la sesión. Comprueba que el servidor esté disponible y vuelve a cargar la página.
      </main>
    );
  }

  return (
    <AuthProvider value={{ user: session.user, logout }}>
      <Outlet />
    </AuthProvider>
  );
}

export default AuthGate;
