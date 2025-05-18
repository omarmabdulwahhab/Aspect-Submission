import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { getAllDeliveries as fetchAllDeliveries } from '../api/deliveryService';
// import { updateStatus as apiUpdateStatus } from '../api/adminService'; // If status updates are done here
// import { useAuth } from '../contexts/AuthContext';

function AdminAllDeliveriesPage() {
  const [deliveries, setDeliveries] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  // const [updateError, setUpdateError] = useState(null);
  // const [updateSuccess, setUpdateSuccess] = useState('');

  const loadAllDeliveries = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await fetchAllDeliveries();
      setDeliveries(data || []);
    } catch (err) {
      setError(err.message || 'Failed to fetch all deliveries.');
      setDeliveries([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadAllDeliveries();
  }, []);

  // Placeholder for a status update function if needed directly on this page
  // const handleUpdateStatus = async (trackingId, newStatus) => {
  //   setLoading(true); // Or a specific loading state for this action
  //   try {
  //     await apiUpdateStatus(trackingId, newStatus);
  //     setUpdateSuccess(`Status for ${trackingId} updated to ${newStatus}`);
  //     loadAllDeliveries(); // Refresh list
  //   } catch (err) {
  //     setUpdateError(err.message || 'Failed to update status.');
  //   } finally {
  //     setLoading(false);
  //   }
  // };

  if (loading) {
    return <div>Loading all deliveries...</div>;
  }

  if (error) {
    return <div><p style={{ color: 'red' }}>Error: {error}</p><Link to="/">Back to Dashboard</Link></div>;
  }

  return (
    <div>
      <h2>All Deliveries (Admin View)</h2>
      {/* {updateError && <p style={{color: 'red'}}>{updateError}</p>}
      {updateSuccess && <p style={{color: 'green'}}>{updateSuccess}</p>} */} 

      {deliveries.length === 0 ? (
        <p>No deliveries found in the system.</p>
      ) : (
        <table>
          <thead>
            <tr>
              <th>Tracking ID</th>
              <th>Status</th>
              <th>User Email</th>
              <th>Pickup Address</th>
              <th>Delivery Address</th>
              <th>Recipient Name</th>
              <th>Assigned Driver</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {deliveries.map((delivery) => (
              <tr key={delivery.trackingId || delivery.id}>
                <td>{delivery.trackingId}</td>
                <td>{delivery.status}</td>
                <td>{delivery.userEmail || 'N/A'} {/* Assuming DTO has userEmail */}</td>
                <td>{delivery.pickupAddress}</td>
                <td>{delivery.deliveryAddress}</td>
                <td>{delivery.recipientName}</td>
                <td>{delivery.driverEmail || 'Not Assigned'} {/* Assuming DTO has driverEmail */}</td>
                <td>
                  <Link to={`/track-delivery?id=${delivery.trackingId}`}>View Details</Link>
                  {/* Add links/buttons for admin actions like Update Status, Assign Driver */}
                  {/* Example: <button onClick={() => handleUpdateStatus(delivery.trackingId, 'NEW_STATUS')}>Update</button> */}
                  <Link to={`/admin/assign-driver?trackingId=${delivery.trackingId}`}> Assign Driver</Link>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
      <br />
      <Link to="/">Back to Dashboard</Link>
    </div>
  );
}

export default AdminAllDeliveriesPage; 