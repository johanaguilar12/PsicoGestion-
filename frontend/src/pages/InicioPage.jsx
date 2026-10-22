import "../styles/home.css";
import { useState, useEffect, useCallback } from "react";
import { ArrowRight, CalendarDays, CalendarPlus, CheckCircle2, Clock3, UserPlus } from "lucide-react";
import { Link } from "react-router-dom";
import { dateKey, formatTime, parseDate, addMinutes } from '../components/calendar/dateUtils';
import AppointmentDialog from '../components/calendar/AppointmentDialog';

const quickActions = [
  { id: "new-appointment", label: "Nueva cita", description: "Agenda una nueva cita", path: "/agenda?nueva=1", icon: CalendarPlus },
  { id: "new-patient", label: "Nuevo paciente", description: "Registra un nuevo paciente", path: "/pacientes/nuevo", icon: UserPlus },
];

const formatRelativeDate = (dateStr, hoyStr) => {
  if (dateStr === hoyStr) return "Hoy";
  const dateObj = parseDate(dateStr);
  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 1);
  if (dateStr === dateKey(tomorrow)) return "Mañana";
  return new Intl.DateTimeFormat("es-MX", { weekday: "short", day: "numeric", month: "short" }).format(dateObj);
};

const getPersonName = (obj) => {
  const name = obj?.nombreCompleto || obj?.nombre_completo || (obj?.nombre && obj?.apellido ? `${obj.nombre} ${obj.apellido}` : obj?.nombre) || obj?.nombres || "Desconocido";
  return typeof name === 'string' ? name.trim() : name;
};
const getRoomName = (obj) => {
  const name = obj?.nombreSala || obj?.nombre_sala || obj?.nombre || "Sin sala";
  return typeof name === 'string' ? name.trim() : name;
};
const getId = (obj, type) => obj?.[`id${type}`] || obj?.[`id_${type.toLowerCase()}`] || obj?.id;

