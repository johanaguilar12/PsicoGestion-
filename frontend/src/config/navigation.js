import {
    CalendarDays,
    DoorOpen,
    Home,
    SquareUser,
    UserCog,
    Users,
  } from "lucide-react";
  
  export const navigationItems = [
    {
      label: "Inicio",
      path: "/",
      icon: Home,
    },
    {
      label: "Agenda",
      path: "/agenda",
      icon: CalendarDays,
    },
    {
      label: "Pacientes",
      path: "/pacientes",
      icon: SquareUser,
    },
    {
      label: "Terapeutas",
      path: "/terapeutas",
      icon: Users,
    },
    {
      label: "Salas",
      path: "/salas",
      icon: DoorOpen,
    },
    {
      label: "Usuarios",
      path: "/usuarios",
      icon: UserCog,
      adminOnly: true,
    },
  ];