import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { scheduleDelivery as apiScheduleDelivery } from '../api/deliveryService';
// import { useAuth } from '../contexts/AuthContext'; // For user context if needed beyond token

function ScheduleDeliveryPage() {
  // These fields should align with your DeliveryRequestDTO on the backend
  const [pickupAddress, setPickupAddress] = useState('');
  const [recipientAddress, setRecipientAddress] = useState('');
  const [recipientName, setRecipientName] = useState('');
  const [recipientPhone, setRecipientPhone] = useState('');
  const [description, setDescription] = useState(''); // Optional description
  // Add other DTO fields as necessary, e.g., packageSize, weight, preferredPickupTime

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setSuccessMessage('');

    const deliveryRequestDTO = {
      pickupAddress,
      recipientAddress,
      recipientName,
      recipientPhone,
      description,
      // Add any other fields your backend DTO expects
    };

    try {
      // The scheduleDelivery function in deliveryService.js is currently a mock.
      // It needs to be updated for actual API call.
      const trackingId = await apiScheduleDelivery(deliveryRequestDTO);
      setSuccessMessage(`Delivery scheduled successfully! Tracking ID: ${trackingId}`);
      // Clear form or redirect
      setPickupAddress('');
      setRecipientAddress('');
      setRecipientName('');
      setRecipientPhone('');
      setDescription('');
      // Optionally navigate to my-deliveries or the tracking page after a delay
      // setTimeout(() => navigate('/my-deliveries'), 3000);
    } catch (err) {
      setError(err.message || 'Failed to schedule delivery. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <h2>Schedule New Delivery</h2>
      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="pickupAddress">Pickup Address:</label>
          <input
            type="text"
            id="pickupAddress"
            value={pickupAddress}
            onChange={(e) => setPickupAddress(e.target.value)}
            required
          />
        </div>
        <div>
          <label htmlFor="recipientAddress">Recipient Address:</label>
          <input
            type="text"
            id="recipientAddress"
            value={recipientAddress}
            onChange={(e) => setRecipientAddress(e.target.value)}
            required
          />
        </div>
        <div>
          <label htmlFor="recipientName">Recipient Name:</label>
          <input
            type="text"
            id="recipientName"
            value={recipientName}
            onChange={(e) => setRecipientName(e.target.value)}
            required
          />
        </div>
        <div>
          <label htmlFor="recipientPhone">Recipient Phone:</label>
          <input
            type="tel" // Use tel for phone numbers
            id="recipientPhone"
            value={recipientPhone}
            onChange={(e) => setRecipientPhone(e.target.value)}
            required
          />
        </div>
        <div>
          <label htmlFor="description">Description (Optional):</label>
          <textarea
            id="description"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
          />
        </div>
        
        {/* Add more form fields here if your DeliveryRequestDTO requires them */}

        {error && <p style={{ color: 'red' }}>Error: {error}</p>}
        {successMessage && <p style={{ color: 'green' }}>{successMessage}</p>}

        <button type="submit" disabled={loading}>
          {loading ? 'Scheduling...' : 'Schedule Delivery'}
        </button>
      </form>
      <br />
      <Link to="/">Back to Dashboard</Link>
    </div>
  );
}

export default ScheduleDeliveryPage; 