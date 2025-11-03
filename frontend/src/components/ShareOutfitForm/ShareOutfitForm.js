import React, { useState } from "react";
import axios from "../../axiosConfig";

const ShareOutfitForm = ({ outfitId, onClose }) => {
    const [description, setDescription] = useState("");

    const handleShare = async (e) => {
        e.preventDefault();
        try {
            const postDTO = {
                outfitId: outfitId, // Include outfit ID
                text: description,          // Add the description
            };

            await axios.post("/api/social/post", postDTO, {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem("token")}`, // Add actual token
                },
            });

            alert("Outfit shared successfully!");
            onClose();
        } catch (error) {
            console.error("Error sharing outfit:", error);
            alert("Failed to share outfit.");
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
                    required
                />
                <button type="submit">Share</button>
                <button type="button" onClick={onClose}>
                    Cancel
                </button>
            </form>
        </div>
    );
};

export default ShareOutfitForm;
