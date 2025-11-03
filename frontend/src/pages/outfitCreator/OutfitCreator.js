import './outfitCreator.css';
import Navigation from "../../components/Navigation/Navigation";
import Section from "../../components/Section/Section";
import DropdownList from "../../components/DropdownList/DropdownList";
import axios from "../../axiosConfig";
import { useEffect, useState } from "react";
import Image from "../../components/Image/Image";

const OutfitCreator = () => {
    const [outfit, setOutfit] = useState([]);
    const [missingTypes, setMissingTypes] = useState([]); // New state for missing types
    const [location, setLocation] = useState(null);
    const [categories, setCategories] = useState([]);
    const [availableCategories, setAvailableCategories] = useState([]);
    const [useWeatherConditions, setUseWeatherConditions] = useState(true);
    const [error, setError] = useState(""); // To store error messages

    // Fetch user location
    const getLocation = () => {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(
                (position) => {
                    const { latitude, longitude } = position.coords;
                    setLocation({ latitude, longitude });
                },
                (error) => {
                    console.error("Error fetching location:", error);
                    setLocation({ latitude: 48.8567, longitude: 2.3508 }); // Default to Paris
                }
            );
        } else {
            console.error("Geolocation is not supported by this browser.");
        }
    };

    // Fetch all categories
    const fetchCategories = async () => {
        try {
            const response = await axios.get('/api/clothes/categories');
            setAvailableCategories(response.data || []);
            // Default: no category selected (treat as no filters). Items without categories are allowed by backend in this case.
            setCategories([]);
        } catch (error) {
            console.error('Error fetching categories:', error);
        }
    };

    // Fetch outfit data
    const fetchData = async () => {
        if (!location) return;

        try {
            const { latitude, longitude } = location;
            const hasFilters = categories && categories.length > 0;
            const baseUrl = `/api/outfits/create?lat=${latitude}&lon=${longitude}&useWeather=${useWeatherConditions}`;
            const url = hasFilters ? `${baseUrl}&categories=${categories.join(',')}` : baseUrl;
            const response = await axios.get(url);

            const { outfit: outfitData, missingTypes: missing } = response.data || {};

            setOutfit(outfitData || []);
            setMissingTypes(missing || []);

            if ((outfitData || []).length === 0) {
                setError("Not enough clothes to create an outfit.");
            } else {
                setError("");
            }
        } catch (error) {
            console.error('Error fetching outfit:', error);
            setError("Failed to fetch outfit. Please try again.");
        }
    };

    // Save the generated outfit
    const saveOutfit = async () => {
        try {
            const outfitDTO = {
                clothes: outfit.map((item) => ({
                    id: item.id,
                    src: item.src,
                    type: item.type,
                    categories: item.categories,
                    userId: item.userId,
                })),
            };

            const response = await axios.post('/api/outfits/save', outfitDTO);
            console.log('Outfit saved:', response.data);
        } catch (error) {
            console.error('Error saving outfit:', error);
        }
    };

    // Toggle weather-based generation
    const toggleWeather = () => setUseWeatherConditions((prev) => !prev);

    useEffect(() => {
        getLocation();
        fetchCategories();
    }, []);

    useEffect(() => {
        if (location) fetchData();
    }, [location, categories, useWeatherConditions]);

    return (
        <>
            <Navigation />
            <Section text="CREATE YOUR OUTFIT" className="outfitCreator">
                <div className="controls">
                    <DropdownList
                        items={availableCategories.map((cat) => ({
                            id: cat?.id || 0,
                            name: cat?.name || "Unknown",
                        }))}
                        selectedItems={categories.map((id) => ({
                            id,
                            name: availableCategories.find((cat) => cat.id === id)?.name || "Unknown",
                        }))}
                        onSelectionChange={(selected) => setCategories(selected.map((item) => item.id))}
                        allowMultiple={true}
                        placeholder="Select Categories"
                    />
                    <label className="weather-toggle">
                        <input
                            type="checkbox"
                            checked={useWeatherConditions}
                            onChange={toggleWeather}
                        />
                        Use Weather Conditions
                    </label>
                </div>
                <div className="outfit-container">
                    {outfit.length > 0 ? (
                        outfit.map((item) => (
                            <div key={item.id} className="outfit-item">
                                <Image imageSrc={item.src} alt={item.name} />
                            </div>
                        ))
                    ) : (
                        <div className="placeholder">
                            <img
                                src="https://via.placeholder.com/150"
                                alt="Placeholder"
                                className="placeholder-image"
                            />
                            <p>{error || "Loading outfit..."}</p>
                        </div>
                    )}
                </div>
                {missingTypes.length > 0 && (
                    <div className="missing-clothes">
                        <h3>Missing Clothing Types:</h3>
                        <ul>
                            {missingTypes.map((type, index) => (
                                <li key={index}>{type}</li>
                            ))}
                        </ul>
                    </div>
                )}
                <div className="actions">
                    <span className="material-symbols-sharp" onClick={saveOutfit}>save</span>
                    <span className="material-symbols-sharp" onClick={fetchData}>refresh</span>
                </div>
            </Section>
        </>
    );
};

export default OutfitCreator;
