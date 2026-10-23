function DetailField({ label, value, className = "" }) {
    return (
      <div className={`form-field detail-field ${className}`}>
        <span className="form-field__label">{label}</span>
  
        <div className="form-field__input detail-field__value">
          {value || "—"}
        </div>
      </div>
    );
  }
  
  export default DetailField;