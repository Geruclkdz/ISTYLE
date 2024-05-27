import './Wardrobe.css';
import Navigation from "../../components/navigation/Navigation";
import Section from "../../components/Section/Section";
import Category from "../../components/Category/Category";
import Image from "../../components/Image/Image";
import {useEffect, useState} from "react";
import axios from "../../axiosConfig";

const Wardrobe = () => {
    const [clothes, setClothes] = useState([]);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await axios.get('/api/clothes');
                setClothes(response.data);

            } catch(error){
                console.error('Error fetching clothes:');
            }
        }

    fetchData();
    }, []);

return (
        <>
            <Navigation/>
            <Section text="YOUR WARDROBE" showAddButton={true}>
                <Category text="Shoes">
                    {clothes.map((clothesItem) => (
                        <Image key={clothesItem.id} imageSrc={clothesItem.src} />
                    ))}
                </Category>
                <Category text="Pants">
                </Category>
                <Category text="Shirts">
                </Category>
            </Section>
        </>
    )
};
export default Wardrobe;
