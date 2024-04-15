import React from 'react';
import './Section.css';

const Section = ({ text, children }) => {
    return (
        <div className="section">
            <div className="text">{text}</div>
            <div className="underline"></div>
            <div className="content">
            {children}
            </div>
        </div>
    );
};

export default Section;
