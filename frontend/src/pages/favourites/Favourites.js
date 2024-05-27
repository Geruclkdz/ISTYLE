import './favourites.css';
import Navigation from "../../components/navigation/Navigation";
import Section from "../../components/Section/Section";
import Image from "../../components/Image/Image";
import image5 from '../../assets/5.png';


const Favourites = () => {
    return (
        <>
            <Navigation/>
            <Section text="FAVOURITES OUTFITS">
                    <Image>{image5}</Image>
            </Section>
        </>
    )
};
export default Favourites;
