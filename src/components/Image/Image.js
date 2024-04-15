import React from 'react';
import './Image.css';

const Image = ({ children }) => {
    return (
        <div className="imageContainer">
            {React.Children.map(children, (child, index) => (
                <img key={index} className='Clothes' src={child} alt='Image'/>
            )
)}
</div>
)
    ;
};

export default Image;
