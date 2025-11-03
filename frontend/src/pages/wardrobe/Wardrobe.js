import './Wardrobe.css';
import Navigation from "../../components/Navigation/Navigation";
import Section from "../../components/Section/Section";
import Type from "../../components/Type/Type";
import Image from "../../components/Image/Image";
import DropdownList from "../../components/DropdownList/DropdownList";
import { useEffect, useState } from "react";
import axios from "../../axiosConfig";

const Wardrobe = () => {
    const [clothesByType, setClothesByType] = useState({});
    const [categories, setCategories] = useState([]); // All available categories
    const [dropdownVisible, setDropdownVisible] = useState({}); // Track visibility of dropdown for each clothing item

    useEffect(() => {
        const fetchData = async () => {
            try {
                // Fetch all clothes
                const clothesResponse = await axios.get('/api/clothes');
                const clothesData = clothesResponse.data;

                // Organize clothes by type
                const clothesByType = clothesData.reduce((acc, item) => {
                    const typeName = item.type.name;
                    if (!acc[typeName]) {
                        acc[typeName] = [];
                    }
                    acc[typeName].push(item);
                    return acc;
                }, {});

                setClothesByType(clothesByType);

                // Fetch all categories
                const categoriesResponse = await axios.get('/api/clothes/categories');
                setCategories(categoriesResponse.data);
            } catch (error) {
                console.error('Error fetching data:', error);
            }
        };

        fetchData();
    }, []);


    const updateCategories = async (clothesId, selected) => {
        try {
            const token = localStorage.getItem("token");
            await axios.put(
                `/api/clothes/${clothesId}`,
                selected,
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                }
            );

            // Update local state after successful backend update
            setClothesByType((prev) => {
                const updated = { ...prev };
                for (const type in updated) {
                    updated[type] = updated[type].map((item) => {
                        if (item.id === clothesId) {
                            item.categories = selected; // Update categories locally
                        }
                        return item;
                    });
                }
                return updated;
            });

            console.log("Categories updated successfully");
        } catch (error) {
            console.error("Error updating categories:", error);
        }
    };

    const handleDropdownVisibility = (clothesId, visible) => {
        setDropdownVisible((prev) => ({ ...prev, [clothesId]: visible }));
    };

    const handleCategoryChange = (clothesId, selected) => {
        updateCategories(clothesId, selected); // Persist changes to backend
    };

    return (
        <>
            <Navigation />
            <Section text="YOUR WARDROBE" showAddButton={true}>
                {Object.entries(clothesByType).map(([type, items]) => (
                    <Type key={type} text={type}>
                        {items.map((clothesItem) => (
                            <div
                                key={clothesItem.id}
                                className="clothes-container"
                                onMouseEnter={() => handleDropdownVisibility(clothesItem.id, true)}
                                onMouseLeave={() => handleDropdownVisibility(clothesItem.id, false)}
                            >
                                <Image imageSrc={clothesItem.src} />
                                {dropdownVisible[clothesItem.id] && (
                                    <DropdownList
                                        items={categories} // All available categories
                                        selectedItems={clothesItem.categories} // Pre-select categories the clothing item belongs to
                                        onSelectionChange={(selected) =>
                                            handleCategoryChange(clothesItem.id, selected)
                                        }
                                        allowMultiple={true}
                                        placeholder="Set Categories"
                                    />
                                )}
                            </div>
                        ))}
                    </Type>
                ))}
            </Section>
        </>
    );
};

export default Wardrobe;
