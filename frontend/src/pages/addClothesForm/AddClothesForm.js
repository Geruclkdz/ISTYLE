import './AddClothesForm.css';
import Navigation from "../../components/Navigation/Navigation";
import Section from "../../components/Section/Section";
import { useEffect, useState } from "react";
import axios from "../../axiosConfig";
import DropdownList from "../../components/DropdownList/DropdownList";
import { useNavigate } from "react-router-dom";

const AddClothesForm = () => {
    const [clothes, setClothes] = useState({
        image: '',
        category: [],
        type: {},
        isRainResistant: false,
        isWindResistant: false
    });

    const navigate = useNavigate();
    const [categories, setCategories] = useState([]);
    const [types, setTypes] = useState([]);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const [categoriesResponse, typesResponse] = await Promise.all([
                    axios.get('/api/clothes/categories'),
                    axios.get('/api/clothes/types'),
                ]);
                setCategories(categoriesResponse.data);
                setTypes(typesResponse.data);
            } catch (error) {
                console.error('Error fetching categories and types:', error);
            }
        };
        fetchData();
    }, []);

    const handleChange = (event) => {
        const { name, type, value, checked, files } = event.target;

        if (type === 'file') {
            setClothes(prevState => ({
                ...prevState,
                image: files[0]
            }));
        } else if (type === 'checkbox') {
            setClothes(prevState => ({
                ...prevState,
                [name]: checked
            }));
        } else if (name === 'type') {
            const selectedType = types.find(option => option.id === parseInt(value, 10));
            setClothes(prevState => ({
                ...prevState,
                type: selectedType || {}
            }));
        } else {
            setClothes(prevState => ({
                ...prevState,
                [name]: value
            }));
        }
    };

    const handleCategoryChange = (selectedCategories) => {
        setClothes(prevState => ({
            ...prevState,
            category: selectedCategories,
        }));
    };

    const handleTypeChange = (selectedTypes) => {
        setClothes(prevState => ({
            ...prevState,
            type: selectedTypes[0] || {}, // Single selection for type
        }));
    };

    const handleAddCategory = async (categoryName) => {
        try {
            const response = await axios.post('/api/clothes/categories', { name: categoryName });
            const newCategory = response.data;

            if (!newCategory.id || !newCategory.name) {
                console.error('New category missing required properties:', newCategory);
                return;
            }

            setCategories((prevCategories) => [...prevCategories, newCategory]);

            return newCategory;
        } catch (error) {
            console.error('Error adding category:', error);
        }
    };

    const handleSubmit = async (event) => {
        event.preventDefault();

        const formData = new FormData();
        formData.append('image', clothes.image);
        formData.append('type', JSON.stringify(clothes.type));
        formData.append('category', JSON.stringify(clothes.category));
        formData.append('isRainResistant', clothes.isRainResistant);
        formData.append('isWindResistant', clothes.isWindResistant);

        try {
            await axios.post('/api/clothes', formData, {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem('token')}`,
                    'Content-Type': 'multipart/form-data',
                }
            });
            navigate('/wardrobe');
        } catch (error) {
            console.error('Error submitting form:', error);
        }
    };

    // Determine if checkboxes should be disabled
    const disableResistantCheckboxes = ['Tops', 'Bottoms'].includes(clothes.type.name);

    return (
        <>
            <Navigation />
            <Section text="ADD CLOTHES">
                <form onSubmit={handleSubmit} className="addClothes">
                    <label>
                        Upload Image:
                        <input type="file" name="image" onChange={handleChange} />
                    </label>
                    <DropdownList
                        items={categories}
                        selectedItems={clothes.category}
                        onSelectionChange={handleCategoryChange}
                        onAddItem={handleAddCategory}
                        allowMultiple={true}
                        placeholder="Select Categories"
                    />
                    <DropdownList
                        items={types}
                        selectedItems={[clothes.type]}
                        onSelectionChange={handleTypeChange}
                        onAddItem={null}
                        allowMultiple={false}
                        placeholder="Select Type"
                    />
                    <div className="checkboxes">
                        <label>
                            <input
                                type="checkbox"
                                name="isRainResistant"
                                checked={clothes.isRainResistant}
                                onChange={handleChange}
                                disabled={disableResistantCheckboxes}
                            />
                            Rain Resistant
                        </label>
                        <label>
                            <input
                                type="checkbox"
                                name="isWindResistant"
                                checked={clothes.isWindResistant}
                                onChange={handleChange}
                                disabled={disableResistantCheckboxes}
                            />
                            Wind Resistant
                        </label>
                    </div>
                    <button type="submit" className="addButton">Add Clothes</button>
                </form>
            </Section>
        </>
    );
};

export default AddClothesForm;
