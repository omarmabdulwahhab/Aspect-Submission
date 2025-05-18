import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

const ProtectedRoute = ({ allowedRoles }) => {
  const { isAuthenticated, user } = useAuth(); // Assuming user object has a 'role' property

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  // If allowedRoles is provided, check if the user's role is included
  // This is a basic role check. In a real app, your user object from AuthContext
  // would need to reliably provide the user's role(s).
  if (allowedRoles && user && user.role && !allowedRoles.includes(user.role)) {
    // User is authenticated but does not have the required role
    // Redirect to an unauthorized page or home page
    // For now, redirecting to home, but an "Unauthorized" page would be better.
    console.warn(`User with role ${user.role} tried to access a route for ${allowedRoles.join(', ')}`);
    return <Navigate to="/" replace />;
  }

  return <Outlet />; // Render the child route
};

export default ProtectedRoute; 