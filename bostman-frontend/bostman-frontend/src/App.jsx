import React from 'react';
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
  Link
} from 'react-router-dom';
import './App.css';
import { useAuth } from './contexts/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';

// Pages
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import DashboardPage from './pages/DashboardPage';
import MyDeliveriesPage from './pages/MyDeliveriesPage';
import ScheduleDeliveryPage from './pages/ScheduleDeliveryPage';
import TrackDeliveryPage from './pages/TrackDeliveryPage';
import DriverAssignedDeliveriesPage from './pages/DriverAssignedDeliveriesPage';
import AdminAllDeliveriesPage from './pages/AdminAllDeliveriesPage';
import AdminAssignDriverPage from './pages/AdminAssignDriverPage';
import EditDeliveryPage from './pages/EditDeliveryPage';

// Placeholder pages for features
// const AdminAssignDriverPage = () => <div><h2>Assign Driver (Admin)</h2><p>Form to assign driver...</p><Link to="/">Back to Dashboard</Link></div>; // Remove old placeholder

const NotFoundPage = () => <div><h2>404 - Page Not Found</h2><Link to="/">Go Home</Link></div>;


function App() {
  const { isAuthenticated, user } = useAuth(); // user might be needed for role-based nav elements

  return (
    <Router>
      <div className="App">
        {/* Basic Nav (could be moved to a Navbar component) */}
        {/* <nav>
          <ul>
            <li><Link to="/">Home</Link></li>
            {!isAuthenticated && <li><Link to="/login">Login</Link></li>}
            {!isAuthenticated && <li><Link to="/register">Register</Link></li>}
          </ul>
        </nav> */}

        <Routes>
          {/* Public Routes */}
          <Route path="/login" element={!isAuthenticated ? <LoginPage /> : <Navigate to="/" />} />
          <Route path="/register" element={!isAuthenticated ? <RegisterPage /> : <Navigate to="/" />} />

          {/* Protected Routes (Generic User) */}
          <Route path="/" element={<ProtectedRoute />}>
            <Route index element={<DashboardPage />} />
            <Route path="my-deliveries" element={<MyDeliveriesPage />} />
            <Route path="schedule-delivery" element={<ScheduleDeliveryPage />} />
            <Route path="track-delivery" element={<TrackDeliveryPage />} />
            <Route path="edit-delivery/:trackingId" element={<EditDeliveryPage />} />
          </Route>

          {/* Protected Admin Routes */}
          <Route path="/admin" element={<ProtectedRoute allowedRoles={['ADMIN']} />}>
            {/* Add a default admin dashboard page or redirect */}
            <Route index element={<Navigate to="/admin/all-deliveries" replace />} /> 
            <Route path="all-deliveries" element={<AdminAllDeliveriesPage />} />
            <Route path="assign-driver" element={<AdminAssignDriverPage />} />
            {/* More admin routes here */}
          </Route>

          {/* Protected Driver Routes */}
          <Route path="/driver" element={<ProtectedRoute allowedRoles={['DRIVER']} />}>
            <Route index element={<Navigate to="/driver/assigned-deliveries" replace />} /> 
            <Route path="assigned-deliveries" element={<DriverAssignedDeliveriesPage />} />
            {/* More driver routes here */}
          </Route>
          
          <Route path="*" element={<NotFoundPage />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
