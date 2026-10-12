import { useEffect, useId, useRef, useState } from 'react';
import { ChevronDown, Pencil, Trash2, X } from 'lucide-react';
import { addMinutes, formatTime, longDate, parseDate } from './dateUtils';
import { ROOMS, THERAPISTS } from '../../data/calendarData';

const uniqueNames = (values) => [...new Set(values.filter((value) => typeof value === 'string' && value.trim()).map((value) => value.trim()))].sort((a, b) => a.localeCompare(b, 'es', { sensitivity: 'base' }));

function SearchSelect({ label, value, options, placeholder, onChange, error }) {
  const [open, setOpen] = useState(false);
  const [active, setActive] = useState(0);
  const root = useRef(null);
  const input = useRef(null);
  const id = useId();
  const filtered = options.filter((option) => option.toLocaleLowerCase('es').includes((value || '').toLocaleLowerCase('es')));

  useEffect(() => {
    const close = (event) => { if (!root.current?.contains(event.target)) setOpen(false); };
    document.addEventListener('pointerdown', close);
    return () => document.removeEventListener('pointerdown', close);
  }, []);

  const choose = (option) => { onChange(option); setOpen(false); setActive(0); };
  return (
    <div className="calendar-form__field calendar-search" ref={root}>
      <label className="calendar-form__label" htmlFor={id}>{label} <span className="calendar-form__required" aria-hidden="true">*</span></label>
      <div className="calendar-search__control">
        <input id={id} ref={input} type="text" autoComplete="off" role="combobox" aria-autocomplete="list" aria-expanded={open} aria-controls={`${id}-list`} aria-invalid={Boolean(error)} placeholder={placeholder} value={value || ''} onFocus={() => { setOpen(true); setActive(0); }} onChange={(event) => { onChange(event.target.value); setOpen(true); setActive(0); }} onKeyDown={(event) => {
          if (event.key === 'Escape') { event.stopPropagation(); setOpen(false); }
          if (event.key === 'ArrowDown') { event.preventDefault(); setOpen(true); setActive((current) => Math.min(current + 1, filtered.length - 1)); }
          if (event.key === 'ArrowUp') { event.preventDefault(); setActive((current) => Math.max(current - 1, 0)); }
          if (event.key === 'Enter' && open && filtered.length) { event.preventDefault(); choose(filtered[active] || filtered[0]); }
        }} />
        <button type="button" className="calendar-search__toggle" aria-label={`Mostrar opciones de ${label.toLowerCase()}`} aria-expanded={open} onClick={() => { if (!open) { input.current?.focus(); setOpen(true); } else setOpen(false); }}><ChevronDown size={20} /></button>
      </div>
      {open && <div id={`${id}-list`} role="listbox" className="calendar-search__options">
        {filtered.length ? filtered.map((option, index) => <button type="button" role="option" aria-selected={value === option} key={option} className={`calendar-search__option${index === active ? ' calendar-search__option--active' : ''}`} onMouseDown={(event) => event.preventDefault()} onClick={() => choose(option)}>{option}</button>) : <div className="calendar-search__empty">No se encontraron resultados</div>}
      </div>}
      {error && <span className="calendar-form__field-error">{error}</span>}
    </div>
  );
}

