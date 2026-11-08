
import { useEffect, useState } from "react";
import { CircleUserRound } from "lucide-react";

function PhotoUpload({ id = "photo", onChange }) {
  const [photoPreview, setPhotoPreview] = useState(null);

  useEffect(() => {
    return () => {
      if (photoPreview) {
        URL.revokeObjectURL(photoPreview);
      }
    };
  }, [photoPreview]);

  const handlePhotoChange = (event) => {
    const file = event.target.files?.[0];

    if (file) {
      setPhotoPreview(URL.createObjectURL(file));
    } else {
      setPhotoPreview(null);
    }

    onChange?.(event);
  };

  return (
    <div className="photo-upload">
      {photoPreview ? (
        <img
          src={photoPreview}
          alt="Vista previa de la fotografía"
          className="photo-upload__preview photo-upload__preview--image"
        />
      ) : (
        <CircleUserRound
          className="photo-upload__preview"
          size={128}
          strokeWidth={0.5}
          aria-hidden="true"
        />
      )}

      <label className="photo-upload__button" htmlFor={id}>
        Subir foto
      </label>

      <input
        id={id}
        name={id}
        className="photo-upload__input"
        type="file"
        accept=".jpg,.jpeg,.png,image/jpeg,image/png"
        onChange={handlePhotoChange}
      />

      <p className="photo-upload__help">
        JPG, PNG, JPEG (máx. 1MB)
      </p>
    </div>
  );
}

export default PhotoUpload;
