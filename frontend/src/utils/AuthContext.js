import React, { createContext, useState, useEffect, useContext } from 'react';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [isLoggedIn, setIsLoggedIn] = useState(() => {
        // Check if token exists in localStorage
        return !!localStorage.getItem('token');
    });

    const login = () => {
        setIsLoggedIn(true);
    };

    const logout = () => {
        localStorage.removeItem('token');
        setIsLoggedIn(false);
    };

    return (
        <AuthContext.Provider value={{ isLoggedIn, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    return useContext(AuthContext);
};
