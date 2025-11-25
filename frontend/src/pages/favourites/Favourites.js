import './favourites.css';
import Navigation from "../../components/Navigation/Navigation";
import Section from "../../components/Section/Section";
import Outfit from "../../components/Outfit/Outfit";
import {useEffect, useState} from "react";
import axios from "../../axiosConfig";
import ShareOutfitForm from "../../components/ShareOutfitForm/ShareOutfitForm";

const Favourites = () => {
    const [outfits, setOutfits] = useState([]);
    const [toastMsg, setToastMsg] = useState("");
    const [sharingOutfitId, setSharingOutfitId] = useState(null);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await axios.get('/api/outfits');
                const outfitsData = response.data;

                setOutfits(outfitsData || []);
            } catch (error) {
                console.error('Error fetching outfits:', error);
            }
        };

        fetchData();
    }, []);

    const showToast = (msg) => {
        setToastMsg(msg);
        setTimeout(() => setToastMsg(""), 3000);
    };

    // open modal instead of prompt
    const openShareModal = (outfit) => {
        setSharingOutfitId(outfit.id);
    };

    const handleShared = () => {
        showToast('Outfit shared');
        setSharingOutfitId(null);
    };

    const handleShareClose = () => {
        setSharingOutfitId(null);
    };

    // Remove favorite handler
    const handleRemoveFavorite = async (e, outfit) => {
        e.stopPropagation(); // prevent opening share modal
        try {
            await axios.delete(`/api/outfits/${outfit.id}`);
            setOutfits(prev => prev.filter(o => o.id !== outfit.id));
            showToast('Removed from favourites');
        } catch (err) {
            console.error('Failed to remove favourite', err);
            showToast('Failed to remove favourite');
        }
    };

    return (
        <>
            <Navigation/>
            <Section text="FAVOURITES OUTFITS" className="favourites">
                <div className="favouritesGrid">
                    {outfits.map((outfit, index) => (
                        <div key={index} className="outfit-container">
                            <button className="remove-btn" onClick={(e) => handleRemoveFavorite(e, outfit)}>✕</button>
                            <div className="overlay" onClick={() => openShareModal(outfit)}>
                                Share
                            </div>
                            <Outfit outfit={outfit} />
                        </div>
                    ))}
                </div>
                {toastMsg && <div className="toast">{toastMsg}</div>}

                {/* modal */}
                {sharingOutfitId && (
                    <div className="modal-backdrop">
                        <div className="modal">
                            <ShareOutfitForm outfitId={sharingOutfitId} onClose={handleShareClose} onShared={handleShared} />
                        </div>
                    </div>
                )}
            </Section>
        </>
    )
};
export default Favourites;
