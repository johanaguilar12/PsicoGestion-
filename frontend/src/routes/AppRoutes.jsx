import {
  Navigate,
  Route,
  Routes,
} from "react-router-dom";

import AppLayout from "../components/layout/AppLayout";

import LoginPage from "../pages/LoginPage";
import InicioPage from "../pages/InicioPage";
import AgendaPage from "../pages/AgendaPage";
import PacientesPage from "../pages/PacientesPage";
import TerapeutasPage from "../pages/TerapeutasPage";
import SalasPage from "../pages/SalasPage";
import UsuariosPage from "../pages/UsuariosPage";
import UsuarioDetallePage from "../pages/UsuarioDetallePage";
import NuevaCitaPage from "../pages/NuevaCitaPage";
import NuevoPacientePage from "../pages/NuevoPacientePage";

function AppRoutes() {
  return (
    <Routes>
      <Route
        path="/login"
        element={<LoginPage />}
      />

      <Route element={<AppLayout />}>
        <Route
          index
          element={<InicioPage />}
        />

        <Route
          path="agenda"
          element={<AgendaPage />}
        />

        <Route
          path="agenda/nueva"
          element={<NuevaCitaPage />}
        />

        <Route
          path="pacientes"
          element={<PacientesPage />}
        />

        <Route
          path="pacientes/nuevo"
          element={<NuevoPacientePage />}
        />

        <Route
          path="terapeutas"
          element={<TerapeutasPage />}
        />

        <Route
          path="salas"
          element={<SalasPage />}
        />

        <Route
          path="usuarios"
          element={<UsuariosPage />}
        />

        <Route
          path="usuarios/:userId"
          element={<UsuarioDetallePage />}
        />
      </Route>

      <Route
        path="*"
        element={<Navigate to="/" replace />}
      />
    </Routes>
  );
}

export default AppRoutes;