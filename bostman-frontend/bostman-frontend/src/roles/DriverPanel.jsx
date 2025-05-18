import React from 'react';
import { useEffect, useState } from 'react';
import api from '../api/axios';

export default function DriverPanel() {
  const [deliveries, setDeliveries] = useState([]);

  const fetchDeliveries = async () => {
    try {
      const { data } = await api.get('/driver/deliveries');
      setDeliveries(data);
    } catch {
      alert('Failed to fetch deliveries');
    }
  };

  const markAsDelivered = async (trackingId) => {
    try {
      await api.post('/driver/complete', null, {
        params: { trackingId }
      });
      alert('Delivery marked as delivered');
      fetchDeliveries();
    } catch {
      alert('Failed to update status');
    }
  };

  useEffect(() => {
    fetchDeliveries();
  }, []);

  return (
    <div>
      <h2 className="text-xl font-bold mb-4">Assigned Deliveries</h2>
      {deliveries.length === 0 ? (
        <p>No deliveries assigned.</p>
      ) : (
        <ul className="list-disc pl-6">
          {deliveries.map(d => (
            <li key={d.trackingId} className="mb-2">
              <b>{d.trackingId}</b>: {d.pickupLocation} → {d.dropoffLocation} <br />
              Status: <i>{d.status}</i><br />
              {d.status !== 'DELIVERED' && (
                <button
                  onClick={() => markAsDelivered(d.trackingId)}
                  className="bg-green-600 text-white px-3 py-1 mt-1 rounded"
                >
                  Mark as Delivered
                </button>
              )}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
