import React from "react";
import Image from "../Image/Image";
import './Outfit.css';

const Outfit = ({ outfit }) => {
    // outfit may be { clothes: [...] } or an array
    const items = Array.isArray(outfit) ? outfit : (outfit?.clothes || []);

    const findByType = (names) => {
        const lowerNames = names.map(n => n.toLowerCase());
        return items.find(it => {
            const t = (it?.type?.name || it?.type || '').toString().toLowerCase();
            return lowerNames.some(n => t.includes(n));
        });
    };

    const top = findByType(['top', 'tops']);
    const bottom = findByType(['bottom', 'bottoms']);
    const shoes = findByType(['shoe', 'shoes']);
    const mid = findByType(['mid', 'mid-layer', 'mid_layer', 'midlayer', 'mid layer']);
    const outer = findByType(['outer', 'outerwear', 'jacket', 'coat']);

    return (
        <div className="outfitGrid">
            <div className="col col-left">
                {top && (
                    <div className="col-item top-item">
                        <Image imageSrc={top.src} alt={top.name} />
                    </div>
                )}
                {bottom && (
                    <div className="col-item bottom-item">
                        <Image imageSrc={bottom.src} alt={bottom.name} />
                    </div>
                )}
                {shoes && (
                    <div className="col-item shoes-item">
                        <Image imageSrc={shoes.src} alt={shoes.name} />
                    </div>
                )}
            </div>

            <div className="col col-right">
                {mid && (
                    <div className="col-item mid-item">
                        <Image imageSrc={mid.src} alt={mid.name} />
                    </div>
                )}
                {outer && (
                    <div className="col-item outer-item">
                        <Image imageSrc={outer.src} alt={outer.name} />
                    </div>
                )}
            </div>
        </div>
    );
};

export default Outfit;
