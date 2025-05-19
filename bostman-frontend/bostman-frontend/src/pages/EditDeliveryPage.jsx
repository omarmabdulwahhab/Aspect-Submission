import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { getDeliveryByTrackingId, editDelivery as apiEditDelivery } from '../api/deliveryService';
// You might want to import useAuth to get user roles if admins have different edit capabilities
// import { useAuth } from '../contexts/AuthContext';

function EditDeliveryPage() {
  const { trackingId } = useParams();
  const navigate = useNavigate();
  // const { user } = useAuth(); // Example: To check if user is admin

  const [delivery, setDelivery] = useState({
    pickupAddress: '',
    recipientAddress: '',
    recipientName: '',
    recipientPhone: '',
    description: '',
    // scheduledTime: '', // If applicable
  });
  const [initialLoading, setInitialLoading] = useState(true);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState('');

  useEffect(() => {
    const fetchDelivery = async () => {
      try {
        setInitialLoading(true);
        setError(null);
        const data = await getDeliveryByTrackingId(trackingId);
        if (data) {
          setDelivery({
            pickupAddress: data.pickupAddress || '',
            recipientAddress: data.recipientAddress || '',
            recipientName: data.recipientName || '',
            recipientPhone: data.recipientPhone || '',
            description: data.description || '',
            // scheduledTime: data.scheduledTime || '', // Format as needed for input type datetime-local
          });
        } else {
          setError('Delivery not found or you do not have permission to view it.');
        }
      } catch (err) {
        setError(err.message || 'Failed to fetch delivery details.');
      } finally {
        setInitialLoading(false);
      }
    };

    if (trackingId) {
      fetchDelivery();
    }
  }, [trackingId]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setDelivery(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setSuccessMessage('');

    const deliveryUpdateDTO = {
      pickupAddress: delivery.pickupAddress,
      recipientAddress: delivery.recipientAddress,
      recipientName: delivery.recipientName,
      recipientPhone: delivery.recipientPhone,
      description: delivery.description,
      // scheduledTime: delivery.scheduledTime ? new Date(delivery.scheduledTime).toISOString() : null, // Ensure correct format if sent
    };

    try {
      await apiEditDelivery(trackingId, deliveryUpdateDTO);
      setSuccessMessage('Delivery updated successfully!');
      setTimeout(() => navigate('/my-deliveries'), 2000); // Redirect after a short delay
    } catch (err) {
      setError(err.message || 'Failed to update delivery. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  if (initialLoading) {
    return <div className="container mx-auto p-4">Loading delivery details...</div>;
  }

  if (error && !successMessage) { // Don't show initial fetch error if a success message is present
    return (
      <div className="container mx-auto p-4">
        <p className="text-bostman-error">Error: {error}</p>
        <Link to="/my-deliveries" className="button-style mt-4">Back to My Deliveries</Link>
      </div>
    );
  }

  return (
    <div className="container mx-auto p-4 max-w-lg">
      <h2 className="text-2xl font-semibold mb-6 text-bostman-orange-dark">Edit Delivery (ID: {trackingId})</h2>
      <form onSubmit={handleSubmit} className="space-y-4 card">
        <div>
          <label htmlFor="pickupAddress" className="label-style">Pickup Address:</label>
          <input
            type="text"
            id="pickupAddress"
            name="pickupAddress"
            value={delivery.pickupAddress}
            onChange={handleChange}
            className="input-style"
            required
          />
        </div>
        <div>
          <label htmlFor="recipientAddress" className="label-style">Recipient Address:</label>
          <input
            type="text"
            id="recipientAddress"
            name="recipientAddress"
            value={delivery.recipientAddress}
            onChange={handleChange}
            className="input-style"
            required
          />
        </div>
        <div>
          <label htmlFor="recipientName" className="label-style">Recipient Name:</label>
          <input
            type="text"
            id="recipientName"
            name="recipientName"
            value={delivery.recipientName}
            onChange={handleChange}
            className="input-style"
            required
          />
        </div>
        <div>
          <label htmlFor="recipientPhone" className="label-style">Recipient Phone:</label>
          <input
            type="tel"
            id="recipientPhone"
            name="recipientPhone"
            value={delivery.recipientPhone}
            onChange={handleChange}
            className="input-style"
            required
          />
        </div>
        <div>
          <label htmlFor="description" className="label-style">Description (Optional):</label>
          <textarea
            id="description"
            name="description"
            value={delivery.description}
            onChange={handleChange}
            className="input-style h-24"
          />
        </div>
        {/* Add scheduledTime input if it's editable 
        <div>
          <label htmlFor="scheduledTime" className="label-style">Scheduled Time (Optional):</label>
          <input
            type="datetime-local"
            id="scheduledTime"
            name="scheduledTime"
            value={delivery.scheduledTime} // Ensure this is formatted correctly for the input
            onChange={handleChange}
            className="input-style"
          />
        </div>
        */}
        
        {error && <p className="text-bostman-error">Error: {error}</p>}
        {successMessage && <p className="text-bostman-success">{successMessage}</p>}

        <div className="flex justify-end space-x-3 pt-4">
            <Link to="/my-deliveries" className="button-style-secondary">
                Cancel
            </Link>
            <button type="submit" className="button-style" disabled={loading}>
                {loading ? 'Saving Changes...' : 'Save Changes'}
            </button>
        </div>
      </form>
    </div>
  );
}

export default EditDeliveryPage; 