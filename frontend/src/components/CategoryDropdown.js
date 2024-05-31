import React, { useState, useRef, useEffect } from 'react';
import './CategoryDropdown.css'; // Ensure to create and import appropriate CSS for styling

const CategoryDropdown = ({ categories, selectedCategories, onCategoryChange }) => {
    const [isOpen, setIsOpen] = useState(false);
    const dropdownRef = useRef(null);

    const handleToggleDropdown = () => {
        setIsOpen(!isOpen);
    };

    const handleClickOutside = (event) => {
        if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
            setIsOpen(false);
        }
    };

    useEffect(() => {
        document.addEventListener('mousedown', handleClickOutside);
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, []);

    return (
        <div className="dropdown" ref={dropdownRef}>
            <button type="button" className="dropdown-toggle" onClick={handleToggleDropdown}>
                Select Categories
            </button>
            {isOpen && (
                <div className="dropdown-menu">
                    {categories.map(category => (
                        <div key={category.id} className="dropdown-item">
                            <label>
                                <input
                                    type="checkbox"
                                    value={category.id}
                                    checked={selectedCategories.some(selectedCategory => selectedCategory.id === category.id)}
                                    onChange={onCategoryChange}
                                />
                                {category.name}
                            </label>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default CategoryDropdown;