function AppointmentDialog({ appointment, mode, onClose, onSave, onDelete, appointments = [] }) {
  const [draft, setDraft] = useState(appointment || {});
  const [confirmDelete, setConfirmDelete] = useState(false);
  const [error, setError] = useState('');
  const [invalid, setInvalid] = useState(false);
  useEffect(() => { setDraft(appointment || {}); setConfirmDelete(false); setError(''); setInvalid(false); }, [appointment, mode]);
  useEffect(() => {
    if (!appointment) return undefined;
    const previous = document.body.style.overflow;
    document.body.style.overflow = 'hidden';
    const escape = (event) => { if (event.key === 'Escape') onClose(); };
    document.addEventListener('keydown', escape);
    return () => { document.body.style.overflow = previous; document.removeEventListener('keydown', escape); };
  }, [appointment, onClose]);
  if (!appointment) return null;
  const edit = mode === 'edit' || mode === 'create';
  const patients = uniqueNames(appointments.map((item) => item.patient));
  const therapists = uniqueNames([...THERAPISTS, ...appointments.map((item) => item.therapist)]);
  const rooms = uniqueNames([...ROOMS, ...appointments.map((item) => item.room)]);
  const update = (key, value) => { setDraft((current) => ({ ...current, [key]: value })); setError(''); };
  const missing = (key) => invalid && !String(draft[key] || '').trim();
  const save = (event) => {
    event.preventDefault();
    setInvalid(true);
    if (!draft.patient?.trim() || !draft.therapist?.trim() || !draft.room?.trim() || !draft.date || !draft.time) { setError('Completa los campos obligatorios para guardar la cita.'); return; }
    if (!patients.includes(draft.patient.trim()) && patients.length) { setError('Selecciona un paciente de la lista.'); return; }
    if (!therapists.includes(draft.therapist.trim())) { setError('Selecciona un terapeuta de la lista.'); return; }
    if (!rooms.includes(draft.room.trim())) { setError('Selecciona una sala de la lista.'); return; }
    const chosenDate = new Date(`${draft.date}T12:00:00`);
    if (!/^\d{4}-\d{2}-\d{2}$/.test(draft.date || '') || Number.isNaN(chosenDate.getTime()) || chosenDate.toISOString().slice(0, 10) !== draft.date) { setError('Selecciona una fecha válida.'); return; }
    if (!/^([01]\d|2[0-3]):[0-5]\d$/.test(draft.time || '')) { setError('Selecciona un horario válido.'); return; }
    const conflict = appointments.some((item) => item.id !== draft.id && item.date === draft.date && item.time === draft.time && (item.room === draft.room || item.therapist === draft.therapist));
    if (conflict) { setError('Ya existe una cita en ese horario para la sala o el terapeuta seleccionado.'); return; }
    onSave({ ...draft, patient: draft.patient.trim(), therapist: draft.therapist.trim(), room: draft.room.trim(), color: draft.color });
  };
  return <div className="calendar-modal" role="presentation" onMouseDown={(event) => { if (event.target === event.currentTarget) onClose(); }}>
    <section className="calendar-modal__panel" role="dialog" aria-modal="true" aria-labelledby="calendar-modal-title">
      <header className="calendar-modal__header"><h2 id="calendar-modal-title">{mode === 'create' ? 'Nueva cita' : edit ? 'Editar cita' : 'Detalle de la cita'}</h2><button type="button" className="calendar-icon-button" onClick={onClose} aria-label="Cerrar"><X size={22} /></button></header>
      {edit ? <form className="calendar-form" onSubmit={save} noValidate>
        <SearchSelect label="Nombre del paciente" placeholder="Buscar paciente por nombre" value={draft.patient} options={patients} onChange={(value) => update('patient', value)} error={missing('patient') ? 'Selecciona un paciente.' : ''} />
        <SearchSelect label="Terapeuta" placeholder="Buscar terapeuta por nombre" value={draft.therapist} options={therapists} onChange={(value) => update('therapist', value)} error={missing('therapist') ? 'Selecciona un terapeuta.' : ''} />
        <div className="calendar-form__field"><label className="calendar-form__label" htmlFor="appointment-room">Sala <span className="calendar-form__required" aria-hidden="true">*</span></label><select className="calendar-form__select" id="appointment-room" value={draft.room || ''} aria-invalid={Boolean(missing('room'))} onChange={(event) => update('room', event.target.value)}><option value="">Seleccionar sala</option>{rooms.map((room) => <option key={room} value={room}>{room}</option>)}</select>{missing('room') && <span className="calendar-form__field-error">Selecciona una sala.</span>}</div>
        <div className="calendar-form__split"><div className="calendar-form__field"><label className="calendar-form__label" htmlFor="appointment-date">Fecha <span className="calendar-form__required" aria-hidden="true">*</span></label><input id="appointment-date" required type="date" value={draft.date || ''} aria-invalid={Boolean(missing('date'))} onChange={(event) => update('date', event.target.value)} />{missing('date') && <span className="calendar-form__field-error">Selecciona una fecha.</span>}</div><div className="calendar-form__field"><label className="calendar-form__label" htmlFor="appointment-time">Horario <span className="calendar-form__required" aria-hidden="true">*</span></label><select id="appointment-time" value={draft.time || ''} aria-invalid={Boolean(missing('time'))} onChange={(event) => update('time', event.target.value)}>{Array.from({ length: 26 }, (_, i) => `${String(7 + Math.floor(i / 2)).padStart(2, '0')}:${i % 2 ? '30' : '00'}`).map((time) => <option key={time} value={time}>{formatTime(time)}</option>)}</select>{missing('time') && <span className="calendar-form__field-error">Selecciona un horario.</span>}</div></div>
        <div className="calendar-form__field"><label className="calendar-form__label" htmlFor="appointment-notes">Notas</label><textarea id="appointment-notes" rows={3} value={draft.notes || ''} onChange={(event) => update('notes', event.target.value)} /></div>
        <p className="calendar-form__notice"><span className="calendar-form__required">*</span> Campos obligatorios</p>
        {error && <p className="calendar-form__error" role="alert">{error}</p>}
        <footer className="calendar-modal__actions"><button className="calendar-button" type="button" onClick={onClose}>Cancelar</button><button className="calendar-button calendar-button--primary" type="submit">Guardar</button></footer>
      </form> : <div className="calendar-detail"><p><strong>Paciente:</strong> {appointment.patient}</p><p><strong>Terapeuta:</strong> {appointment.therapist}</p><p><strong>Sala:</strong> {appointment.room}</p><p><strong>Fecha:</strong> {longDate(parseDate(appointment.date))}</p><p><strong>Horario:</strong> {formatTime(appointment.time)} – {formatTime(addMinutes(appointment.time, 30))}</p>{appointment.notes && <p><strong>Notas:</strong> {appointment.notes}</p>}
        {confirmDelete ? <div className="calendar-confirm" role="alertdialog" aria-label="Confirmar eliminación"><p>¿Eliminar esta cita? Esta acción no se puede deshacer.</p><div className="calendar-modal__actions"><button className="calendar-button calendar-button--danger" type="button" onClick={() => onDelete(appointment.id)}>Sí, eliminar</button><button className="calendar-button" type="button" onClick={() => setConfirmDelete(false)}>Cancelar</button></div></div> : <footer className="calendar-modal__actions"><button className="calendar-button calendar-button--danger-outline" type="button" onClick={() => setConfirmDelete(true)}><Trash2 size={17} /> Eliminar</button><button className="calendar-button calendar-button--primary" type="button" onClick={() => onSave(null, 'edit')}><Pencil size={17} /> Editar</button></footer>}
      </div>}
    </section>
  </div>;
}
export default AppointmentDialog;
