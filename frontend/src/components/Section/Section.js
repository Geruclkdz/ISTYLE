import React from 'react';
import './Section.css';
import {Link} from "react-router-dom";

const Section = ({ text, children, showAddButton }) => {
    return (
        <div className="section">
            <div className="section-top">
                <div className="text">{text}</div>
                {showAddButton && <Link to="/addClothesForm">
                    <button className="add-button">+</button>
                </Link>}
            </div>
            <div className="underline"></div>
            <div className="content">
                {children}
            </div>
        </div>
    );
};

export default Section;