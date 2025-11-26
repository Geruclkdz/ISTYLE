import React, { useState, useEffect } from "react";
import axios from "../../axiosConfig";
import "./FollowingDropdown.css";

const FollowingDropdown = ({ viewingUserId, onUserSelect }) => {
    const [followingList, setFollowingList] = useState([]);
    const [dropdownOpen, setDropdownOpen] = useState(false);

    useEffect(() => {
        const fetchFollowingList = async () => {
            try {
                const token = localStorage.getItem("token");
                const response = await axios.get("/api/social/following", {
                    headers: { Authorization: `Bearer ${token}` }
                });
                setFollowingList(response.data);
            } catch (err) {
                console.error("Error fetching following list:", err.response?.data || err.message);
            }
        };

        fetchFollowingList();
    }, [viewingUserId]);

    return (
        <div className="following-dropdown">
            <button
                className="following-toggle"
                onClick={() => setDropdownOpen((prev) => !prev)}
            >
                Following
            </button>
            {dropdownOpen && (
                <ul className="following-list">
                    {followingList.map((user) => (
                        <li key={user.id} onClick={() => onUserSelect(user.id)}>
                            <img
                                src={user.photo || "https://via.placeholder.com/50"}
                                alt={user.username}
                            />
                            <span>{user.username}</span>
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
};

export default FollowingDropdown;