function InicioPage() {
  const [stats, setStats] = useState({ total: 0, confirmadas: 0, pendientes: 0 });
  const [nextAppointments, setNextAppointments] = useState([]);
  
  // Estados para el Modal
  const [appointments, setAppointments] = useState([]);
  const [pacientes, setPacientes] = useState([]);
  const [terapeutas, setTerapeutas] = useState([]);
  const [salas, setSalas] = useState([]);
  const [selected, setSelected] = useState(null);
  const [dialogMode, setDialogMode] = useState('detail');

  const formattedDate = new Intl.DateTimeFormat("es-MX", { weekday: "long", day: "numeric", month: "long", year: "numeric" }).format(new Date());
  const currentDate = formattedDate.charAt(0).toUpperCase() + formattedDate.slice(1);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [citasRes, pacRes, terRes, salRes] = await Promise.all([
          fetch("http://localhost:8081/api/v1/citas/detalles"),
          fetch("http://localhost:8081/api/v1/pacientes"),
          fetch("http://localhost:8081/api/v1/terapeutas"),
          fetch("http://localhost:8081/api/v1/salas")
        ]);

        if (salRes.ok) setSalas(await salRes.json());
        if (terRes.ok) setTerapeutas(await terRes.json());
        if (pacRes.ok) setPacientes(await pacRes.json());

        if (citasRes.ok) {
          const data = await citasRes.json();
          const ahoraDate = new Date();
          const hoyStr = dateKey(ahoraDate);
          const horaActualStr = `${String(ahoraDate.getHours()).padStart(2, '0')}:${String(ahoraDate.getMinutes()).padStart(2, '0')}`;
          
          const citasFormateadas = data.map(c => {
            let fechaLimpia = typeof c.fecha === 'string' ? c.fecha.substring(0, 10) : c.fecha;
            let horaInicioLimpia = typeof c.hora_inicio === 'string' ? c.hora_inicio.substring(0, 5) : "00:00";
            if (Array.isArray(c.fecha)) fechaLimpia = `${c.fecha[0]}-${String(c.fecha[1]).padStart(2, '0')}-${String(c.fecha[2]).padStart(2, '0')}`;
            if (Array.isArray(c.hora_inicio)) horaInicioLimpia = `${String(c.hora_inicio[0]).padStart(2, '0')}:${String(c.hora_inicio[1]).padStart(2, '0')}`;

            const estadoSeguro = String(c.estado || 'PENDIENTE').toUpperCase();

            return {
              id: c.id_cita,
              patient: getPersonName({ nombreCompleto: c.paciente }),
              therapist: getPersonName({ nombreCompleto: c.terapeuta }),
              room: getRoomName({ nombreSala: c.sala }),
              date: fechaLimpia,
              time: horaInicioLimpia,
              status: estadoSeguro,
              notes: c.notas || '',
              color: estadoSeguro === 'PENDIENTE' ? 'pendiente' : estadoSeguro === 'CANCELADA' ? 5 : 'confirmada'
            };
          });

          setAppointments(citasFormateadas);

          const citasHoy = citasFormateadas.filter(c => c.date === hoyStr && c.status !== 'CANCELADA');
          const pendientesValidas = citasHoy.filter(c => c.status === 'PENDIENTE' && c.time >= horaActualStr);

          setStats({
            total: citasHoy.length,
            confirmadas: citasHoy.filter(c => c.status === 'CONFIRMADA').length,
            pendientes: pendientesValidas.length
          });

          const futuras = citasFormateadas.filter(c => {
            if (c.status === 'CANCELADA') return false;
            if (c.date < hoyStr) return false; 
            if (c.date === hoyStr && c.time < horaActualStr) return false; 
            return true;
          });

          futuras.sort((a, b) => a.date.localeCompare(b.date) || a.time.localeCompare(b.time));
          setNextAppointments(futuras.slice(0, 3));
        }
      } catch (error) {
        console.error("Error al cargar el resumen del inicio:", error);
      }
    };
    
    fetchData();
    const interval = setInterval(fetchData, 60000);
    return () => clearInterval(interval);
  }, []);

  const closeDialog = useCallback(() => setSelected(null), []);
  const openDetails = (item) => { setSelected(item); setDialogMode('detail'); };

  const save = async (item, nextMode) => { 
    if (nextMode === 'edit') { setDialogMode('edit'); return { success: true }; } 

    const matchName = (n1, n2) => String(n1).trim().toLowerCase() === String(n2).trim().toLowerCase();
    const pac = pacientes.find(p => matchName(getPersonName(p), item.patient) || String(getId(p, 'Paciente')) === String(item.patient));
    const ter = terapeutas.find(t => matchName(getPersonName(t), item.therapist) || String(getId(t, 'Terapeuta')) === String(item.therapist));
    const sal = salas.find(s => matchName(getRoomName(s), item.room) || String(getId(s, 'Sala')) === String(item.room));

    if (!pac || !ter || !sal) return { success: false, error: "Error de sistema: No se pudo identificar al paciente o sala." };

    const payload = {
      idPaciente: getId(pac, 'Paciente'),
      idTerapeuta: getId(ter, 'Terapeuta'),
      idSala: getId(sal, 'Sala'),
      fecha: item.date,
      horaInicio: item.time,
      horaFin: addMinutes(item.time, 60),
      estado: item.status || "PENDIENTE",
      notas: item.notes || "",
      correoPaciente: pac?.correoElectronico || "correo@ejemplo.com",
      telefonoPaciente: pac?.telefono || "0000000000"
    };

    try {
      const response = await fetch(`http://localhost:8081/api/v1/citas/${item.id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
      });
      if (response.ok) { window.location.reload(); return { success: true }; } 
      else { return { success: false, error: await response.text() }; }
    } catch { return { success: false, error: "Error de conexión." }; }
  };

  const remove = async (id) => { 
    try {
      const response = await fetch(`http://localhost:8081/api/v1/citas/${id}/cancelar`, { method: "PUT" });
      if (response.ok) window.location.reload(); 
    } catch (error) { console.error("Error al cancelar:", error); }
  };

  const todaySummary = [
    { id: "today", label: "Citas de hoy", value: stats.total, icon: CalendarDays },
    { id: "confirmed", label: "Confirmadas", value: stats.confirmadas, icon: CheckCircle2 },
    { id: "pending", label: "Pendientes", value: stats.pendientes, icon: Clock3 },
  ];

  return (
    <section className="home-page">
      <header className="home-header">
        <h1 className="page-title">Inicio</h1>
        <p className="home-header__date">{currentDate}</p>
      </header>

      <section className="home-section" aria-labelledby="today-summary-title">
        <h2 id="today-summary-title" className="home-section__title">Resumen de hoy</h2>
        <div className="home-stats">
          {todaySummary.map((item) => {
            const Icon = item.icon;
            return (
              <article key={item.id} className="home-stat-card">
                <div className="home-stat-card__icon" aria-hidden="true"><Icon size={24} strokeWidth={1.8} /></div>
                <div className="home-stat-card__content">
                  <span className="home-stat-card__label">{item.label}</span>
                  <strong className="home-stat-card__value">{item.value}</strong>
                </div>
              </article>
            );
          })}
        </div>
      </section>

      <section className="home-section" aria-labelledby="next-appointment-title">
        <div className="home-section__heading">
          <h2 id="next-appointment-title" className="home-section__title">Próximas citas</h2>
          <Link to="/agenda" className="home-section__link"><span>Ver agenda</span><ArrowRight size={18} strokeWidth={1.8}/></Link>
        </div>
        
        {nextAppointments.length > 0 ? (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            {nextAppointments.map((appt) => (
              <article 
                key={appt.id} 
                className="next-appointment" 
                onClick={() => openDetails(appt)} 
                style={{ cursor: 'pointer', transition: 'transform 0.2s' }}
                onMouseOver={(e) => e.currentTarget.style.transform = 'scale(1.01)'}
                onMouseOut={(e) => e.currentTarget.style.transform = 'scale(1)'}
              >
                <div className="next-appointment__time">
                  <span className="next-appointment__time-label" style={{ textTransform: 'capitalize' }}>{formatRelativeDate(appt.date, dateKey(new Date()))}</span>
                  <strong>{formatTime(appt.time)}</strong>
                </div>
                <div className="next-appointment__information">
                  <div className="next-appointment__detail">
                    <span className="next-appointment__label">Paciente</span>
                    <span className="next-appointment__primary-value">{appt.patient}</span>
                  </div>
                  <div className="next-appointment__details">
                    <div className="next-appointment__detail">
                      <span className="next-appointment__label">Terapeuta</span>
                      <span className="next-appointment__detail-value">{appt.therapist}</span>
                    </div>
                    <div className="next-appointment__detail">
                      <span className="next-appointment__label">Sala</span>
                      <span className="next-appointment__detail-value">{appt.room}</span>
                    </div>
                  </div>
                </div>
              </article>
            ))}
          </div>
        ) : (
          <article className="next-appointment" style={{ padding: '2rem', textAlign: 'center', backgroundColor: '#f8fafc', color: '#64748b' }}>
            No hay citas programadas próximamente.
          </article>
        )}
      </section>

      <section className="home-section" aria-labelledby="quick-actions-title">
        <h2 id="quick-actions-title" className="home-section__title">Accesos rápidos</h2>
        <div className="quick-actions">
          {quickActions.map((action) => {
            const Icon = action.icon;
            return (
              <Link key={action.id} to={action.path} className="quick-action">
                <div className="quick-action__icon" aria-hidden="true"><Icon size={24} strokeWidth={1.8} /></div>
                <div className="quick-action__content">
                  <strong>{action.label}</strong>
                  <span>{action.description}</span>
                </div>
                <ArrowRight className="quick-action__arrow" size={20} strokeWidth={1.8} aria-hidden="true" />
              </Link>
            );
          })}
        </div>
      </section>

      <AppointmentDialog 
        appointment={selected} 
        mode={dialogMode} 
        onClose={closeDialog} 
        onSave={save} 
        onDelete={remove} 
        appointments={appointments} 
        pacientes={pacientes}
        terapeutas={terapeutas}
        salas={salas}
      />
    </section>
  );
}

export default InicioPage;