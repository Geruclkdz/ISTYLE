import './outfitCreator.css';
import Navigation from "../../components/Navigation/Navigation";
import Section from "../../components/Section/Section";
import DropdownList from "../../components/DropdownList/DropdownList";
import axios from "../../axiosConfig";
import { useEffect, useState } from "react";
import Image from "../../components/Image/Image";

const OutfitCreator = () => {
    const [outfit, setOutfit] = useState([]);
    const [comments, setComments] = useState([]);
    const [weather, setWeather] = useState(null);
    const [midLayerExists, setMidLayerExists] = useState(false);
    const [outerwearExists, setOuterwearExists] = useState(false);
    const [location, setLocation] = useState(null);
    const [categories, setCategories] = useState([]);
    const [availableCategories, setAvailableCategories] = useState([]);
    const [useWeatherConditions, setUseWeatherConditions] = useState(true);
    const [error, setError] = useState("");
    const [toastMsg, setToastMsg] = useState("");

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

            const { outfit: outfitData, comments: respComments, weather: respWeather } = response.data || {};

            const items = outfitData || [];
            setOutfit(items);
            setComments(respComments || []);
            setWeather(respWeather || null);

            // determine existing layers
            const hasMid = items.some((it) => {
                const t = it?.type?.name || it?.type || "";
                return String(t).toLowerCase().includes("mid");
            });
            const hasOuter = items.some((it) => {
                const t = it?.type?.name || it?.type || "";
                return String(t).toLowerCase().includes("outer");
            });
            setMidLayerExists(hasMid);
            setOuterwearExists(hasOuter);

            if (items.length === 0) {
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

    // Add Layer handler
    const handleAddLayer = async (layerType) => {
        try {
            const { latitude, longitude } = location || { latitude: null, longitude: null };
            const baseUrl = `/api/outfits/addLayer?type=${encodeURIComponent(layerType)}&useWeather=${useWeatherConditions}`;
            const urlWithLoc = (latitude != null && longitude != null) ? `${baseUrl}&lat=${latitude}&lon=${longitude}` : baseUrl;
            // pass selectedIds so backend can color-match against current outfit
            const selectedIds = outfit.map((it) => it.id).filter(Boolean);
            const url = selectedIds.length > 0 ? `${urlWithLoc}&selectedIds=${selectedIds.join(',')}` : urlWithLoc;

            const response = await axios.get(url);
            const { layer, comments: newComments } = response.data || {};
            if (layer) {
                setOutfit((prev) => [...prev, layer]);
                // update layer presence flags
                const t = layer?.type?.name || layer?.type || "";
                if (String(t).toLowerCase().includes("mid")) setMidLayerExists(true);
                if (String(t).toLowerCase().includes("outer")) setOuterwearExists(true);
            }
            if (newComments && newComments.length > 0) {
                setComments((prev) => [...prev, ...newComments]);
            }
            // immediate UI update done
        } catch (err) {
            console.error('Failed to add layer', err);
            showToast('Failed to add layer');
        }
    }

    // Helper: show toast
    const showToast = (msg) => {
        setToastMsg(msg);
        setTimeout(() => setToastMsg(""), 4000);
    };

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
                <div className="outfit-top-row">
                    {weather && (
                        <div className="weather-badge">
                            {weather.temperature}°C
                            <span className="weather-icons">{weather.isRaining ? ' 🌧️' : ''}{weather.isWindy ? ' 💨' : ''}</span>
                        </div>
                    )}
                    {comments && comments.length > 0 && (
                        <div className="info-tooltip">
                            <span className="info-icon">ⓘ Notes</span>
                            <div className="tooltip-text">
                                <ul>
                                    {comments.map((c, idx) => <li key={idx}>{c}</li>)}
                                </ul>
                            </div>
                        </div>
                    )}
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
                {/* Buttons for manual layering */}
                <div className="manual-layer-actions">
                    <button onClick={() => handleAddLayer('MID_LAYER')} disabled={midLayerExists}>Add Mid-Layer</button>
                    <button onClick={() => handleAddLayer('OUTERWEAR')} disabled={outerwearExists}>Add Outerwear</button>
                </div>

                {/* Toast message */}
                {toastMsg && (
                    <div className="toast">{toastMsg}</div>
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



