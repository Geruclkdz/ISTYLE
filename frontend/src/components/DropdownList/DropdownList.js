import React, { useState, useRef, useEffect } from 'react';
import './DropdownList.css';
import axios from '../../axiosConfig';

const DropdownList = ({
                          items,
                          selectedItems,
                          onSelectionChange,
                          onAddItem = null, // Pass null to disable adding functionality
                          addUrl = null, // optional endpoint to POST new items (e.g. '/api/clothes/categories')
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
            try {
                let newItem = null;
                // If addUrl is provided, this component will perform the POST
                if (addUrl) {
                    const response = await axios.post(addUrl, { name: newItemName });
                    newItem = response.data;
                } else if (onAddItem) {
                    // Fallback: call parent-provided handler (parent may perform POST and return new item)
                    newItem = await onAddItem(newItemName);
                }

                if (newItem) {
                    // If parent provided an updater callback and we performed the POST here, call it so parent can add to its local list
                    if (addUrl && typeof onAddItem === 'function') {
                        try {
                            onAddItem(newItem);
                        } catch (err) {
                            console.error('onAddItem threw an error while updating parent list:', err);
                        }
                    }
                    setNewItemName('');
                }
            } catch (error) {
                console.error('Error adding item:', error);
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
                    {items.map((item, index) => (
                        <div key={item.id || item.name || index} className="dropdown-item">
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

                    {(addUrl || onAddItem) && (
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
