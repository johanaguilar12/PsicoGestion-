import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { ChevronLeft, ChevronRight, Filter, Plus, Printer, X } from 'lucide-react';
import { useSearchParams } from 'react-router-dom';
import { apiFetch } from '../services/api';
import CalendarGrid from '../components/calendar/CalendarGrid';
import MobileCalendar from '../components/calendar/MobileCalendar';
import AppointmentDialog from '../components/calendar/AppointmentDialog';
import { addDays, addMinutes, dateKey, formatTime, longDate, monthStart, parseDate, weekStart } from '../components/calendar/dateUtils';
import '../styles/forms.css';
import '../styles/calendar.css';

const readPrintSettings = () => {
  try {
    const saved = JSON.parse(localStorage.getItem('psicogestion-agenda-print') || '{}');
    return {
      format: ['calendar', 'list'].includes(saved.format) ? saved.format : 'calendar',
      calendar: saved.calendar || {},
      list: saved.list || {},
    };
  } catch {
    return { format: 'calendar', calendar: {}, list: {} };
  }
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

function AgendaPage() {
  const [searchParams, setSearchParams] = useSearchParams();
  const [date, setDate] = useState(() => new Date());
  const [view, setView] = useState('week');
  const [printBlackWhite, setPrintBlackWhite] = useState(() => {
    try { return JSON.parse(localStorage.getItem('psicogestion-agenda-print') || '{}').blackWhite === true; } catch { return false; }
  });
  
  const [appointments, setAppointments] = useState([]);
  const [pacientes, setPacientes] = useState([]);
  const [terapeutas, setTerapeutas] = useState([]);
  const [salas, setSalas] = useState([]);

  const [selected, setSelected] = useState(null);
  const [dialogMode, setDialogMode] = useState('detail');
  const [filtersOpen, setFiltersOpen] = useState(false);
  const [printOpen, setPrintOpen] = useState(false);
  const [savedPrint] = useState(readPrintSettings);
  const [printFormat, setPrintFormat] = useState(savedPrint.format);
  const [printSettings, setPrintSettings] = useState(() => ({
    calendar: { period: savedPrint.calendar.period || 'week', date: dateKey(new Date()) },
    list: { period: savedPrint.list.period || 'week', date: dateKey(new Date()) },
  }));
  
  const [room, setRoom] = useState('');
  const [therapist, setTherapist] = useState('');
  const filterRef = useRef(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [citasRes, pacRes, terRes, salRes] = await Promise.all([
          apiFetch("http://localhost:8081/api/v1/citas/detalles"),
          apiFetch("http://localhost:8081/api/v1/pacientes"),
          apiFetch("http://localhost:8081/api/v1/terapeutas"),
          apiFetch("http://localhost:8081/api/v1/salas")
        ]);

        if (salRes.ok) setSalas(await salRes.json());
        if (terRes.ok) setTerapeutas(await terRes.json());
        if (pacRes.ok) setPacientes(await pacRes.json());

        if (citasRes.ok) {
          const data = await citasRes.json();
          const citasFormateadas = data.map(c => {
            let fechaLimpia = typeof c.fecha === 'string' ? c.fecha.substring(0, 10) : c.fecha;
            let horaInicioLimpia = typeof c.hora_inicio === 'string' ? c.hora_inicio.substring(0, 5) : "00:00";
            let horaFinLimpia = typeof c.hora_fin === 'string' ? c.hora_fin.substring(0, 5) : "00:30";

            if (Array.isArray(c.fecha)) fechaLimpia = `${c.fecha[0]}-${String(c.fecha[1]).padStart(2, '0')}-${String(c.fecha[2]).padStart(2, '0')}`;
            if (Array.isArray(c.hora_inicio)) horaInicioLimpia = `${String(c.hora_inicio[0]).padStart(2, '0')}:${String(c.hora_inicio[1]).padStart(2, '0')}`;
            if (Array.isArray(c.hora_fin)) horaFinLimpia = `${String(c.hora_fin[0]).padStart(2, '0')}:${String(c.hora_fin[1]).padStart(2, '0')}`;

            const estadoSeguro = String(c.estado || 'PENDIENTE').toUpperCase();

            return {
              id: c.id_cita,
              folio: c.folio,
              patient: getPersonName({ nombreCompleto: c.paciente }),
              therapist: getPersonName({ nombreCompleto: c.terapeuta }),
              room: getRoomName({ nombreSala: c.sala }),
              date: fechaLimpia,
              time: horaInicioLimpia,
              endTime: horaFinLimpia,
              status: estadoSeguro,
              notes: c.notas || '',
              color: estadoSeguro === 'PENDIENTE' ? 'pendiente' : estadoSeguro === 'CANCELADA' ? 5 : 'confirmada'
            };
          });
          setAppointments(citasFormateadas);
        }
      } catch (error) {
        console.error("Error cargando datos de agenda:", error);
      }
    };
    fetchData();
  }, []);

  const printPeriod = printSettings[printFormat].period;
  const printDate = printSettings[printFormat].date;
  const setPrintPeriod = (period) => setPrintSettings((current) => ({ ...current, [printFormat]: { ...current[printFormat], period } }));
  const setPrintDate = (date) => setPrintSettings((current) => ({ ...current, [printFormat]: { ...current[printFormat], date } }));

  useEffect(() => {
    const settings = { format: printFormat, blackWhite: printBlackWhite, ...printSettings };
    try { localStorage.setItem('psicogestion-agenda-print', JSON.stringify(settings)); } catch { }
  }, [printFormat, printSettings, printBlackWhite]);

  const closeDialog = useCallback(() => setSelected(null), []);
  const visible = useMemo(() => appointments.filter((item) => (!room || item.room === room) && (!therapist || item.therapist === therapist) && item.status !== 'CANCELADA'), [appointments, room, therapist]);
  
  const changePeriod = (step) => setDate((current) => view === 'month' ? new Date(current.getFullYear(), current.getMonth() + step, Math.min(current.getDate(), 28)) : addDays(current, step * (view === 'week' ? 7 : 1)));
  const start = weekStart(date);
  const periodLabel = view === 'day' ? longDate(date) : view === 'month' ? new Intl.DateTimeFormat('es-MX', { month: 'long', year: 'numeric' }).format(date) : `${new Intl.DateTimeFormat('es-MX', { day: 'numeric', month: 'short' }).format(start)} – ${new Intl.DateTimeFormat('es-MX', { day: 'numeric', month: 'short', year: 'numeric' }).format(addDays(start, 6))}`;
  
  const printDays = useMemo(() => {
    const chosen = parseDate(printDate);
    if (printPeriod === 'day') return [chosen];
    if (printPeriod === 'week') return Array.from({ length: 7 }, (_, index) => addDays(weekStart(chosen), index));
    return Array.from({ length: new Date(chosen.getFullYear(), chosen.getMonth() + 1, 0).getDate() }, (_, index) => new Date(chosen.getFullYear(), chosen.getMonth(), index + 1));
  }, [printDate, printPeriod]);
  
  const printDayKeys = useMemo(() => new Set(printDays.map(dateKey)), [printDays]);
  const printMonthDate = parseDate(printDate);
  const printTimes = useMemo(() => Array.from({ length: 21 }, (_, i) => `${String(Math.floor((420 + i * 30) / 60)).padStart(2, '0')}:${String((420 + i * 30) % 60).padStart(2, '0')}`), []);
  const printItems = useMemo(() => visible.filter((item) => printDayKeys.has(item.date)).sort((a, b) => a.date.localeCompare(b.date) || a.time.localeCompare(b.time)), [visible, printDayKeys]);
  
  const invalidPrintRange = false;

  const printGrouped = useMemo(() => {
    const grouped = new Map();
    printItems.forEach((item) => grouped.set(item.date, [...(grouped.get(item.date) || []), item]));
    return grouped;
  }, [printItems]);

  const printLabel = printPeriod === 'day' ? longDate(parseDate(printDate)) : printPeriod === 'week' ? `${longDate(printDays[0])} – ${longDate(printDays[6])}` : new Intl.DateTimeFormat('es-MX', { month: 'long', year: 'numeric' }).format(parseDate(printDate));
  
  const printNow = () => { 
    if (invalidPrintRange) return;
    setPrintOpen(false); 
    requestAnimationFrame(() => requestAnimationFrame(() => window.print())); 
  };
  
  const openPrint = () => setPrintOpen(true);

  useEffect(() => {
    if (!printOpen) return undefined;
    const oldOverflow = document.body.style.overflow;
    document.body.style.overflow = 'hidden';
    const onEscape = (event) => {
      if (event.key === 'Escape') setPrintOpen(false);
    };
    window.addEventListener('keydown', onEscape);
    return () => {
      document.body.style.overflow = oldOverflow;
      window.removeEventListener('keydown', onEscape);
    };
  }, [printOpen]);

  const todayKey = dateKey(new Date());
  const monthlyDays = useMemo(() => {
    const first = monthStart(date);
    const offset = first.getDay();
    const count = new Date(date.getFullYear(), date.getMonth() + 1, 0).getDate();
    return [...Array(offset).fill(null), ...Array.from({ length: count }, (_, index) => new Date(date.getFullYear(), date.getMonth(), index + 1))];
  }, [date]);

  const appointmentsByDate = useMemo(() => {
    const grouped = new Map();
    visible.forEach((item) => {
      const list = grouped.get(item.date) || [];
      list.push(item);
      grouped.set(item.date, list);
    });
    grouped.forEach((list) => list.sort((a, b) => a.time.localeCompare(b.time)));
    return grouped;
  }, [visible]);

  const create = (day = dateKey(date), time = '09:00') => { setSelected({ date: day, time, patient: '', therapist: '', room: '', notes: '', status: 'PENDIENTE' }); setDialogMode('create'); };

  useEffect(() => {
    if (searchParams.get('nueva') !== '1') return;
    create(dateKey(new Date()));
    const nextParams = new URLSearchParams(searchParams);
    nextParams.delete('nueva');
    setSearchParams(nextParams, { replace: true });
  }, [searchParams, setSearchParams]);

  const save = async (item, nextMode) => { 
    if (nextMode === 'edit') { setDialogMode('edit'); return { success: true }; } 

    const matchName = (n1, n2) => String(n1).trim().toLowerCase() === String(n2).trim().toLowerCase();

    const pac = pacientes.find(p => matchName(getPersonName(p), item.patient) || String(getId(p, 'Paciente')) === String(item.patient));
    const ter = terapeutas.find(t => matchName(getPersonName(t), item.therapist) || String(getId(t, 'Terapeuta')) === String(item.therapist));
    const sal = salas.find(s => matchName(getRoomName(s), item.room) || String(getId(s, 'Sala')) === String(item.room));

    if (!pac || !ter || !sal) {
      return { success: false, error: "Error de sistema: No se pudo identificar al paciente o sala seleccionada." };
    }

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
      const method = item.id ? "PUT" : "POST";
      const url = item.id ? `http://localhost:8081/api/v1/citas/${item.id}` : "http://localhost:8081/api/v1/citas";
      
      const response = await apiFetch(url, {
        method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
      });

      if (response.ok) {
        window.location.reload(); 
        return { success: true };
      } else {
        const errorText = await response.text();
        return { success: false, error: errorText }; 
      }
    } catch (error) {
      console.error("Error:", error);
      return { success: false, error: "Error de conexión con el servidor." };
    }
  };

  const remove = async (id) => { 
    try {
      const response = await apiFetch(`http://localhost:8081/api/v1/citas/${id}/cancelar`, { method: "PUT" });
      if (response.ok) {
        window.location.reload(); 
      }
    } catch (error) {
      console.error("Error al cancelar cita:", error);
    }
  };

  const openDetails = (item) => { setSelected(item); setDialogMode('detail'); };
  const changeView = (next) => setView(next);

  return <section className={`agenda-page agenda-page--${view}${view === 'day' && (date.getDay() === 0 || date.getDay() === 6) ? ' agenda-page--closed-day' : ''}`}>
    <header className="agenda-page__header"><h1 className="page-title">Agenda</h1><button className="data-page__create agenda-page__new" type="button" onClick={() => create()}><Plus size={20} /> Nueva cita</button></header>
    <div className="agenda-toolbar"><div className="agenda-toolbar__nav"><button type="button" className="calendar-icon-button" onClick={() => changePeriod(-1)} aria-label="Periodo anterior"><ChevronLeft size={21} /></button><button type="button" className="agenda-toolbar__today" onClick={() => setDate(new Date())}>Hoy</button><button type="button" className="calendar-icon-button" onClick={() => changePeriod(1)} aria-label="Periodo siguiente"><ChevronRight size={21} /></button></div>
      <div className="agenda-toolbar__views" role="group" aria-label="Vista del calendario">{[['day', 'Día'], ['week', 'Semana'], ['month', 'Mes']].map(([value, label]) => <button key={value} type="button" aria-pressed={view === value} className={view === value ? 'is-active' : ''} onClick={() => changeView(value)}>{label}</button>)}</div>
      <div className="agenda-toolbar__actions"><div className="calendar-filter" ref={filterRef}><button className="calendar-button" type="button" aria-expanded={filtersOpen} onClick={() => setFiltersOpen((current) => !current)}><Filter size={19} /> Filtros {(room || therapist) && <span className="calendar-filter__dot" />}</button>{filtersOpen && <div className="calendar-filter__popover"><div className="calendar-filter__title"><strong>Filtrar citas</strong><button type="button" className="calendar-icon-button" aria-label="Cerrar filtros" onClick={() => setFiltersOpen(false)}><X size={18} /></button></div><label>Sala<select value={room} onChange={(event) => setRoom(event.target.value)}><option value="">Todas las salas</option>{salas.map((s) => <option key={getId(s, 'Sala')} value={getRoomName(s)}>{getRoomName(s)}</option>)}</select></label><label>Terapeuta<select value={therapist} onChange={(event) => setTherapist(event.target.value)}><option value="">Todos los terapeutas</option>{terapeutas.map((t) => <option key={getId(t, 'Terapeuta')} value={getPersonName(t)}>{getPersonName(t)}</option>)}</select></label><button type="button" className="calendar-filter__clear" onClick={() => { setRoom(''); setTherapist(''); }}>Limpiar filtros</button></div>}</div><button className="calendar-button agenda-toolbar__print" type="button" onClick={openPrint}><Printer size={19} /> Imprimir</button></div>
    </div>
    <p className="agenda-page__period">{periodLabel}</p>
    {view !== 'month' && <MobileCalendar view={view} date={date} appointments={visible} onDateChange={setDate} onSelect={openDetails} onCreate={create} />}
    {view === 'month' ? <div className="calendar-month"><div className="calendar-month__grid">{['Dom', 'Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb'].map((label, index) => <div className={`calendar-month__weekday${(index === 0 || index === 6) ? ' calendar-month__weekday--closed' : ''}`} key={label}>{label}</div>)}{monthlyDays.map((day, index) => { if (!day) return <div className="calendar-month__day calendar-month__day--empty" key={`blank-${index}`} />; const key = dateKey(day); const items = appointmentsByDate.get(key) || []; return <button type="button" key={key} className={`calendar-month__day${day.getDay() === 0 || day.getDay() === 6 ? ' calendar-month__day--closed' : ''}${key === todayKey ? ' calendar-month__day--today' : ''}`} onClick={() => create(key)}><strong className="calendar-month__date-number">{day.getDate()}</strong>{items.slice(0, 3).map((item) => <span key={item.id} className={`calendar-month__event calendar-event--${item.color || 5}`}>{item.time} {item.patient}</span>)}{items.length > 3 && <small>+{items.length - 3} citas</small>}</button>; })}</div></div> : <CalendarGrid date={date} view={view} appointments={visible} onSelect={openDetails} onCreate={create} />}
    
    <section className={`agenda-print agenda-print--${printFormat} ${printBlackWhite ? 'agenda-print--black-white' : 'agenda-print--colors'}`} aria-label="Agenda para imprimir">
      <header className="agenda-print__heading"><h2>Agenda</h2><p>{printLabel}</p><span>{printItems.length} citas</span></header>
      {printFormat === 'list' ? (
        <table className="agenda-print__table">
          <thead><tr><th>Fecha</th><th>Horario</th><th>Paciente</th><th>Terapeuta</th><th>Sala</th></tr></thead>
          <tbody>{printItems.map((item) => (
            <tr key={item.id}><td>{item.date}</td><td>{item.time}</td><td>{item.patient}</td><td>{item.therapist}</td><td>{item.room}</td></tr>
          ))}</tbody>
        </table>
      ) : (
        <div className={`agenda-print__calendar agenda-print__calendar--${printPeriod}`}>
          <table className="agenda-print__timetable">
            <thead><tr><th className="agenda-print__hour-heading">Horario</th>{printDays.map((day) => <th key={dateKey(day)}>{day.getDate()}</th>)}</tr></thead>
            <tbody>{printTimes.map((time) => <tr key={time}><th scope="row">{time}</th>
              {printDays.map((day) => <td key={`${dateKey(day)}-${time}`}>
                {(printGrouped.get(dateKey(day)) || []).filter((item) => item.time === time).map((item) => <div className="agenda-print__event" key={item.id}><strong>{item.patient}</strong></div>)}
              </td>)}
            </tr>)}</tbody>
          </table>
        </div>
      )}
    </section>
    
    {printOpen && <div className="calendar-print-modal" role="presentation" onMouseDown={(event) => { if (event.target === event.currentTarget) setPrintOpen(false); }}>
      <div className="calendar-print-modal__panel" role="dialog" aria-modal="true" aria-labelledby="calendar-print-title" aria-describedby="calendar-print-description">
        <div className="calendar-print-modal__header"><h2 id="calendar-print-title">Imprimir agenda</h2><button type="button" className="calendar-icon-button" aria-label="Cerrar diálogo" onClick={() => setPrintOpen(false)}><X size={22}/></button></div>
        <p className="calendar-print-modal__description" id="calendar-print-description">Elige el formato de impresión.</p>
        <fieldset className="calendar-print-modal__choices">
          <legend>¿Qué deseas imprimir?</legend>
          <label className={printFormat === 'calendar' ? 'is-selected' : ''}><input type="radio" name="print-format" value="calendar" checked={printFormat === 'calendar'} onChange={() => setPrintFormat('calendar')}/><span><strong>Calendario</strong><small>Las citas organizadas por fecha.</small></span></label>
          <label className={printFormat === 'list' ? 'is-selected' : ''}><input type="radio" name="print-format" value="list" checked={printFormat === 'list'} onChange={() => setPrintFormat('list')}/><span><strong>Lista de citas</strong><small>Una tabla con los datos de cada cita.</small></span></label>
        </fieldset>
        <div className="calendar-print-modal__period"><span>Periodo del calendario</span><div role="group" aria-label="Periodo de impresión">{[['day', 'Día'], ['week', 'Semana'], ['month', 'Mes']].map(([value, label]) => <button type="button" key={value} className={printPeriod === value ? 'is-active' : ''} aria-pressed={printPeriod === value} onClick={() => setPrintPeriod(value)}>{label}</button>)}</div></div>
        {printPeriod === 'month' ? <div className="calendar-print-modal__month-fields">
          <label className="calendar-print-modal__field">Mes
            <select value={printMonthDate.getMonth()} onChange={(event) => setPrintDate(dateKey(new Date(printMonthDate.getFullYear(), Number(event.target.value), 1)))}>
              {Array.from({ length: 12 }, (_, index) => <option key={index} value={index}>{new Intl.DateTimeFormat('es-MX', { month: 'long' }).format(new Date(2026, index, 1))}</option>)}
            </select>
          </label>
          <label className="calendar-print-modal__field">Año
            <input type="number" min="1900" max="2100" value={printMonthDate.getFullYear()} onChange={(event) => { const year = Number(event.target.value); if (year >= 1900 && year <= 2100) setPrintDate(dateKey(new Date(year, printMonthDate.getMonth(), 1))); }}/>
          </label>
        </div> : <label className="calendar-print-modal__field">Fecha de referencia
          <input type="date" value={printDate} onChange={(event) => setPrintDate(event.target.value || dateKey(date))}/>
        </label>}
        <label className="calendar-print-modal__monochrome"><input type="checkbox" checked={printBlackWhite} onChange={(event) => setPrintBlackWhite(event.target.checked)}/> Imprimir en blanco y negro</label>
        <p className="calendar-print-modal__range">Se imprimirá:<strong>{printPeriod === 'day' ? longDate(printDays[0]) : printPeriod === 'week' ? `${longDate(printDays[0])} – ${longDate(printDays[6])}` : `${longDate(printDays[0])} – ${longDate(printDays[printDays.length - 1])}`}</strong></p>
        {invalidPrintRange ? <p className="calendar-print-modal__error" role="alert">La fecha final debe ser igual o posterior a la inicial.</p> : <p className="calendar-print-modal__summary" role="status">{printItems.length} {printItems.length === 1 ? 'cita encontrada' : 'citas encontradas'}.</p>}
        <p className="calendar-print-modal__notice">Se incluyen los datos necesarios de cada cita, pero nunca las notas privadas.</p>
        <div className="calendar-print-modal__actions">
          <button type="button" className="create-form__button create-form__button--secondary" onClick={() => setPrintOpen(false)}>Cancelar</button>
          <button type="button" className="create-form__button create-form__button--primary" onClick={printNow} disabled={invalidPrintRange}>Ver impresión</button>
        </div>      
      </div>
    </div>}
    
    <button type="button" className="agenda-page__fab" onClick={() => create()} aria-label="Nueva cita"><Plus size={28} /></button>
    
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
  </section>;
}

export default AgendaPage;
