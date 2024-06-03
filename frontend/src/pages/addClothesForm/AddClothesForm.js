import './AddClothesForm.css';
import Navigation from "../../components/navigation/Navigation";
import Section from "../../components/Section/Section";
import { useEffect, useState } from "react";
import axios from "../../axiosConfig";
import CategoryDropdown from "../../components/CategoryDropdown/CategoryDropdown";
import { useNavigate } from "react-router-dom";

const AddClothesForm = () => {

    const [clothes, setClothes] = useState({
        image: '',
        category: [],
        type: {},
    });
    const navigate = useNavigate();
    const [categories, setCategories] = useState([]);
    const [types, setTypes] = useState([]);

    useEffect(() => {
        const fetchCategoriesAndTypes = async () => {
            try {
                const categoriesResponse = await axios.get('/api/clothes/categories');
                const typesResponse = await axios.get('/api/clothes/types');
                setCategories(categoriesResponse.data);
                setTypes(typesResponse.data);
            } catch (error) {
                console.error('Error:', error);
            }
        };
        fetchCategoriesAndTypes();
    }, []);

    const handleChange = (event) => {
        if (event.target.name === 'image') {
            setClothes({
                ...clothes,
                [event.target.name]: event.target.files[0],
            });
        } else if (event.target.name === 'type') {
            const selectedOption = types.find(option => option.id === parseInt(event.target.value, 10)); // Parse value to integer
            setClothes({
                ...clothes,
                [event.target.name]: selectedOption,
            });
        } else {
            setClothes({
                ...clothes,
                [event.target.name]: event.target.value,
            });
        }
    };

    const handleCategoryChange = (event) => {
        const selectedCategory = categories.find(category => category.id === parseInt(event.target.value, 10)); // Parse value to integer
        if (event.target.checked) {
            setClothes(prevState => ({
                ...prevState,
                category: [...prevState.category, selectedCategory]
            }));
        } else {
            setClothes(prevState => ({
                ...prevState,
                category: prevState.category.filter(category => category.id !== selectedCategory.id)
            }));
        }
    };

    const handleSubmit = async (event) => {
        event.preventDefault();

        const formData = new FormData();
        formData.append('image', clothes.image);
        formData.append('type', JSON.stringify(clothes.type));
        formData.append('category', JSON.stringify(clothes.category));

        try {
            await axios.post('/api/clothes', formData, {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem('token')}`,
                    'Content-Type': 'multipart/form-data',
                }
            });
            navigate('/wardrobe');
        } catch (error) {
            console.error('Error:', error);
        }
    };

    return (
        <>
            <Navigation />
            <Section text="ADD CLOTHES">
                <form onSubmit={handleSubmit} className="addClothes">
                        <input type="file" name="image" onChange={handleChange} />
                        <CategoryDropdown
                            categories={categories}
                            selectedCategories={clothes.category}
                            onCategoryChange={handleCategoryChange}
                        />
                        <select name="type" value={clothes.type.id || ''} onChange={handleChange}> {/* Add a fallback value */}
                            <option value="">Select a type</option>
                            {types.map(type => (
                                <option key={type.id} value={type.id}>{type.name}</option>
                            ))}
                        </select>
                    <button type="submit" className="addButton">Add Clothes </button>
                </form>
            </Section>
        </>
    );
};

export default AddClothesForm;
