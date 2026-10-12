import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { ChevronLeft, ChevronRight, Filter, Plus, Printer, X } from 'lucide-react';
import { useSearchParams } from 'react-router-dom';
import CalendarGrid from '../components/calendar/CalendarGrid';
import MobileCalendar from '../components/calendar/MobileCalendar';
import AppointmentDialog from '../components/calendar/AppointmentDialog';
import { addDays, addMinutes, dateKey, formatTime, longDate, monthStart, parseDate, weekStart } from '../components/calendar/dateUtils';
import { DEMO_APPOINTMENTS, ROOMS, THERAPISTS } from '../data/calendarData';
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

function AgendaPage() {
  const [searchParams, setSearchParams] = useSearchParams();
  const [date, setDate] = useState(() => new Date());
  const [view, setView] = useState('week');
  const [printBlackWhite, setPrintBlackWhite] = useState(() => {
    try { return JSON.parse(localStorage.getItem('psicogestion-agenda-print') || '{}').blackWhite === true; } catch { return false; }
  });
  const [appointments, setAppointments] = useState(DEMO_APPOINTMENTS);
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
  const printPeriod = printSettings[printFormat].period;
  const printDate = printSettings[printFormat].date;
  const setPrintPeriod = (period) => setPrintSettings((current) => ({ ...current, [printFormat]: { ...current[printFormat], period } }));
  const setPrintDate = (date) => setPrintSettings((current) => ({ ...current, [printFormat]: { ...current[printFormat], date } }));
  useEffect(() => {
    const settings = { format: printFormat, blackWhite: printBlackWhite, ...printSettings };
    try { localStorage.setItem('psicogestion-agenda-print', JSON.stringify(settings)); } catch { }
  }, [printFormat, printSettings, printBlackWhite]);
  const [room, setRoom] = useState('');
  const [therapist, setTherapist] = useState('');
  const filterRef = useRef(null);
  const closeDialog = useCallback(() => setSelected(null), []);
  const visible = useMemo(() => appointments.filter((item) => (!room || item.room === room) && (!therapist || item.therapist === therapist)), [appointments, room, therapist]);
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
  const printTimes = useMemo(() => {
    const times = Array.from({ length: 24 }, (_, index) => {
      const minutes = 7 * 60 + index * 30;
      return `${String(Math.floor(minutes / 60)).padStart(2, '0')}:${String(minutes % 60).padStart(2, '0')}`;
    });
    return times;
  }, []);
  const printItems = useMemo(() => visible.filter((item) => printDayKeys.has(item.date)).sort((a, b) => a.date.localeCompare(b.date) || a.time.localeCompare(b.time)), [visible, printDayKeys]);
  const invalidPrintRange = false;
  const printGrouped = useMemo(() => {
    const grouped = new Map();
    printItems.forEach((item) => grouped.set(item.date, [...(grouped.get(item.date) || []), item]));
    return grouped;
  }, [printItems]);
  const printLabel = printPeriod === 'day'
    ? longDate(parseDate(printDate))
    : printPeriod === 'week'
      ? `${longDate(printDays[0])} – ${longDate(printDays[6])}`
      : new Intl.DateTimeFormat('es-MX', { month: 'long', year: 'numeric' }).format(parseDate(printDate));
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
  const create = (day = dateKey(date), time = '09:00') => { setSelected({ date: day, time, patient: '', therapist: '', room: '', notes: '' }); setDialogMode('create'); };
  useEffect(() => {
    if (searchParams.get('nueva') !== '1') return;
    create(dateKey(new Date()));
    const nextParams = new URLSearchParams(searchParams);
    nextParams.delete('nueva');
    setSearchParams(nextParams, { replace: true });
  }, [searchParams, setSearchParams]);
  const save = (item, nextMode) => { if (nextMode === 'edit') { setDialogMode('edit'); return; } setAppointments((current) => item.id ? current.map((entry) => entry.id === item.id ? { ...item, color: entry.color } : entry) : [...current, { ...item, color: (current.length % 5) + 1, id: globalThis.crypto?.randomUUID?.() || `new-${Date.now()}` }]); setSelected(null); };
  const remove = (id) => { setAppointments((current) => current.filter((item) => item.id !== id)); setSelected(null); };
  const openDetails = (item) => { setSelected(item); setDialogMode('detail'); };
  const changeView = (next) => setView(next);
  return <section className={`agenda-page agenda-page--${view}${view === 'day' && (date.getDay() === 0 || date.getDay() === 6) ? ' agenda-page--closed-day' : ''}`}>
    <header className="agenda-page__header"><h1 className="page-title">Agenda</h1><button className="data-page__create agenda-page__new" type="button" onClick={() => create()}><Plus size={20} /> Nueva cita</button></header>
    <div className="agenda-toolbar"><div className="agenda-toolbar__nav"><button type="button" className="calendar-icon-button" onClick={() => changePeriod(-1)} aria-label="Periodo anterior"><ChevronLeft size={21} /></button><button type="button" className="agenda-toolbar__today" onClick={() => setDate(new Date())}>Hoy</button><button type="button" className="calendar-icon-button" onClick={() => changePeriod(1)} aria-label="Periodo siguiente"><ChevronRight size={21} /></button></div>
      <div className="agenda-toolbar__views" role="group" aria-label="Vista del calendario">{[['day', 'Día'], ['week', 'Semana'], ['month', 'Mes']].map(([value, label]) => <button key={value} type="button" aria-pressed={view === value} className={view === value ? 'is-active' : ''} onClick={() => changeView(value)}>{label}</button>)}</div>
      <div className="agenda-toolbar__actions"><div className="calendar-filter" ref={filterRef}><button className="calendar-button" type="button" aria-expanded={filtersOpen} onClick={() => setFiltersOpen((current) => !current)}><Filter size={19} /> Filtros {(room || therapist) && <span className="calendar-filter__dot" />}</button>{filtersOpen && <div className="calendar-filter__popover"><div className="calendar-filter__title"><strong>Filtrar citas</strong><button type="button" className="calendar-icon-button" aria-label="Cerrar filtros" onClick={() => setFiltersOpen(false)}><X size={18} /></button></div><label>Sala<select value={room} onChange={(event) => setRoom(event.target.value)}><option value="">Todas las salas</option>{ROOMS.map((name) => <option key={name}>{name}</option>)}</select></label><label>Terapeuta<select value={therapist} onChange={(event) => setTherapist(event.target.value)}><option value="">Todos los terapeutas</option>{THERAPISTS.map((name) => <option key={name}>{name}</option>)}</select></label><button type="button" className="calendar-filter__clear" onClick={() => { setRoom(''); setTherapist(''); }}>Limpiar filtros</button></div>}</div><button className="calendar-button agenda-toolbar__print" type="button" onClick={openPrint}><Printer size={19} /> Imprimir</button></div>
    </div>
    <p className="agenda-page__period">{periodLabel}</p>
    {view !== 'month' && <MobileCalendar view={view} date={date} appointments={visible} onDateChange={setDate} onSelect={openDetails} onCreate={create} />}
    {view === 'month' ? <div className="calendar-month"><div className="calendar-month__grid">{['Dom', 'Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb'].map((label, index) => <div className={`calendar-month__weekday${(index === 0 || index === 6) ? ' calendar-month__weekday--closed' : ''}`} key={label}>{label}</div>)}{monthlyDays.map((day, index) => { if (!day) return <div className="calendar-month__day calendar-month__day--empty" key={`blank-${index}`} />; const key = dateKey(day); const items = appointmentsByDate.get(key) || []; return <button type="button" key={key} className={`calendar-month__day${day.getDay() === 0 || day.getDay() === 6 ? ' calendar-month__day--closed' : ''}${key === todayKey ? ' calendar-month__day--today' : ''}`} onClick={() => create(key)}><strong className="calendar-month__date-number">{day.getDate()}</strong>{items.slice(0, 3).map((item) => <span key={item.id} className={`calendar-month__event calendar-event--${item.color || 5}`}>{item.time} {item.patient}</span>)}{items.length > 3 && <small>+{items.length - 3} citas</small>}</button>; })}</div></div> : <CalendarGrid date={date} view={view} appointments={visible} onSelect={openDetails} onCreate={create} />}
    <section className={`agenda-print agenda-print--${printFormat} ${printBlackWhite ? 'agenda-print--black-white' : 'agenda-print--colors'}`} aria-label="Agenda para imprimir">
      <header className="agenda-print__heading">
        <h2>Agenda</h2>
        <p>{printLabel}</p>
        <span>{printItems.length} {printItems.length === 1 ? 'cita' : 'citas'}</span>
      </header>
      {printFormat === 'list' ? (
        <table className="agenda-print__table">
          <thead><tr><th>Fecha</th><th>Horario</th><th>Paciente</th><th>Terapeuta</th><th>Sala</th></tr></thead>
          <tbody>{printItems.length ? printItems.map((item) => (
            <tr key={item.id}>
              <td>{new Intl.DateTimeFormat('es-MX', { day: '2-digit', month: 'short', year: 'numeric' }).format(parseDate(item.date))}</td>
              <td>{formatTime(item.time)} – {formatTime(addMinutes(item.time, 30))}</td>
              <td className="agenda-print__patient-cell"><span className={`agenda-print__patient calendar-event--${item.color || 5}`}>{item.patient}</span></td>
              <td>{item.therapist}</td>
              <td>{item.room}</td>
            </tr>
          )) : <tr><td colSpan={5}>No hay citas registradas en este periodo.</td></tr>}</tbody>
        </table>
      ) : (
        <div className={`agenda-print__calendar agenda-print__calendar--${printPeriod}`}>
          {printPeriod === 'month' ? (
            <>
              <div className="agenda-print__weekdays">{['Dom', 'Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb'].map((day, index) => <strong className={(index === 0 || index === 6) ? 'agenda-print__weekday--closed' : ''} key={day}>{day}</strong>)}</div>
              <div className="agenda-print__days">
                {Array.from({ length: printDays[0].getDay() }, (_, index) => <div className="agenda-print__day agenda-print__day--blank" key={`blank-${index}`} />)}
                {printDays.map((day) => <div className={`agenda-print__day${day.getDay() === 0 || day.getDay() === 6 ? ' agenda-print__day--closed' : ''}`} key={dateKey(day)}>
                  <h3>{day.getDate()}</h3>
                  <div className="agenda-print__events">{(printGrouped.get(dateKey(day)) || []).map((item) => <div className={`agenda-print__event calendar-event--${item.color || 5}`} key={item.id}>
                    <strong>{formatTime(item.time)}</strong><span>{item.patient}</span>
                  </div>)}</div>
                </div>)}
              </div>
            </>
          ) : (
            <table className="agenda-print__timetable">
              <thead><tr><th className="agenda-print__hour-heading">Horario</th>{printDays.map((day) => <th className={day.getDay() === 0 || day.getDay() === 6 ? 'agenda-print__closed' : ''} key={dateKey(day)}>{new Intl.DateTimeFormat('es-MX', { weekday: 'short', day: 'numeric', month: 'short' }).format(day)}</th>)}</tr></thead>
              <tbody>{printTimes.map((time) => <tr key={time}>
                <th scope="row">{formatTime(time)}</th>
                {printDays.map((day) => <td className={day.getDay() === 0 || day.getDay() === 6 ? 'agenda-print__closed' : ''} key={`${dateKey(day)}-${time}`}>
                  {(printGrouped.get(dateKey(day)) || []).filter((item) => item.time.slice(0, 5) === time).map((item) => <div className={`agenda-print__event calendar-event--${item.color || 5}`} key={item.id}>
                    <strong>{item.patient}</strong>
                    <span>{formatTime(item.time)} – {formatTime(addMinutes(item.time, 30))}</span>
                    <small>{item.therapist}</small>
                    <small>{item.room}</small>
                  </div>)}
                </td>)}
              </tr>)}</tbody>
            </table>
          )}
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
  <button
    type="button"
    className="create-form__button create-form__button--secondary"
    onClick={() => setPrintOpen(false)}
  >
    Cancelar
  </button>

  <button
    type="button"
    className="create-form__button create-form__button--primary"
    onClick={printNow}
    disabled={invalidPrintRange}
  >
    Ver impresión
  </button>
</div>      </div>
    </div>}
    <button type="button" className="agenda-page__fab" onClick={() => create()} aria-label="Nueva cita"><Plus size={28} /></button>
    <AppointmentDialog appointment={selected} mode={dialogMode} onClose={closeDialog} onSave={save} onDelete={remove} appointments={appointments} />
  </section>;
}
export default AgendaPage;
