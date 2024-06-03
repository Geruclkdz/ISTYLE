import './Wardrobe.css';
import Navigation from "../../components/navigation/Navigation";
import Section from "../../components/Section/Section";
import Type from "../../components/Type/Type";
import Image from "../../components/Image/Image";
import { useEffect, useState } from "react";
import axios from "../../axiosConfig";

const Wardrobe = () => {
    const [clothesByType, setClothesByType] = useState({});

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await axios.get('/api/clothes');
                const clothesData = response.data;

                const clothesByType = clothesData.reduce((acc, item) => {
                    const typeName = item.type.name;
                    if (!acc[typeName]) {
                        acc[typeName] = [];
                    }
                    acc[typeName].push(item);
                    return acc;
                }, {});

                setClothesByType(clothesByType);
            } catch (error) {
                console.error('Error fetching clothes:', error);
            }
        };

        fetchData();
    }, []);

    return (
        <>
            <Navigation />
            <Section text="YOUR WARDROBE" showAddButton={true}>
                {Object.entries(clothesByType).map(([type, items]) => (
                    <Type key={type} text={type}>
                        {items.map((clothesItem) => (
                            <Image key={clothesItem.id} imageSrc={clothesItem.src} />
                        ))}
                    </Type>
                ))}
            </Section>
        </>
    )
};

export default Wardrobe;
