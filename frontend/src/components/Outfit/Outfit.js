import React from "react";
import Image from "../Image/Image";
import './Outfit.css';

const Outfit = ({ outfit }) => {
    const getClothesByType = (type) => {
        return outfit.clothes.find(cloth => cloth.type.name === type);
    };

    const tops = getClothesByType('Tops');
    const bottoms = getClothesByType('Bottoms');
    const shoes = getClothesByType('Shoes');

    return (
        <div className="outfitContainer">
            {tops && <Image imageSrc={tops.src} />}
            {bottoms && <Image imageSrc={bottoms.src} />}
            {shoes && <Image imageSrc={shoes.src} />}
        </div>
    );
};

export default Outfit;
