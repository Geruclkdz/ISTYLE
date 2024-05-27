import './outfitCreator.css';
import Navigation from "../../components/navigation/Navigation";
import Section from "../../components/Section/Section";
import image1 from '../../assets/1.png';
import image2 from '../../assets/2.png';
import image3 from '../../assets/3.png';
import Image from "../../components/Image/Image";

const OutfitCreator = () => {
    return (
        <>
            <Navigation/>
            <Section text="CREATE YOUR OUTFIT">
                <Image>
                    {image1}
                    {image2}
                    {image3}
                </Image>
                <div className="controls">
                    <span className="material-symbols-sharp">hr_resting</span>
                    <span className="material-symbols-sharp">change_circle</span>
                </div>
            </Section>
        </>
    )
};
export default OutfitCreator;
