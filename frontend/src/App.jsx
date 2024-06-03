import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Wardrobe from './pages/wardrobe/Wardrobe';
import Register from "./pages/register/Register";
import Login from "./pages/login/Login";
import OutfitCreator from "./pages/outfitCreator/OutfitCreator";
import Profile from "./pages/profile/Profile";
import Favourites from "./pages/favourites/Favourites";
import AddClothesForm from "./pages/addClothesForm/AddClothesForm";
import { AuthProvider, useAuth } from './utils/AuthContext';

const ProtectedRoute = ({ children }) => {
    const { isLoggedIn } = useAuth();
    return isLoggedIn ? children : <Navigate to="/login" />;
};

const App = () => {
    return (
        <AuthProvider>
            <Router>
                <Routes>
                    <Route path="/" element={<Login />} />
                    <Route path="/login" element={<Login />} />
                    <Route path="/register" element={<Register />} />
                    <Route path="/wardrobe" element={<ProtectedRoute><Wardrobe /></ProtectedRoute>} />
                    <Route path="/outfitCreator" element={<ProtectedRoute><OutfitCreator /></ProtectedRoute>} />
                    <Route path="/profile" element={<ProtectedRoute><Profile /></ProtectedRoute>} />
                    <Route path="/favourites" element={<ProtectedRoute><Favourites /></ProtectedRoute>} />
                    <Route path="/addClothesForm" element={<ProtectedRoute><AddClothesForm /></ProtectedRoute>} />
                </Routes>
            </Router>
        </AuthProvider>
    );
};

export default App;
