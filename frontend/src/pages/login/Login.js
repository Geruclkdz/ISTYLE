import './login.css';
import logo from "../../assets/LOGO.png";
import LoginOrRegister from "../../components/loginOrRegister";
import {useState} from "react";
import {useNavigate} from "react-router-dom";
import axios from "../../axiosConfig";
import {useAuth} from "../../utils/AuthContext";

const Login = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();
    const { login } = useAuth();

    const handleLogin = async (event) => {
        event.preventDefault();
        if (!email  || !password) {
            alert('Please fill in all fields.');
            return;
        }
        try {
            const response = await axios.post('/api/auth/login', {
                email: email,
                password: password,
            });
            localStorage.setItem('token', response.data.token);
            login();
            navigate('/wardrobe');
        } catch (error) {
            console.error('Login failed:', error.response.data);
            alert('Login failed');
        }
    };
    return (
        <>
            <div className="container">
                <div className="logo">
                    <img src={logo} alt="LOGO"/>
                </div>
                <LoginOrRegister />
                <form className="login" onSubmit={handleLogin}>
                        <input name="email" type="email" placeholder="e-mail" id="email" value={email} onChange={e => setEmail(e.target.value)}/>
                        <input name="password" type="password" placeholder="password" id="password" value={password} onChange={e => setPassword(e.target.value)}/>
                        <button id="submit" type="submit">SUBMIT</button>
                    </form>

            </div>
            </>
    )
};
export default Login;
