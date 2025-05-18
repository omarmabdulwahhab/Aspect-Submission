import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { getMyDeliveries as fetchMyDeliveries, deleteDelivery as apiDeleteDelivery } from '../api/deliveryService';
// import { useAuth } from '../contexts/AuthContext'; // Not strictly needed here if ProtectedRoute handles auth

function MyDeliveriesPage() {
  const [deliveries, setDeliveries] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [actionError, setActionError] = useState(null); // For errors during delete
  const [actionSuccess, setActionSuccess] = useState(''); // For success messages

  const loadDeliveries = async () => {
    try {
      setLoading(true);
      setError(null);
      setActionError(null); // Clear previous action errors
      setActionSuccess(''); // Clear previous success messages
      const data = await fetchMyDeliveries(); 
      setDeliveries(data || []);
    } catch (err) {
      setError(err.message || 'Failed to fetch deliveries. Please try again later.');
      setDeliveries([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadDeliveries();
  }, []);

  const handleDelete = async (trackingId) => {
    if (window.confirm('Are you sure you want to delete this delivery?')) {
      try {
        setLoading(true); // Indicate loading state for the action
        setActionError(null);
        setActionSuccess('');
        await apiDeleteDelivery(trackingId);
        setActionSuccess('Delivery deleted successfully!');
        // Refresh the list of deliveries
        await loadDeliveries(); // Re-fetch to reflect the deletion
      } catch (err) {
        setActionError(err.message || 'Failed to delete delivery.');
        setLoading(false); // Ensure loading is false on error
      } finally {
        // setLoading(false); // Already handled in loadDeliveries or if error specific above
      }
    }
  };

  if (loading && deliveries.length === 0) { // Show initial loading only
    return <div>Loading your deliveries...</div>;
  }

  if (error) {
    return <div><p className="text-bostman-error">Error: {error}</p><Link to="/" className="button-style">Back to Dashboard</Link></div>;
  }

  return (
    <div className="container mx-auto p-4">
      <h2 className="text-2xl font-semibold mb-6 text-bostman-orange-dark">My Deliveries</h2>
      
      {actionError && <p className="text-bostman-error p-3 bg-red-100 rounded-md mb-4">Error: {actionError}</p>}
      {actionSuccess && <p className="text-bostman-success p-3 bg-green-100 rounded-md mb-4">{actionSuccess}</p>}

      {deliveries.length === 0 && !loading ? (
        <div className="card text-center">
            <p className="text-lg">You have no deliveries scheduled.</p>
            <Link to="/schedule-delivery" className="button-style mt-4">Schedule a Delivery</Link>
        </div>
      ) : (
        <ul className="space-y-6">
          {deliveries.map((delivery) => (
            <li key={delivery.trackingId || delivery.id} className="card hover:shadow-lg transition-shadow duration-200 ease-in-out">
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div className="md:col-span-2">
                    <p><strong className="text-bostman-orange-dark">Tracking ID:</strong> {delivery.trackingId}</p>
                    <p><strong className="text-bostman-orange-dark">Status:</strong> {delivery.status}</p>
                    <p><strong className="text-bostman-orange-dark">Details:</strong> {delivery.description || 'N/A'} </p>
                    <p><strong className="text-bostman-orange-dark">Recipient:</strong> {delivery.recipientName || 'N/A'} at {delivery.recipientAddress || 'N/A'}</p>
                    <p><strong className="text-bostman-orange-dark">Pickup:</strong> {delivery.pickupAddress || 'N/A'}</p>
                </div>
                <div className="flex flex-col md:items-end md:justify-between space-y-2 md:space-y-0">
                    <Link to={`/track-delivery?id=${delivery.trackingId}`} className="button-style text-center w-full md:w-auto">Track this delivery</Link>
                    <button 
                        onClick={() => handleDelete(delivery.trackingId)}
                        className="button-style bg-red-600 hover:bg-red-700 text-white w-full md:w-auto disabled:opacity-50"
                        disabled={loading} // Disable button if any loading is active
                    >
                        {loading && !error && !actionError ? 'Deleting...' : 'Delete Delivery'}
                    </button>
                </div>
              </div>
            </li>
          ))}
        </ul>
      )}
      <div className="mt-8 text-center">
        <Link to="/" className="button-style">Back to Dashboard</Link>
      </div>
    </div>
  );
}

export default MyDeliveriesPage; 