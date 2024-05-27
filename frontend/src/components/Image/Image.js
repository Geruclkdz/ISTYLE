import React from 'react';
import './Image.css';

const Image = ({ imageSrc }) => {
    return (
        <div className="imageContainer">
                <img className='Clothes' src={imageSrc} alt='Image'/>
    </div>
)
    ;
};

export default Image;
