import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Wardrobe from './pages/wardrobe/Wardrobe';
import Register from "./pages/register/Register";
import Login from "./pages/login/Login";
import OutfitCreator from "./pages/outfitCreator/OutfitCreator";
import Profile from "./pages/profile/Profile";
import Favourites from "./pages/favourites/Favourites";
import AddClothesForm from "./pages/addClothesForm/AddClothesForm";

const App = () => {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<Login />} />
                <Route path="/wardrobe" element={<Wardrobe />} />
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                <Route path="/outfitCreator" element={<OutfitCreator />} />
                <Route path="/profile" element={<Profile />} />
                <Route path="/favourites" element={<Favourites />} />
                <Route path="/addClothesForm" element={<AddClothesForm />} />
            </Routes>
        </Router>
    );
};

export default App;
