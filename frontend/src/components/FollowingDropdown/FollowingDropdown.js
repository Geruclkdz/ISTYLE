import React, { useState, useEffect } from "react";
import axios from "../../axiosConfig";
import "./FollowingDropdown.css";
import buildImageUrl from '../../utils/buildImageUrl';

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

                const normalized = (response.data || []).map(u => ({
                    ...u,
                    photo: u.photo || u.user_photo || u.user?.photo || u.avatar || u.profilePhoto || null,
                }));

                setFollowingList(normalized);
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
                                src={buildImageUrl(user.photo) || undefined}
                                alt={user.username}
                                onError={(e) => { e.currentTarget.onerror = null; e.currentTarget.src = 'https://via.placeholder.com/50'; }}
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
