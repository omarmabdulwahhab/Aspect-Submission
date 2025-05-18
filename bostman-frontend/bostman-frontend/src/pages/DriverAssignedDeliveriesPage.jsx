import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { getAssignedDeliveries as fetchAssignedDeliveries, completeDelivery as apiCompleteDelivery } from '../api/driverService';
import { useAuth } from '../contexts/AuthContext'; // To get driver info if needed, though token is primary

function DriverAssignedDeliveriesPage() {
  const [deliveries, setDeliveries] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [updateError, setUpdateError] = useState(null);
  const [updateSuccess, setUpdateSuccess] = useState('');
  const { user } = useAuth(); // user.email might be used or confirmed by backend via token

  const loadAssignedDeliveries = async () => {
    try {
      setLoading(true);
      setError(null);
      setUpdateError(null);
      setUpdateSuccess('');
      // Assumes driverService.getAssignedDeliveries uses the token to identify the driver
      const data = await fetchAssignedDeliveries();
      setDeliveries(data || []);
    } catch (err) {
      setError(err.message || 'Failed to fetch assigned deliveries.');
      setDeliveries([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadAssignedDeliveries();
  }, []); // Load once on mount

  const handleCompleteDelivery = async (trackingId) => {
    setLoading(true); // Use main loading or a specific one for this action
    setUpdateError(null);
    setUpdateSuccess('');
    try {
      // driverService.completeDelivery uses token + trackingId
      const responseMessage = await apiCompleteDelivery(trackingId);
      setUpdateSuccess(responseMessage.message || `Delivery ${trackingId} marked as complete.`);
      // Refresh the list of deliveries after marking one as complete
      loadAssignedDeliveries(); 
    } catch (err) {
      setUpdateError(err.message || `Failed to mark delivery ${trackingId} as complete.`);
    } finally {
      setLoading(false); // Reset main loading or the specific one
    }
  };

  if (loading && deliveries.length === 0) { // Show initial loading message only if no deliveries are yet shown
    return <div>Loading your assigned deliveries...</div>;
  }

  if (error) {
    return <div><p style={{ color: 'red' }}>Error: {error}</p><Link to="/">Back to Dashboard</Link></div>;
  }

  return (
    <div>
      <h2>My Assigned Deliveries</h2>
      {user && <p>Viewing deliveries for: <strong>{user.email}</strong></p>}
      
      {updateError && <p style={{ color: 'red' }}>Update Error: {updateError}</p>}
      {updateSuccess && <p style={{ color: 'green' }}>{updateSuccess}</p>}

      {deliveries.length === 0 && !loading && (
        <p>You have no deliveries assigned to you at the moment, or all are completed.</p>
      )}
      {deliveries.length > 0 && (
        <ul>
          {deliveries.map((delivery) => (
            <li key={delivery.trackingId || delivery.id}>
              <p><strong>Tracking ID:</strong> {delivery.trackingId}</p>
              <p><strong>Status:</strong> {delivery.status}</p>
              <p><strong>Recipient:</strong> {delivery.recipientName} at {delivery.recipientAddress}</p>
              <p><strong>Pickup Address:</strong> {delivery.pickupAddress || 'N/A'}</p>
              <p><strong>Description:</strong> {delivery.description || 'N/A'}</p>
              {/* Add more relevant details for the driver */}

              {delivery.status !== 'DELIVERED' && delivery.status !== 'COMPLETED' && ( // Assuming COMPLETED is a final state
                <button 
                  onClick={() => handleCompleteDelivery(delivery.trackingId)}
                  disabled={loading} // Disable button while any loading is true
                >
                  {loading ? 'Updating...' : 'Mark as Delivered'}
                </button>
              )}
              <hr />
            </li>
          ))}
        </ul>
      )}
      <Link to="/">Back to Dashboard</Link>
    </div>
  );
}

export default DriverAssignedDeliveriesPage; 