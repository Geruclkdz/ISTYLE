import './wardrobe.css';
import Navigation from "../../components/navigation/navigation";
import Section from "../../components/Section/Section";
import Category from "../../components/Category/Category";
import image1 from '../../assets/1.png';
import image2 from '../../assets/2.png';
import image3 from '../../assets/3.png';
import Image from "../../components/Image/Image";

const App = () => {
    return (
        <>
            <Navigation/>
            <Section text="YOUR WARDROBE">
                <Category text="Shoes">
                    <Image>{image3}</Image>
                    <Image>{image3}</Image>
                    <Image>{image3}</Image>
                    <Image>{image3}</Image>
                </Category>
                <Category text="Pants">
                    <Image>{image2}</Image>
                </Category>
                <Category text="Shirts">
                    <Image>{image1}</Image>
                    <Image>{image1}</Image>
                    <Image>{image1}</Image>
                    <Image>{image1}</Image>
                    <Image>{image1}</Image>
                </Category>
            </Section>
        </>
    )
};
export default App;
