function LoginBrand() {
    return (
      <div className="login-brand">
        <img
          src="/uady-logo-blue.svg"
          alt="Universidad Autónoma de Yucatán"
          className="login-brand__uady"
        />
  
        <div className="login-brand__product">
          <span className="login-brand__name">
            Psico<span>Gestión</span>
          </span>
  
          <span className="login-brand__subtitle">
            CLÍNICA PSICOLÓGICA
            <span aria-hidden="true"> </span>
            SEAP <span aria-hidden="true">•</span> UADY
          </span>
        </div>
      </div>
    );
  }
  
  export default LoginBrand;