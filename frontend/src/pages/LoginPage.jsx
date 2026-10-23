import LoginBrand from "../components/auth/LoginBrand";
import LoginForm from "../components/auth/LoginForm";
import "../styles/login.css";

function LoginPage() {
  return (
    <main className="login-page">
      <div className="login-container">
        <LoginBrand />

        <div className="login-card">
          <LoginForm />
        </div>
      </div>
    </main>
  );
}

export default LoginPage;