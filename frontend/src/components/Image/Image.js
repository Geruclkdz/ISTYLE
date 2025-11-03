import React from 'react';
import './Image.css';

// Prefer environment variable if you have one, otherwise fall back to local dev URL.
const API_BASE = process.env.REACT_APP_API_URL || 'http://localhost:8080';

const Image = ({ imageSrc, alt = 'Clothes' }) => {
    if (!imageSrc) return null;

    // `imageSrc` is typically like "/images/15_82.jpg" (relative path from backend)
    const baseSrc = imageSrc.startsWith('http')
        ? imageSrc
        : `${API_BASE}${imageSrc.startsWith('/') ? '' : '/'}${imageSrc}`;

    // Add a cache buster to ensure the browser doesn't cache a previous 403/404 during dev
    const src = `${baseSrc}${baseSrc.includes('?') ? '&' : '?'}t=${Date.now()}`;

    return (
        <div className="imageContainer">
            <img
                className="Clothes"
                src={src}
                alt={alt}
                onError={() => console.error('Error fetching image:', baseSrc)}
            />
        </div>
    );
};

export default Image;
