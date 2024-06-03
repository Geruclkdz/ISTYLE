import { useNavigate } from "react-router-dom";

function LoginOrRegister() {
    const navigate = useNavigate();

    const handleLoginClick = () => {
        navigate('/login');
    };

    const handleRegisterClick = () => {
        navigate('/register');
    };

    return (
        <div className="login-or-register">
            <button id="login" onClick={handleLoginClick}>LOGIN</button>
            <button id="register" onClick={handleRegisterClick}>REGISTER</button>
        </div>
    );
}

export default LoginOrRegister;