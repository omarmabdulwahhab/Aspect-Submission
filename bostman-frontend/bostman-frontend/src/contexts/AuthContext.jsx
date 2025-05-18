import React, { createContext, useState, useContext, useEffect } from 'react';
import { jwtDecode } from 'jwt-decode'; // Import jwt-decode
import { login as apiLogin, register as apiRegister } from '../api/authService';

const AuthContext = createContext(null);

// Helper function to process token and set user state
const processToken = (tokenString, setTokenState, setUserState) => {
  localStorage.setItem('authToken', tokenString);
  setTokenState(tokenString);
  try {
    const decodedToken = jwtDecode(tokenString);
    // Ensure your JWT has 'sub' for username/email and 'roles' as an array of strings
    const roles = decodedToken.roles || []; // Default to empty array if roles claim is missing
    // Ensure roles are consistently an array, even if backend sends a single role string (less common for JWTs)
    const userRoles = Array.isArray(roles) ? roles : [roles]; 
    setUserState({ 
      email: decodedToken.sub, // 'sub' is standard for subject (username/email)
      roles: userRoles       // 'roles' is the custom claim we added
    });
    return { email: decodedToken.sub, roles: userRoles };
  } catch (error) {
    console.error("Failed to decode token:", error);
    // If token is invalid, clear auth state
    localStorage.removeItem('authToken');
    setTokenState(null);
    setUserState(null);
    return null;
  }
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem('authToken'));
  const [loading, setLoading] = useState(true); // Start true until initial token check
  const [error, setError] = useState(null);

  // Initialize user from stored token on component mount
  useEffect(() => {
    const storedToken = localStorage.getItem('authToken');
    if (storedToken) {
      processToken(storedToken, setToken, setUser);
    }
    setLoading(false); // Initial loading finished
  }, []);

  const login = async (credentials) => {
    setLoading(true);
    setError(null);
    try {
      const tokenString = await apiLogin(credentials); // This now returns the JWT string
      processToken(tokenString, setToken, setUser);
      setLoading(false);
      return true;
    } catch (err) {
      setError(err.message || 'Login failed. Please try again.');
      setLoading(false);
      setUser(null);
      setToken(null);
      localStorage.removeItem('authToken');
      return false;
    }
  };

  const register = async (userData) => {
    setLoading(true);
    setError(null);
    try {
      await apiRegister(userData); // Returns a success message string
      setLoading(false);
      return true;
    } catch (err) {
      setError(err.message || 'Registration failed. Please try again.');
      setLoading(false);
      return false;
    }
  };

  const logout = () => {
    setUser(null);
    setToken(null);
    localStorage.removeItem('authToken');
    // Optionally, redirect to login page or clear other app state
  };

  return (
    <AuthContext.Provider value={{ user, token, login, register, logout, loading, error, isAuthenticated: !!token }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}; 