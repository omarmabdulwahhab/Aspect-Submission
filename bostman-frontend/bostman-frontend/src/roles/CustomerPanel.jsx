import { useState, useEffect } from 'react';
import api from '../api/axios';

export default function CustomerPanel() {
  const [form, setForm] = useState({
    pickupLocation: '',
    dropoffLocation: '',
    scheduledTime: '',
  });

  const [deliveries, setDeliveries] = useState([]);

  const createDelivery = async () => {
    try {
      await api.post('/deliveries', form);
      alert('Delivery scheduled!');
      fetchDeliveries();
    } catch {
      alert('Error scheduling delivery');
    }
  };

  const fetchDeliveries = async () => {
    const { data } = await api.get('/deliveries');
    setDeliveries(data);
  };

  useEffect(() => {
    fetchDeliveries();
  }, []);

  return (
    <div>
      <h2 className="text-xl mb-2">Schedule a Delivery</h2>
      <input placeholder="Pickup" className="border p-2 mr-2"
        onChange={e => setForm({ ...form, pickupLocation: e.target.value })} />
      <input placeholder="Dropoff" className="border p-2 mr-2"
        onChange={e => setForm({ ...form, dropoffLocation: e.target.value })} />
      <input type="datetime-local" className="border p-2 mr-2"
        onChange={e => setForm({ ...form, scheduledTime: e.target.value })} />
      <button onClick={createDelivery} className="bg-green-600 text-white px-3 py-2 rounded">
        Create
      </button>

      <h2 className="text-xl mt-6 mb-2">Your Deliveries</h2>
      <ul className="list-disc pl-6">
        {deliveries.map(d => (
          <li key={d.trackingId}>
            <b>{d.trackingId}</b>: {d.pickupLocation} → {d.dropoffLocation} | <i>{d.status}</i>
          </li>
        ))}
      </ul>
    </div>
  );
}
