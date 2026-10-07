import Breadcrumb from "./Breadcrumb";
import FormNotice from "./FormNotice";
import "../../styles/forms.css";

function CreateFormPage({
  breadcrumb = [],
  title,
  children,
  onSubmit,
  onCancel,
}) {
  return (
    <section className="create-page">
      <Breadcrumb items={breadcrumb} />

      <h1 className="page-title create-page__title">
        {title}
      </h1>

      <form
        className="create-form"
        onSubmit={onSubmit}
      >
        <div className="create-form__content">
          {children}
        </div>

        <FormNotice />

        <div className="create-form__actions">
          <button
            type="submit"
            className="
              create-form__button
              create-form__button--primary
            "
          >
            Guardar
          </button>

          <button
            type="button"
            className="
              create-form__button
              create-form__button--secondary
            "
            onClick={onCancel}
          >
            Cancelar
          </button>
        </div>
      </form>
    </section>
  );
}

export default CreateFormPage;