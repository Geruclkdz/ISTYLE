import React from 'react';
import './Image.css';
import buildImageUrl from '../../utils/buildImageUrl';

const Image = ({ imageSrc, alt = 'Clothes', className = 'Clothes', placeholder = 'https://via.placeholder.com/150' }) => {
    if (!imageSrc) return null;

    // use shared helper to build the full URL (handles API base and cache-busting)
    const src = buildImageUrl(imageSrc);

    return (
        <div className="imageContainer">
            <img
                className={className}
                src={src || undefined}
                alt={alt}
                loading="lazy"
                onError={(e) => { e.currentTarget.onerror = null; e.currentTarget.src = placeholder; }}
            />
        </div>
    );
};

export default Image;
