import React from 'react';
import './Category.css';

const Category = ({ text, children }) => {
    return (
        <div className="category">
            <div className="content">{text}</div>
            <div className="images">{children}</div>
        </div>

    );
};

export default Category;
