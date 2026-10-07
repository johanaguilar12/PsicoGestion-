import { Navigate, Route, Routes } from "react-router-dom";

import AppLayout from "../components/layout/AppLayout";

import AgendaPage from "../pages/AgendaPage";
import EditarPacientePage from "../pages/EditarPacientePage";
import EditarSalaPage from "../pages/EditarSalaPage";
import EditarTerapeutaPage from "../pages/EditarTerapeutaPage";
import EditarUsuarioPage from "../pages/EditarUsuarioPage";
import InicioPage from "../pages/InicioPage";
import LoginPage from "../pages/LoginPage";
import NuevaCitaPage from "../pages/NuevaCitaPage";
import NuevaSalaPage from "../pages/NuevaSalaPage";
import NuevoPacientePage from "../pages/NuevoPacientePage";
import NuevoTerapeutaPage from "../pages/NuevoTerapeutaPage";
import NuevoUsuarioPage from "../pages/NuevoUsuarioPage";
import PacienteDetallePage from "../pages/PacienteDetallePage";
import PacientesPage from "../pages/PacientesPage";
import SalaDetallePage from "../pages/SalaDetallePage";
import SalasPage from "../pages/SalasPage";
import TerapeutaDetallePage from "../pages/TerapeutaDetallePage";
import TerapeutasPage from "../pages/TerapeutasPage";
import UsuarioDetallePage from "../pages/UsuarioDetallePage";
import UsuariosPage from "../pages/UsuariosPage";

function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />

      <Route element={<AppLayout />}>
        <Route index element={<InicioPage />} />

        <Route path="agenda" element={<AgendaPage />} />
        <Route path="agenda/nueva" element={<NuevaCitaPage />} />

        <Route path="pacientes" element={<PacientesPage />} />
        <Route path="pacientes/nuevo" element={<NuevoPacientePage />} />
        <Route
          path="pacientes/:patientId"
          element={<PacienteDetallePage />}
        />
        <Route
          path="pacientes/:patientId/editar"
          element={<EditarPacientePage />}
        />

        <Route path="terapeutas" element={<TerapeutasPage />} />
        <Route
          path="terapeutas/nuevo"
          element={<NuevoTerapeutaPage />}
        />
        <Route
          path="terapeutas/:therapistId"
          element={<TerapeutaDetallePage />}
        />
        <Route
          path="terapeutas/:therapistId/editar"
          element={<EditarTerapeutaPage />}
        />

        <Route path="salas" element={<SalasPage />} />
        <Route path="salas/nueva" element={<NuevaSalaPage />} />
        <Route
          path="salas/:roomId"
          element={<SalaDetallePage />}
        />
        <Route
          path="salas/:roomId/editar"
          element={<EditarSalaPage />}
        />

        <Route path="usuarios" element={<UsuariosPage />} />
        <Route
          path="usuarios/nuevo"
          element={<NuevoUsuarioPage />}
        />
        <Route
          path="usuarios/:userId"
          element={<UsuarioDetallePage />}
        />
        <Route
          path="usuarios/:userId/editar"
          element={<EditarUsuarioPage />}
        />
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}

export default AppRoutes;