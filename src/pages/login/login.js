import './login.css';
import logo from "../../assets/LOGO.png";
import LoginOrRegister from "../../components/loginOrRegister";
const App = () => {
    return (
        <>
            <div className="container">
                <div className="logo">
                    <img src={logo} alt="LOGO"/>
                </div>
                <LoginOrRegister />
                <form className="login" action="login" method="POST">
                        <input name="email" type="email" placeholder="e-mail"/>
                        <input name="password" type="password" placeholder="password"/>
                        <button id="submit" type="submit">SUBMIT</button>
                    </form>

            </div>
            </>
    )
};
export default App;
