import React from 'react';
import './Type.css';

const Type = ({ text, children }) => {
    return (
        <div className="type">
            <div className="content">{text}</div>
            <div className="images">{children}</div>
        </div>

    );
};

export default Type;
