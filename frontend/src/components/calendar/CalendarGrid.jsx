import { addDays, addMinutes, dateKey, formatTime, shortWeekday, slots, weekStart } from './dateUtils';

function CalendarGrid({ date, view, appointments, onSelect, onCreate }) {
  const dates = view === 'day' ? [date] : Array.from({ length: 7 }, (_, index) => addDays(weekStart(date), index));
  const todayKey = dateKey(new Date());
  const selectedKey = dateKey(date);
  return (
    <div className="calendar-scroll" role="region" aria-label="Calendario de citas" tabIndex={0}>
      <div className="calendar-grid" style={{ '--calendar-days': dates.length }}>
        <div className="calendar-grid__corner" />
        {dates.map((day) => <div key={dateKey(day)} className={`calendar-grid__day${dateKey(day) === selectedKey ? ' calendar-grid__day--selected' : ''}${dateKey(day) === todayKey ? ' calendar-grid__day--today' : ''}`}><span>{shortWeekday(day)}</span> <span className="calendar-grid__date-number">{day.getDate()}</span></div>)}
        {slots.map((time) => <div className="calendar-grid__row" key={time}>
          <div className="calendar-grid__time">{formatTime(time)}</div>
          {dates.map((day) => {
            const key = dateKey(day);
            const matches = appointments.filter((item) => item.date === key && item.time === time);
            return <div className="calendar-grid__cell" key={`${key}-${time}`}>
              <button className="calendar-grid__empty" type="button" aria-label={`Crear cita el ${key} a las ${formatTime(time)}`} onClick={() => onCreate(key, time)} />
              {matches.map((item) => <button key={item.id} type="button" className={`calendar-event calendar-event--${item.color || 1}`} onClick={() => onSelect(item)} aria-label={`Ver cita de ${item.patient}, ${formatTime(item.time)}, ${item.room}`}>
                <span>{item.time}–{addMinutes(item.time, 30)}</span><strong>{item.patient}</strong><span>{item.room}</span>
              </button>)}
            </div>;
          })}
        </div>)}
      </div>
    </div>
  );
}
export default CalendarGrid;
