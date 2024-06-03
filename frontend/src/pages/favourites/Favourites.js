import './favourites.css';
import Navigation from "../../components/navigation/Navigation";
import Section from "../../components/Section/Section";
import Outfit from "../../components/Outfit/Outfit";
import { useEffect, useState } from "react";
import axios from "../../axiosConfig";

const Favourites = () => {
    const [outfits, setOutfits] = useState([]);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await axios.get('/api/outfits');
                const outfitsData = response.data;

                setOutfits(outfitsData);
            } catch (error) {
                console.error('Error fetching outfits:', error);
            }
        };

        fetchData();
    }, []);

    return (
        <>
            <Navigation />
            <Section text="FAVOURITES OUTFITS" className="favourites">

                {outfits.map((outfit, index) => (
                    <Outfit key={index} outfit={outfit} />
                ))}
            </Section>
        </>
    )
};
export default Favourites;
