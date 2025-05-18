import React, { useState, useEffect } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { getDeliveryByTrackingId as fetchDeliveryDetails } from '../api/deliveryService';

function TrackDeliveryPage() {
  const [searchParams] = useSearchParams();
  const initialTrackingId = searchParams.get('id') || '';

  const [trackingIdInput, setTrackingIdInput] = useState(initialTrackingId);
  const [deliveryDetails, setDeliveryDetails] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [searched, setSearched] = useState(false); // To know if a search has been attempted

  const handleFetchDetails = async (idToFetch) => {
    if (!idToFetch.trim()) {
      setError('Please enter a tracking ID.');
      setDeliveryDetails(null);
      setSearched(true);
      return;
    }
    setLoading(true);
    setError(null);
    setDeliveryDetails(null);
    setSearched(true);
    try {
      // The getDeliveryByTrackingId function in deliveryService.js is a mock.
      // It needs to be updated for an actual API call.
      const data = await fetchDeliveryDetails(idToFetch.trim());
      setDeliveryDetails(data);
    } catch (err) {
      setError(err.message || 'Failed to fetch delivery details. Check the ID or try again.');
      setDeliveryDetails(null);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    handleFetchDetails(trackingIdInput);
  };

  // Automatically fetch if an ID is in the URL params when the page loads
  useEffect(() => {
    if (initialTrackingId) {
      handleFetchDetails(initialTrackingId);
    }
    // Disabled linting for next line because handleFetchDetails causes infinite loop if added to dependency array
    // eslint-disable-next-line react-hooks/exhaustive-deps 
  }, [initialTrackingId]); // Only re-run if the initialTrackingId from URL changes

  return (
    <div>
      <h2>Track Your Delivery</h2>
      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="trackingId">Enter Tracking ID:</label>
          <input
            type="text"
            id="trackingId"
            value={trackingIdInput}
            onChange={(e) => setTrackingIdInput(e.target.value)}
            placeholder="e.g., mock-tracking-id-123"
            required
          />
        </div>
        <button type="submit" disabled={loading}>
          {loading ? 'Tracking...' : 'Track'}
        </button>
      </form>

      {loading && <p>Loading delivery details...</p>}
      {error && <p style={{ color: 'red' }}>Error: {error}</p>}
      
      {!loading && searched && !deliveryDetails && !error && (
        <p>No delivery found with that tracking ID.</p>
      )}

      {deliveryDetails && (
        <div>
          <h3>Delivery Details</h3>
          <p><strong>Tracking ID:</strong> {deliveryDetails.trackingId}</p>
          <p><strong>Status:</strong> {deliveryDetails.status}</p>
          {/* Display more details from your DeliveryResponseDTO */}
          <p><strong>Description:</strong> {deliveryDetails.description || deliveryDetails.details || 'N/A'}</p> 
          <p><strong>Recipient:</strong> {deliveryDetails.recipientName || 'N/A'}</p>
          <p><strong>Recipient Address:</strong> {deliveryDetails.recipientAddress || 'N/A'}</p>
          <p><strong>Pickup Address:</strong> {deliveryDetails.pickupAddress || 'N/A'}</p>
          <p><strong>Assigned Driver:</strong> {deliveryDetails.driverEmail || 'Not yet assigned'}</p>
          {/* Add estimated delivery date, delivery history, etc. as available */}
        </div>
      )}
      <br />
      <Link to="/">Back to Dashboard</Link>
    </div>
  );
}

export default TrackDeliveryPage; 