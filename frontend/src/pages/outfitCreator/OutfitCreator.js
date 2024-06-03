import './outfitCreator.css';
import Navigation from "../../components/navigation/Navigation";
import Section from "../../components/Section/Section";
import axios from "../../axiosConfig";
import { useEffect, useState } from "react";
import Image from "../../components/Image/Image";

const OutfitCreator = () => {
    const [outfit, setOutfit] = useState({ top: null, bottom: null, shoes: null });

    const fetchData = async () => {
        try {
            const response = await axios.get('/api/outfits/create');
            const outfitData = response.data;

            if (!outfitData) {
                console.log('No outfit data received');
                return;
            }

            const top = outfitData.find(item => item.type.name === 'Tops');
            const bottom = outfitData.find(item => item.type.name === 'Bottoms');
            const shoes = outfitData.find(item => item.type.name === 'Shoes');

            setOutfit({ top, bottom, shoes });

        } catch (error) {
            console.error('Error fetching outfit:', error);
        }
    }

    useEffect(() => {
        fetchData();
    }, []);

    const saveOutfit = async () => {
        try {
            const clothes = [outfit.top, outfit.bottom, outfit.shoes]
            const outfitDTO = {
                clothes: clothes.map(item => ({
                    id: item.id,
                    src: item.src,
                    type: item.type,
                    categories: item.categories,
                    userId: item.userId
                }))};

            const response = await axios.post('/api/outfits/save', outfitDTO);
            console.log('Outfit saved:', response.data);
        } catch (error) {
            console.error('Error saving outfit:', error);
        }
    };

    return (
        <>
            <Navigation />
            <Section text="CREATE YOUR OUTFIT" className="outfitCreator">
                <div className="outfit-container">
                    {outfit.top && <Image imageSrc={outfit.top.src} alt={outfit.top.name} />}
                    {outfit.bottom && <Image imageSrc={outfit.bottom.src} alt={outfit.bottom.name} />}
                    {outfit.shoes && <Image imageSrc={outfit.shoes.src} alt={outfit.shoes.name} />}
                </div>
                <div className="controls">
                    <span className="material-symbols-sharp" onClick={saveOutfit}>hr_resting</span>
                    <span className="material-symbols-sharp" onClick={fetchData}>change_circle</span>
                </div>
            </Section>
        </>
    );
};

export default OutfitCreator;
