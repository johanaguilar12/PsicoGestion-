import { CircleUserRound } from "lucide-react";

function PhotoUpload({ id = "photo", onChange }) {
  return (
    <div className="photo-upload">
      <CircleUserRound
        className="photo-upload__preview"
        size={128}
        strokeWidth={0.5}
        aria-hidden="true"
      />

      <label className="photo-upload__button" htmlFor={id}>
        Subir foto
      </label>

      <input
        id={id}
        name={id}
        className="photo-upload__input"
        type="file"
        accept=".jpg,.jpeg,.png,image/jpeg,image/png"
        onChange={onChange}
      />

      <p className="photo-upload__help">
        JPG, PNG, JPEG (máx. 2MB)
      </p>
    </div>
  );
}

export default PhotoUpload;