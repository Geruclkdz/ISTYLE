import React, {useEffect, useRef, useState} from "react";
import "./profile.css";
import Navigation from "../../components/Navigation/Navigation";
import Section from "../../components/Section/Section";
import axios from "../../axiosConfig";
import FollowingDropdown from "../../components/FollowingDropdown/FollowingDropdown";

const Profile = () => {
    const [feed, setFeed] = useState([]);
    const [newComment, setNewComment] = useState("");
    const [error, setError] = useState("");
    const [profile, setProfile] = useState({photo: "", description: "", username: "", isFollowed: false});
    const [updatedDescription, setUpdatedDescription] = useState("");
    const [newPhoto, setNewPhoto] = useState(null);
    const [searchResults, setSearchResults] = useState([]);
    const [searchQuery, setSearchQuery] = useState("");
    const [viewingUserId, setViewingUserId] = useState(null);

    const fileInputRef = useRef(null);

    const fetchProfile = async (userId = null) => {
        const token = localStorage.getItem("token");
        const endpoint = userId ? `/api/social/profile?userId=${userId}` : "/api/social/profile";
        const response = await axios.get(endpoint, {
            headers: {Authorization: `Bearer ${token}`},
        });
        const data = response.data || {};
        // Normalize photo field: backend might send `user_photo`; UI expects `photo`
        return {...data, photo: data.photo || data.user_photo};
    };

    // Utility function to fetch feed
    const fetchFeed = async (userId = null) => {
        const token = localStorage.getItem("token");
        const endpoint = userId ? `/api/social/feed?userId=${userId}` : "/api/social/feed";
        const response = await axios.get(endpoint, {
            headers: {Authorization: `Bearer ${token}`},
        });
        return response.data;
    };

    const checkFollowStatus = async (viewingUserId) => {
        try {
            const token = localStorage.getItem("token");
            const response = await axios.get("/api/social/following", {
                headers: {Authorization: `Bearer ${token}`},
            });

            // Check if viewingUserId is in the list of followed users
            const isFollowed = response.data.some((user) => user.id === viewingUserId);
            setProfile((prev) => ({...prev, isFollowed}));
        } catch (err) {
            console.error("Error checking follow status:", err.response?.data || err.message);
            setError("Failed to check follow status. Please try again.");
        }
    };


    const toggleFollow = async () => {
        try {
            const token = localStorage.getItem("token");
            const endpoint = `/api/social/follow?followeeId=${viewingUserId}`;
            const method = profile.isFollowed ? "delete" : "post";

            await axios({
                method,
                url: endpoint,
                headers: {Authorization: `Bearer ${token}`},
            });

            setProfile((prev) => ({...prev, isFollowed: !prev.isFollowed}));
        } catch (err) {
            console.error("Error toggling follow status:", err.response?.data || err.message);
            setError("Failed to update follow status. Please try again.");
        }
    };

    // Fetch data when viewingUserId changes
    useEffect(() => {
        const loadData = async () => {
            try {
                const profileData = await fetchProfile(viewingUserId);
                const feedData = await fetchFeed(viewingUserId);

                setProfile(profileData);
                setFeed(feedData);

                if (viewingUserId !== null) {
                    await checkFollowStatus(viewingUserId);
                } else {
                    setUpdatedDescription(profileData.description);
                }

            } catch (error) {
                console.error("Error loading data:", error);
                setError("Failed to load data. Please try again.");
            }
        };

        loadData();
    }, [viewingUserId]);


    const handlePhotoUpload = async (event) => {
        if (!isEditable) return;
        const file = event.target.files[0];
        if (!file) return;

        try {
            const token = localStorage.getItem("token");
            const formData = new FormData();
            formData.append("photo", file);

            const response = await axios.put("/api/social/profile/photo", formData, {
                headers: {
                    Authorization: `Bearer ${token}`
                },
            });

            setProfile((prev) => ({
                ...prev,
                photo: response.data.photo || prev.photo,
            }));

            setError("");
        } catch (err) {
            console.error("Error uploading photo:", err.response?.data || err.message);
            setError("Failed to upload photo. Please try again.");
        }
    };

    const handleDescriptionUpdate = async (newDescription) => {
        if (!isEditable) return;

        setUpdatedDescription(newDescription);

        try {
            const token = localStorage.getItem("token");
            const formData = new FormData();
            formData.append("description", newDescription);

            await axios.put(
                "/api/social/profile",
                formData,
                {
                    headers: {Authorization: `Bearer ${token}`}
                }
            );

            // Refresh profile data after a successful update
            const updatedProfile = await fetchProfile();
            setProfile(updatedProfile);
            setError("");
        } catch (err) {
            console.error("Error updating description:", err.response?.data || err.message);
            setError("Failed to update description. Please try again.");
        }
    };

    const handleStar = async (postId) => {
        try {
            const token = localStorage.getItem("token");
            const response = await axios.post(`/api/social/post/${postId}/star`, {}, {
                headers: { Authorization: `Bearer ${token}` },
            });
            const newCount = response.data?.starCount ?? null;
            if (newCount !== null) {
                setFeed((prevFeed) =>
                    prevFeed.map((post) =>
                        post.id === postId ? { ...post, starCount: newCount } : post
                    )
                );
            }
        } catch (err) {
            console.error("Error adding/removing star:", err);
            setError("Failed to update star. Please try again.");
        }
    }

    const handleComment = async (postId) => {
        try {
            const token = localStorage.getItem("token");
            const commentDTO = {postId: postId, text: newComment};
            await axios.post(`/api/social/post/${postId}/comment`, commentDTO, {
                headers: {Authorization: `Bearer ${token}`},
            });
            setNewComment("");
            setError("");
        } catch (err) {
            console.error("Error adding comment:", err);
            setError("Failed to add comment. Please try again.");
        }
    };

    const handleSearch = async (query) => {
        try {
            const token = localStorage.getItem("token");
            const response = await axios.get(`/api/social/profile/search?username=${query}`, {
                headers: {Authorization: `Bearer ${token}`},
            });
            const limitedResults = response.data.slice(0, 3);
            setSearchResults(limitedResults);
        } catch (err) {
            console.error("Error searching profiles:", err);
            setError("Failed to search profiles. Please try again.");
        }
    };

    const handleInputChange = (e) => {
        const query = e.target.value;
        setSearchQuery(query);
        if (query.length > 0) {
            handleSearch(query);
        } else {
            setSearchResults([]);
        }
    };

    const isEditable = viewingUserId === null;

    return (
        <>
            <Navigation/>
            <div className="page">
                <Section text="SEARCH PROFILES">
                    <input
                        type="text"
                        placeholder="Search by username"
                        value={searchQuery}
                        onChange={handleInputChange}
                    />
                    {searchResults.map((user) => (
                        <div
                            key={user.id}
                            onClick={() => setViewingUserId(user.id)}
                            className="search-result"
                        >
                            <img
                                src={(user.photo ? ((user.photo.startsWith('http') ? '' : (process.env.REACT_APP_API_URL || 'http://localhost:8080')) + (user.photo.startsWith('/') ? '' : '/')) + user.photo : 'https://via.placeholder.com/50') + (user.photo ? (`${user.photo.includes('?') ? '&' : '?'}t=${Date.now()}`) : '')}
                                alt="User"/>
                            <p>{user.username}</p>
                        </div>
                    ))}
                    <FollowingDropdown
                        viewingUserId={viewingUserId}
                        onUserSelect={(userId) => setViewingUserId(userId)}
                    />
                </Section>
                <Section text="PROFILE">
                    {error && <p className="error">{error}</p>}
                    <div className="profile-container">
                        <img
                            className="profilePic"
                            src={(profile.photo ? ((profile.photo.startsWith('http') ? '' : (process.env.REACT_APP_API_URL || 'http://localhost:8080')) + (profile.photo.startsWith('/') ? '' : '/')) + profile.photo : 'https://via.placeholder.com/150') + (profile.photo ? (`${profile.photo.includes('?') ? '&' : '?'}t=${Date.now()}`) : '')}
                            alt="Profile"
                            onClick={isEditable ? () => fileInputRef.current.click() : undefined}
                            style={isEditable ? {cursor: "pointer"} : {}}
                        />
                        {isEditable && (
                            <input
                                type="file"
                                ref={fileInputRef}
                                style={{display: "none"}}
                                onChange={handlePhotoUpload}
                            />
                        )}
                        <div className="profile-details">
                            <h2>{profile.username}</h2>
                            {!isEditable && (
                                <button onClick={toggleFollow}>
                                    {profile.isFollowed ? "Unfollow" : "Follow"}
                                </button>
                            )}
                        </div>
                        {isEditable ? (
                            <textarea
                                value={updatedDescription}
                                onChange={(e) => handleDescriptionUpdate(e.target.value)}
                                placeholder="Update your description"
                            />
                        ) : (
                            <p>{profile.description}</p>
                        )}

                    </div>
                </Section>
                <Section text="FEED">
                    {feed.map((post) => (
                        <div key={post.id} className="post">
                            <p>{post.text}</p>
                            <p>Stars: {post.starCount}</p>
                            <button onClick={() => handleStar(post.id)}>Star</button>
                            <div className="comment-section">
                                <input
                                    type="text"
                                    placeholder="Add a comment..."
                                    value={newComment}
                                    onChange={(e) => setNewComment(e.target.value)}
                                />
                                <button onClick={() => handleComment(post.id)}>Comment</button>
                            </div>
                        </div>
                    ))}
                </Section>
            </div>
        </>
    );
};

export default Profile;
