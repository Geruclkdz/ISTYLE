import './navigation.css'
import logo from '../../assets/LOGO.png'
import {Link} from "react-router-dom";
import {useNavigate} from "react-router-dom";
import {useAuth} from "../../utils/AuthContext";

function Navigation() {

    const { logout } = useAuth();
    const navigate = useNavigate();
    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <>
            <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Material+Symbols+Sharp:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200"/>
            <nav>
                <img src={logo} alt="LOGO"/>
                <ul>
                    <li>
                        <Link to={"/wardrobe"}>
                            <span className="material-symbols-sharp">checkroom</span>
                        </Link>
                    </li>
                    <li>
                        <Link to={"/outfitCreator"}>
                        <span className="material-symbols-sharp">swap_horiz</span>
                            </Link>
                    </li>
                    <li>
                        <Link to={"/favourites"}>
                        <span className="material-symbols-sharp">hr_resting</span>
                            </Link>
                    </li>
                    <li>
                        <Link to={"/profile"}>
                        <span className="material-symbols-sharp">account_circle</span>
                            </Link>
                    </li>
                    <li>
                        <button className="logout_button" onClick={handleLogout}>Log Out</button>
                    </li>
                </ul>
            </nav>
        </>
    );
}

export default Navigation;
