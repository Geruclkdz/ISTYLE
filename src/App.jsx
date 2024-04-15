import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Wardrobe from './pages/wardrobe/wardrobe';
import Register from "./pages/register/register";
import Login from "./pages/login/login";
import OutfitCreator from "./pages/outfitCreator/outfitCreator";
import Profile from "./pages/profile/profile";
import Favourites from "./pages/favourites/favourites";

const App = () => {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<Wardrobe />} />
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                <Route path="/outfitCreator" element={<OutfitCreator />} />
                <Route path="/profile" element={<Profile />} />
                <Route path="/favourites" element={<Favourites />} />
            </Routes>
        </Router>
    );
};

export default App;
