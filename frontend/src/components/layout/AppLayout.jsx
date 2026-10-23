import { useEffect, useState } from "react";
import {
  Outlet,
  useLocation,
} from "react-router-dom";

import MobileHeader from "./MobileHeader";
import Sidebar from "./Sidebar";

function AppLayout() {
  const [isMenuOpen, setIsMenuOpen] =
    useState(false);

  const location = useLocation();

  const openMenu = () => {
    setIsMenuOpen(true);
  };

  const closeMenu = () => {
    setIsMenuOpen(false);
  };

  useEffect(() => {
    closeMenu();
  }, [location.pathname]);

  useEffect(() => {
    if (!isMenuOpen) {
      document.body.style.overflow = "";
      return undefined;
    }

    document.body.style.overflow = "hidden";

    return () => {
      document.body.style.overflow = "";
    };
  }, [isMenuOpen]);

  useEffect(() => {
    const handleEscape = (event) => {
      if (event.key === "Escape") {
        closeMenu();
      }
    };

    window.addEventListener(
      "keydown",
      handleEscape
    );

    return () => {
      window.removeEventListener(
        "keydown",
        handleEscape
      );
    };
  }, []);

  return (
    <div className="app-layout">
      <MobileHeader
        isMenuOpen={isMenuOpen}
        onMenuOpen={openMenu}
      />

      <Sidebar
        isOpen={isMenuOpen}
        onClose={closeMenu}
      />

      <button
        type="button"
        className={`mobile-overlay ${
          isMenuOpen
            ? "mobile-overlay--visible"
            : ""
        }`}
        onClick={closeMenu}
        aria-label="Cerrar menú"
        tabIndex={isMenuOpen ? 0 : -1}
      />

      <main className="app-main">
        <div className="app-content">
          <Outlet />
        </div>
      </main>
    </div>
  );
}

export default AppLayout;