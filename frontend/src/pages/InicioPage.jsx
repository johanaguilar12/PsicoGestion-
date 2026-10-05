import "../styles/home.css";

import {
  ArrowRight,
  CalendarDays,
  CalendarPlus,
  CheckCircle2,
  Clock3,
  UserPlus,
} from "lucide-react";

import { Link } from "react-router-dom";


const todaySummary = [
  {
    id: "today",
    label: "Citas de hoy",
    value: 12,
    icon: CalendarDays,
  },
  {
    id: "confirmed",
    label: "Confirmadas",
    value: 8,
    icon: CheckCircle2,
  },
  {
    id: "pending",
    label: "Pendientes",
    value: 4,
    icon: Clock3,
  },
];

const nextAppointment = {
  time: "10:30 a. m.",
  patient: "María Silva",
  therapist: "Armando Quintal",
  room: "2",
};

const quickActions = [
  {
    id: "new-appointment",
    label: "Nueva cita",
    description: "Agenda una nueva cita",
    path: "/agenda/nueva",
    icon: CalendarPlus,
  },
  {
    id: "new-patient",
    label: "Nuevo paciente",
    description: "Registra un nuevo paciente",
    path: "/pacientes/nuevo",
    icon: UserPlus,
  },
];

function InicioPage() {
  const formattedDate = new Intl.DateTimeFormat("es-MX", {
    weekday: "long",
    day: "numeric",
    month: "long",
    year: "numeric",
  }).format(new Date());

  const currentDate =
    formattedDate.charAt(0).toUpperCase() +
    formattedDate.slice(1);

  return (
    <section className="home-page">
      <header className="home-header">
        <h1 className="page-title">
          Inicio
        </h1>

        <p className="home-header__date">
          {currentDate}
        </p>
      </header>

      <section
        className="home-section"
        aria-labelledby="today-summary-title"
      >
        <h2
          id="today-summary-title"
          className="home-section__title"
        >
          Resumen de hoy
        </h2>

        <div className="home-stats">
          {todaySummary.map((item) => {
            const Icon = item.icon;

            return (
              <article
                key={item.id}
                className="home-stat-card"
              >
                <div
                  className="home-stat-card__icon"
                  aria-hidden="true"
                >
                  <Icon
                    size={24}
                    strokeWidth={1.8}
                  />
                </div>

                <div className="home-stat-card__content">
                  <span className="home-stat-card__label">
                    {item.label}
                  </span>

                  <strong className="home-stat-card__value">
                    {item.value}
                  </strong>
                </div>
              </article>
            );
          })}
        </div>
      </section>

      <section
        className="home-section"
        aria-labelledby="next-appointment-title"
      >
        <div className="home-section__heading">
          <h2
            id="next-appointment-title"
            className="home-section__title"
          >
            Próxima cita
          </h2>

          <Link
            to="/agenda"
            className="home-section__link"
          >
            <span>Ver agenda</span>

            <ArrowRight
              size={18}
              strokeWidth={1.8}
              aria-hidden="true"
            />
          </Link>
        </div>

        <article className="next-appointment">
          <div className="next-appointment__time">
            <span className="next-appointment__time-label">
              Próxima
            </span>

            <strong>
              {nextAppointment.time}
            </strong>
          </div>

          <div className="next-appointment__information">
            <div className="next-appointment__detail">
              <span className="next-appointment__label">
                Paciente
              </span>

              <span className="next-appointment__primary-value">
                {nextAppointment.patient}
              </span>
            </div>

            <div className="next-appointment__details">
  <div className="next-appointment__detail">
    <span className="next-appointment__label">
      Terapeuta
    </span>

    <span className="next-appointment__detail-value">
      {nextAppointment.therapist}
    </span>
  </div>

  <div className="next-appointment__detail">
    <span className="next-appointment__label">
      Sala
    </span>

    <span className="next-appointment__detail-value">
      {nextAppointment.room}
    </span>
  </div>
</div>
          </div>
        </article>
      </section>

      <section
        className="home-section"
        aria-labelledby="quick-actions-title"
      >
        <h2
          id="quick-actions-title"
          className="home-section__title"
        >
          Accesos rápidos
        </h2>

        <div className="quick-actions">
          {quickActions.map((action) => {
            const Icon = action.icon;

            return (
              <Link
                key={action.id}
                to={action.path}
                className="quick-action"
              >
                <div
                  className="quick-action__icon"
                  aria-hidden="true"
                >
                  <Icon
                    size={24}
                    strokeWidth={1.8}
                  />
                </div>

                <div className="quick-action__content">
                  <strong>
                    {action.label}
                  </strong>

                  <span>
                    {action.description}
                  </span>
                </div>

                <ArrowRight
                  className="quick-action__arrow"
                  size={20}
                  strokeWidth={1.8}
                  aria-hidden="true"
                />
              </Link>
            );
          })}
        </div>
      </section>
    </section>
  );
}

export default InicioPage;