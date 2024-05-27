import './AddClothesForm.css';
import Navigation from "../../components/navigation/Navigation";
import Section from "../../components/Section/Section";
import {useState} from "react";

const AddClothesForm = () => {

    const [clothes, setClothes] = useState({
        image: '',
        category: '',
    });

    const handleChange = (event) => {
        setClothes({
            ...clothes,
            [event.target.name]: event.target.value,
        });
    };

    const handleSubmit = (event) => {
        event.preventDefault();
        console.log(clothes);
    };
    return (
        <>
            <Navigation/>
            <Section text="ADD CLOTHES">
                <form onSubmit={handleSubmit}>
                    <label>
                        Image:
                        <input type="file" name="image" value={clothes.image} onChange={handleChange}/>
                    </label>
                    <label>
                        Category:
                        <select name="category" value={clothes.category} onChange={handleChange}>
                            <option value="">Select a category</option>
                            <option value="shirts">Shirts</option>
                            <option value="pants">Pants</option>
                            <option value="shoes">Shoes</option>
                        </select>
                    </label>
                    <button type="submit">Add Clothes</button>
                </form>
            </Section>
        </>
    )
};
export default AddClothesForm;
