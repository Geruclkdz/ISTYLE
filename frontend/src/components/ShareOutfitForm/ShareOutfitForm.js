import React, { useState } from "react";
import axios from "../../axiosConfig";

const ShareOutfitForm = ({ outfitId, onClose, onShared }) => {
    const [description, setDescription] = useState("");
    const [loading, setLoading] = useState(false);

    const handleShare = async (e) => {
        e.preventDefault();
        try {
            setLoading(true);
            const postDTO = {
                outfitId: outfitId, // Include outfit ID
                text: description,          // Add the description
            };

            await axios.post("/api/social/post", postDTO);

            // notify parent
            if (typeof onShared === 'function') onShared();
            // close the form
            if (typeof onClose === 'function') onClose();
        } catch (error) {
            console.error("Error sharing outfit:", error);
            // propagate error by throwing or call onClose with error if needed
            alert("Failed to share outfit.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="share-outfit-form">
            <h3>Share Outfit</h3>
            <form onSubmit={handleShare}>
                <textarea
                    placeholder="Write a description..."
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                />
                <div className="share-actions">
                    <button type="submit" disabled={loading}>{loading ? 'Sharing...' : 'Share'}</button>
                    <button type="button" onClick={onClose} disabled={loading}>
                        Cancel
                    </button>
                </div>
            </form>
        </div>
    );
};

export default ShareOutfitForm;
