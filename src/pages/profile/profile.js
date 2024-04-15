import './profile.css';
import Navigation from "../../components/navigation/navigation";
import Section from "../../components/Section/Section";
import image4 from "../../assets/4.jpg";
import image5 from "../../assets/5.png";

const App = () => {
    return (
        <>
            <Navigation/>
            <div className='page'>
            <Section text="PROFILE">
                <img className="profilePic" src={image4} alt="profilePic"/>
                <text className="description"> Jestem Adam i mam fajnego pieska</text>
            </Section>
            <Section text="FEED">
                <img className="fitPic" src={image5} alt="profilePic"/>
                <text className="description"> Jestem Adam i mam fajnego pieska</text>
            </Section>
            </div>
        </>
    )
};
export default App;
