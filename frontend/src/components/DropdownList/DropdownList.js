import React, { useState, useRef, useEffect } from 'react';
import './DropdownList.css';

const DropdownList = ({
                          items,
                          selectedItems,
                          onSelectionChange,
                          onAddItem = null, // Pass null to disable adding functionality
                          allowMultiple = false, // Toggle between single and multiple selection modes
                          placeholder = 'Select an option',
                          preselectAll = false, // New prop for preselection functionality
                      }) => {
    const [isOpen, setIsOpen] = useState(false);
    const [newItemName, setNewItemName] = useState('');
    const dropdownRef = useRef(null);

    // Handle preselection on the first render
    useEffect(() => {
        if (preselectAll && selectedItems.length === 0) {
            onSelectionChange(items);
        }
    }, [preselectAll, items, onSelectionChange, selectedItems.length]);

    const handleToggleDropdown = () => {
        setIsOpen((prev) => !prev);
    };

    const handleClickOutside = (event) => {
        if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
            setIsOpen(false);
        }
    };

    const handleKeyPress = async (event) => {
        if (event.key === 'Enter' && newItemName.trim() !== '') {
            event.preventDefault();
            if (onAddItem) {
                const newItem = await onAddItem(newItemName);
                if (newItem) {
                    setNewItemName('');
                }
            }
        }
    };

    const handleItemChange = (event, item) => {
        if (allowMultiple) {
            // Handle multiple selection
            if (event.target.checked) {
                onSelectionChange([...selectedItems, item]);
            } else {
                onSelectionChange(selectedItems.filter((selected) => selected.id !== item.id));
            }
        } else {
            // Handle single selection
            onSelectionChange([item]);
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
                {placeholder}
            </button>
            {isOpen && (
                <div className="dropdown-menu">
                    {items.map((item) => (
                        <div key={item.id || item.name || Math.random()} className="dropdown-item">
                            <label className="item-check">
                                <input
                                    type={allowMultiple ? 'checkbox' : 'radio'}
                                    name="dropdown-selection"
                                    value={item.id}
                                    checked={selectedItems.some((selected) => selected.id === item.id)}
                                    onChange={(e) => handleItemChange(e, item)}
                                />
                                <span className="item-text">{item.name || 'Unnamed Item'}</span>
                            </label>
                        </div>
                    ))}

                    {onAddItem && (
                        <div className="dropdown-item">
                            <input
                                type="text"
                                placeholder="Add new item"
                                value={newItemName}
                                onChange={(e) => setNewItemName(e.target.value)}
                                onKeyPress={handleKeyPress}
                            />
                        </div>
                    )}
                </div>
            )}
        </div>
    );
};

export default DropdownList;
