import React, {useEffect, useRef, useState} from "react";
import "./profile.css";
import Navigation from "../../components/Navigation/Navigation";
import Section from "../../components/Section/Section";
import axios from "../../axiosConfig";
import FollowingDropdown from "../../components/FollowingDropdown/FollowingDropdown";
import Outfit from "../../components/Outfit/Outfit";
import buildImageUrl from '../../utils/buildImageUrl';

const Profile = () => {
    const [feed, setFeed] = useState([]);
    const [newComment, setNewComment] = useState("");
    const [error, setError] = useState("");
    const [profile, setProfile] = useState({photo: "", description: "", username: "", isFollowed: false});
    const [updatedDescription, setUpdatedDescription] = useState("");
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
        return {...data, photo: data.photo || data.user_photo};
    };

    const fetchFeed = async (userId = null) => {
        const token = localStorage.getItem("token");
        const endpoint = userId ? `/api/social/feed?userId=${userId}` : "/api/social/feed";
        const response = await axios.get(endpoint, {
            headers: {Authorization: `Bearer ${token}`},
        });
        const posts = response.data || [];

        // fetch comments for each post in parallel and attach to post.comments
        return await Promise.all(posts.map(async (post) => {
            try {
                const commentsResponse = await axios.get(`/api/social/post/${post.id}/comments`, {
                    headers: { Authorization: `Bearer ${token}` },
                });
                // backend returns list of CommentDTO
                post.comments = commentsResponse.data || [];
            } catch (err) {
                // if comments fetch fails, attach empty array to avoid crashes
                post.comments = [];
            }
            return post;
        }));
    };

    const checkFollowStatus = async (viewingUserId) => {
        try {
            const token = localStorage.getItem("token");
            const response = await axios.get("/api/social/following", {
                headers: {Authorization: `Bearer ${token}`},
            });

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
            // refresh feed to show the new comment
            const feedData = await fetchFeed(viewingUserId);
            setFeed(feedData);
            setError("");
        } catch (err) {
            console.error("Error adding comment:", err);
            setError("Failed to add comment. Please try again.");
        }
    };

    const handleDeletePost = async (postId) => {
        try {
            const token = localStorage.getItem("token");
            await axios.delete(`/api/social/post/${postId}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            // remove from local state
            setFeed((prev) => prev.filter((p) => p.id !== postId));
            setError("");
        } catch (err) {
            console.error("Error deleting post:", err);
            setError("Failed to delete post. Please try again.");
        }
    };

    const handleDeleteComment = async (postId, commentId) => {
        try {
            const token = localStorage.getItem("token");
            await axios.delete(`/api/social/post/${postId}/comment/${commentId}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            // refresh feed to reflect deletion
            const feedData = await fetchFeed(viewingUserId);
            setFeed(feedData);
            setError("");
        } catch (err) {
            console.error("Error deleting comment:", err);
            setError("Failed to delete comment. Please try again.");
        }
    };

    const handleSearch = async (query) => {
        try {
            const token = localStorage.getItem("token");
            const response = await axios.get(`/api/social/profile/search?username=${query}`, {
                headers: {Authorization: `Bearer ${token}`},
            });
            const limitedResults = (response.data || []).slice(0, 3).map(u => ({
                ...u,
                photo: u.photo || u.user_photo || u.user?.photo || u.avatar || u.profilePhoto || null,
            }));
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
            <div className="page profile-page">
                <Section text="SEARCH PROFILES">
                    <div className="search-row">
                        <div className="search-left">
                            <input
                                type="text"
                                placeholder="Search by username"
                                value={searchQuery}
                                onChange={handleInputChange}
                            />
                            <div className="search-results">
                                {searchResults.map((user) => (
                                    <div
                                        key={user.id}
                                        onClick={() => setViewingUserId(user.id)}
                                        className="search-result"
                                    >
                                        <img
                                            src={buildImageUrl(user.photo) || undefined}
                                            alt="User"
                                            onError={(e) => { e.currentTarget.onerror = null; e.currentTarget.src = 'https://via.placeholder.com/50'; }}
                                        />
                                        <p>{user.username}</p>
                                    </div>
                                ))}
                            </div>
                        </div>
                        <div className="search-right">
                            <FollowingDropdown
                                viewingUserId={viewingUserId}
                                onUserSelect={(userId) => setViewingUserId(userId)}
                            />
                        </div>
                    </div>
                </Section>
                <Section text="PROFILE">
                    {error && <p className="error">{error}</p>}
                    <div className="profile-container">
                        <div className="profile-header">
                            <img
                                className="profilePic"
                                src={buildImageUrl(profile.photo) || undefined}
                                alt="Profile"
                                onClick={isEditable ? () => fileInputRef.current.click() : undefined}
                                style={isEditable ? {cursor: "pointer"} : {}}
                                onError={(e) => { e.currentTarget.onerror = null; e.currentTarget.src = 'https://via.placeholder.com/150'; }}
                            />
                            <div className="profile-details">
                                <h2>{profile.username}</h2>
                                {!isEditable && (
                                    <button onClick={toggleFollow} className="follow-btn">
                                        {profile.isFollowed ? "Unfollow" : "Follow"}
                                    </button>
                                )}
                            </div>
                        </div>
                        {isEditable ? (
                            <input
                                type="file"
                                ref={fileInputRef}
                                style={{display: "none"}}
                                onChange={handlePhotoUpload}
                            />
                        ) : null}

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

                            {/* Render outfit if present on the post */}
                            {post.outfitDTO && (
                                <div className="post-outfit">
                                    <Outfit outfit={post.outfitDTO} />
                                </div>
                            )}

                            <p>Stars: {post.starCount}</p>
                            <button onClick={() => handleStar(post.id)}>Star</button>
                            <button onClick={() => handleDeletePost(post.id)}>Delete Post</button>

                            <div className="comments-list">
                                {post.comments && post.comments.map((comment) => (
                                    <div key={comment.id} className="comment">
                                        <p>{comment.text}</p>
                                        <button onClick={() => handleDeleteComment(post.id, comment.id)}>Delete Comment</button>
                                    </div>
                                ))}
                            </div>

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
