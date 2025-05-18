import React, { useState, useEffect } from 'react';
import { Link, useSearchParams, useNavigate } from 'react-router-dom';
import { assignDriver as apiAssignDriver } from '../api/adminService';
import { getDeliveryByTrackingId as fetchDeliveryDetails } from '../api/deliveryService'; 
// You might need a service to fetch all drivers, e.g., from userService or a new adminService endpoint
// import { getAllDrivers } from '../api/userService'; // Assuming this exists

function AdminAssignDriverPage() {
  const [searchParams] = useSearchParams();
  const trackingId = searchParams.get('trackingId');
  
  const [delivery, setDelivery] = useState(null);
  const [drivers, setDrivers] = useState([]); // Mock drivers for now
  const [selectedDriverEmail, setSelectedDriverEmail] = useState('');
  
  const [loading, setLoading] = useState(false);
  const [pageError, setPageError] = useState(null); // For errors fetching delivery/drivers
  const [assignError, setAssignError] = useState(null); // For errors during assignment
  const [assignSuccess, setAssignSuccess] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const loadData = async () => {
      if (!trackingId) {
        setPageError('No tracking ID provided.');
        return;
      }
      setLoading(true);
      setPageError(null);
      try {
        const deliveryData = await fetchDeliveryDetails(trackingId);
        setDelivery(deliveryData);

        // MOCK DRIVER LIST - Replace with actual API call to fetch drivers
        // const driverData = await getAllDrivers(); 
        // setDrivers(driverData || []);
        setDrivers([
          { id: '1', email: 'driver1@example.com', name: 'Driver One' },
          { id: '2', email: 'driver2@example.com', name: 'Driver Two' },
          { id: '3', email: 'some.driver@work.com', name: 'Some Driver'}
        ]);
        if (deliveryData && deliveryData.driverEmail) {
            setSelectedDriverEmail(deliveryData.driverEmail);
        }

      } catch (err) {
        setPageError(err.message || 'Failed to load delivery or driver data.');
      } finally {
        setLoading(false);
      }
    };
    loadData();
  }, [trackingId]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!selectedDriverEmail) {
      setAssignError('Please select a driver.');
      return;
    }
    setLoading(true);
    setAssignError(null);
    setAssignSuccess('');
    try {
      const response = await apiAssignDriver(trackingId, selectedDriverEmail);
      setAssignSuccess(response.message || 'Driver assigned successfully!');
      // Optionally, navigate back or update delivery details locally
      // setTimeout(() => navigate('/admin/all-deliveries'), 2000);
    } catch (err) {
      setAssignError(err.message || 'Failed to assign driver.');
    } finally {
      setLoading(false);
    }
  };

  if (!trackingId) {
    return <div><p>No delivery tracking ID specified.</p><Link to="/admin/all-deliveries">Go Back</Link></div>;
  }
  if (loading && !delivery) { // Initial load
    return <p>Loading delivery and driver information...</p>;
  }
  if (pageError) {
    return <div><p style={{ color: 'red' }}>Error: {pageError}</p><Link to="/admin/all-deliveries">Go Back</Link></div>;
  }
  if (!delivery) {
    return <div><p>Delivery not found.</p><Link to="/admin/all-deliveries">Go Back</Link></div>;
  }

  return (
    <div>
      <h2>Assign Driver to Delivery</h2>
      <p><strong>Tracking ID:</strong> {delivery.trackingId}</p>
      <p><strong>Current Status:</strong> {delivery.status}</p>
      <p><strong>Current Assigned Driver:</strong> {delivery.driverEmail || 'None'}</p>
      <p><strong>Recipient:</strong> {delivery.recipientName} at {delivery.recipientAddress}</p>

      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="driverSelect">Select Driver:</label>
          <select 
            id="driverSelect" 
            value={selectedDriverEmail} 
            onChange={(e) => setSelectedDriverEmail(e.target.value)}
            required
          >
            <option value="">-- Select a Driver --</option>
            {drivers.map(driver => (
              <option key={driver.id || driver.email} value={driver.email}>
                {driver.name} ({driver.email})
              </option>
            ))}
          </select>
        </div>

        {assignError && <p style={{ color: 'red' }}>{assignError}</p>}
        {assignSuccess && <p style={{ color: 'green' }}>{assignSuccess}</p>}

        <button type="submit" disabled={loading || !selectedDriverEmail}>
          {loading ? 'Assigning...' : 'Assign Driver'}
        </button>
      </form>
      <br />
      <Link to="/admin/all-deliveries">Back to All Deliveries</Link>
      <br />
      <Link to="/">Back to Dashboard</Link>
    </div>
  );
}

export default AdminAssignDriverPage; 