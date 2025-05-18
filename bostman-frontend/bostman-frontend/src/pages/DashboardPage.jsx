import React from 'react';
import { useAuth } from '../contexts/AuthContext';
import { Link } from 'react-router-dom';

function DashboardPage() {
  const { user, logout } = useAuth();

  return (
    <div>
      <h1>Welcome to Bostman</h1>
      {user && (
        <div>
          <p>Email: {user.email}</p>
          <p>Role(s): {user.roles && user.roles.length > 0 ? user.roles.join(', ') : 'Not specified'}</p>
          <button onClick={logout}>Logout</button>
        </div>
      )}

      <nav>
        <ul>
          {/* Basic User Links */}
          <li><Link to="/my-deliveries">My Deliveries</Link></li>
          <li><Link to="/schedule-delivery">Schedule New Delivery</Link></li>
          <li><Link to="/track-delivery">Track a Delivery</Link></li>

          {/* Conditional Links based on Role - This is a simplified example */}
          {/* In a real app, the user object in AuthContext MUST have a reliable role property */}
          {user && user.roles && user.roles.includes('ADMIN') && (
            <>
              <hr />
              <h3>Admin Actions</h3>
              <li><Link to="/admin/all-deliveries">View All Deliveries (Admin)</Link></li>
              <li><Link to="/admin/assign-driver">Assign Driver (Admin)</Link></li>
              {/* Add more admin links here */}
            </>
          )}

          {user && user.roles && user.roles.includes('DRIVER') && (
            <>
              <hr />
              <h3>Driver Actions</h3>
              <li><Link to="/driver/assigned-deliveries">My Assigned Deliveries (Driver)</Link></li>
              {/* Add more driver links here */}
            </>
          )}
        </ul>
      </nav>

      {/* Placeholder for where sub-page content will render if using nested routes */}
      {/* <Outlet /> */}

      <p>More content for the dashboard will go here based on user role and actions.</p>
    </div>
  );
}

export default DashboardPage; 