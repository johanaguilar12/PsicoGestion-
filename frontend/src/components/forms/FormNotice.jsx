import { Info } from "lucide-react";

function FormNotice() {
  return (
    <div className="form-notice" role="note">
      <Info
        className="form-notice__icon"
        size={20}
        strokeWidth={1.8}
        aria-hidden="true"
      />

      <p>
        Todos los campos marcados con <strong>*</strong> son obligatorios.
      </p>
    </div>
  );
}

export default FormNotice;