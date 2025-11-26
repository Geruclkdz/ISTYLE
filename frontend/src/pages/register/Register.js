import './register.css';
import logo from "../../assets/LOGO.png";
import LoginOrRegister from "../../components/loginOrRegister";
import axios from "../../axiosConfig"
import {useNavigate} from "react-router-dom";
import {useState} from "react";

const Register = () => {
    const [email, setEmail] = useState('');
    const [username, setUsername] = useState('');
    const [name, setName] = useState('');
    const [surname, setSurname] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();

    const handleRegister = async (event) => {
        event.preventDefault();
        if (!email || !name || !surname || !password || !username) {
            alert('Please fill in all fields.');
            return;
        }
        try {
            const response = await axios.post('/api/auth/register', {
                email: email,
                name: name,
                surname: surname,
                password: password,
                username: username
            });
            localStorage.setItem('token', response.data.token);
            navigate('/wardrobe');
        } catch (error) {
            console.error('Registration failed:', error.response.data);
            alert('Registration failed');
        }
    };


    return (
        <>
            <div className="register-page">
                <div className="logo">
                    <img src={logo} alt="LOGO"/>
                </div>
                <LoginOrRegister />
                <form className="login" onSubmit={handleRegister}>
                    <input name="username" type="text" placeholder="username" id="username"
                           onChange={e => setUsername(e.target.value)}/>
                    <input name="name" type="text" placeholder="name" id="name"
                           onChange={e => setName(e.target.value)}/>
                    <input name="surname" type="text" placeholder="surname" id="surname"
                           onChange={e => setSurname(e.target.value)}/>
                    <input name="email" type="email" placeholder="e-mail" id="email"
                           onChange={e => setEmail(e.target.value)}/>
                    <input name="password" type="password" placeholder="password" id="password"
                           onChange={e => setPassword(e.target.value)}/>
                    <button id="submit" type="submit">SUBMIT</button>
                </form>

            </div>
        </>
    )
};
export default Register;
