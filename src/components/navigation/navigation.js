import './navigation.css'
import logo from '../../assets/LOGO.png'
function navigation() {
    return (
        <>
            <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Material+Symbols+Sharp:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200"/>
            <nav>
                <img src={logo} alt="LOGO"/>
                <ul>
                    <li>
                        <span className="material-symbols-sharp">checkroom</span>
                    </li>
                    <li>
                        <span className="material-symbols-sharp">swap_horiz</span>
                    </li>
                    <li>
                        <span className="material-symbols-sharp">hr_resting</span>
                    </li>
                    <li>
                        <span className="material-symbols-sharp">account_circle</span>
                    </li>
                    <li>
                        <a className="button" href="/logout"> Log Out </a>
                    </li>
                </ul>
            </nav>
        </>
    );
}

export default navigation;
