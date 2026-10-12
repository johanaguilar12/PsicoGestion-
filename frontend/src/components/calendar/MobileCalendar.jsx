import { addDays, addMinutes, dateKey, formatTime, shortWeekday, slots, weekStart } from './dateUtils';

const capitalize = (value) => value.charAt(0).toUpperCase() + value.slice(1);

function MobileCalendar({ view, date, appointments, onDateChange, onSelect, onCreate }) {
  const selectedKey = dateKey(date);
  const todayKey = dateKey(new Date());
  const days = Array.from({ length: 7 }, (_, index) => addDays(weekStart(date), index));
  const dailyAppointments = appointments.filter((item) => item.date === selectedKey);
  const dateLabel = capitalize(new Intl.DateTimeFormat('es-MX', { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' }).format(date));

  return (
    <section className={`mobile-calendar mobile-calendar--${view}`} aria-label="Agenda diaria">
      <div className="mobile-calendar__week" role="group" aria-label="Seleccionar día">
        {days.map((day) => {
          const key = dateKey(day);
          const selected = key === selectedKey;
          const count = appointments.filter((item) => item.date === key).length;
          return (
            <button key={key} type="button" className={`mobile-calendar__day${selected ? ' is-selected' : ''}${key === todayKey ? ' is-today' : ''}`} aria-pressed={selected} aria-label={`${capitalize(new Intl.DateTimeFormat('es-MX', { dateStyle: 'full' }).format(day))}, ${count} citas`} onClick={() => onDateChange(day)}>
              <span className="mobile-calendar__weekday">{shortWeekday(day).replace('.', '').slice(0, 3)}</span>
              <span className="mobile-calendar__day-number">{day.getDate()}</span>
              <span className={`mobile-calendar__dot${count ? ' has-events' : ''}`} aria-hidden="true" />
            </button>
          );
        })}
      </div>
      <div className="mobile-calendar__heading">
        <h2>{dateLabel}</h2>
        <span>{dailyAppointments.length} {dailyAppointments.length === 1 ? 'cita' : 'citas'}</span>
      </div>
      <div className="mobile-calendar__timeline">
        {slots.map((time) => {
          const matches = dailyAppointments.filter((item) => item.time === time);
          return (
            <div className="mobile-calendar__slot" key={time}>
              <span className="mobile-calendar__time">{formatTime(time)}</span>
              <div className="mobile-calendar__slot-content">
                <button type="button" className="mobile-calendar__empty" onClick={() => onCreate(selectedKey, time)} aria-label={`Crear cita a las ${formatTime(time)}`} />
                {matches.map((item) => (
                  <button key={item.id} type="button" className={`mobile-calendar__event calendar-event--${item.color || 1}`} onClick={() => onSelect(item)} aria-label={`Ver cita de ${item.patient}, ${formatTime(item.time)}, ${item.room}`}>
                    <span className="mobile-calendar__event-time">{item.time}–{addMinutes(item.time, 30)}</span>
                    <strong>{item.patient}</strong>
                    <span>{item.room}</span>
                  </button>
                ))}
              </div>
            </div>
          );
        })}
      </div>
    </section>
  );
}

export default MobileCalendar;
