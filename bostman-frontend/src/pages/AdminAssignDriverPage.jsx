import React, { useState, useEffect } from 'react';
import { getDrivers, assignDriverToDelivery, getAllDeliveries } from '../api/adminService';

function AdminAssignDriverPage() {
    const [deliveries, setDeliveries] = useState([]);
    const [drivers, setDrivers] = useState([]);
    const [selectedDelivery, setSelectedDelivery] = useState('');
    const [selectedDriver, setSelectedDriver] = useState('');
    const [error, setError] = useState('');
    const [successMessage, setSuccessMessage] = useState('');

    useEffect(() => {
        const fetchInitialData = async () => {
            try {
                setError('');
                const fetchedDeliveries = await getAllDeliveries();
                setDeliveries(fetchedDeliveries || []);
                const fetchedDrivers = await getDrivers();
                setDrivers(fetchedDrivers || []);
            } catch (err) {
                setError(err.message || 'Failed to fetch data');
                console.error(err);
            }
        };
        fetchInitialData();
    }, []);

    const handleAssignDriver = async (e) => {
        e.preventDefault();
        if (!selectedDelivery || !selectedDriver) {
            setError('Please select a delivery and a driver.');
            return;
        }
        try {
            setError('');
            setSuccessMessage('');
            await assignDriverToDelivery(selectedDelivery, selectedDriver);
            setSuccessMessage('Driver assigned successfully!');
            // Optionally, refresh deliveries list or update UI
            const fetchedDeliveries = await getAllDeliveries(); // Refresh deliveries
            setDeliveries(fetchedDeliveries || []);
            setSelectedDelivery('');
            setSelectedDriver('');
        } catch (err) {
            setError(err.message || 'Failed to assign driver');
            console.error(err);
        }
    };

    return (
        <div>
            <h2>Assign Driver to Delivery</h2>
            {error && <p style={{ color: 'red' }}>{error}</p>}
            {successMessage && <p style={{ color: 'green' }}>{successMessage}</p>}
            <form onSubmit={handleAssignDriver}>
                <div>
                    <label htmlFor="delivery">Select Delivery:</label>
                    <select id="delivery" value={selectedDelivery} onChange={(e) => setSelectedDelivery(e.target.value)} required>
                        <option value="">-- Select Delivery --</option>
                        {deliveries.map(delivery => (
                            <option key={delivery.id} value={delivery.id}>
                                ID: {delivery.id} - Status: {delivery.status} - (To: {delivery.recipientAddress})
                            </option>
                        ))}
                    </select>
                </div>
                <div>
                    <label htmlFor="driver">Select Driver:</label>
                    <select id="driver" value={selectedDriver} onChange={(e) => setSelectedDriver(e.target.value)} required>
                        <option value="">-- Select Driver --</option>
                        {drivers.map(driver => (
                            // Assuming driver object has id and email, and possibly firstName, lastName
                            <option key={driver.id} value={driver.id}>
                                {driver.firstName || 'N/A'} {driver.lastName || ''} ({driver.email}) - ID: {driver.id}
                            </option>
                        ))}
                    </select>
                </div>
                <button type="submit">Assign Driver</button>
            </form>
        </div>
    );
}

export default AdminAssignDriverPage; 