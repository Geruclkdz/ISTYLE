import './outfitCreator.css';
import Navigation from "../../components/navigation/Navigation";
import Section from "../../components/Section/Section";

const OutfitCreator = () => {
    return (
        <>
            <Navigation/>
            <Section text="CREATE YOUR OUTFIT">
                <div className="controls">
                    <span className="material-symbols-sharp">hr_resting</span>
                    <span className="material-symbols-sharp">change_circle</span>
                </div>
            </Section>
        </>
    )
};
export default OutfitCreator;
