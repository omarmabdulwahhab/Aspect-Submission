import React from 'react';
import { useEffect, useState } from 'react';
import api from '../api/axios';

export default function AdminPanel() {
  const [deliveries, setDeliveries] = useState([]);
  const [assignForm, setAssignForm] = useState({ trackingId: '', driverEmail: '' });
  const [statusForm, setStatusForm] = useState({ trackingId: '', status: 'IN_TRANSIT' });

  const fetchAll = async () => {
    try {
      const { data } = await api.get('/deliveries/all'); // You may need to create this backend route
      setDeliveries(data);
    } catch (err) {
      alert('Could not fetch deliveries');
    }
  };

  useEffect(() => {
    fetchAll();
  }, []);

  const assignDriver = async () => {
    try {
      await api.post(`/admin/assign-driver`, null, {
        params: assignForm
      });
      alert('Driver assigned');
      fetchAll();
    } catch {
      alert('Failed to assign driver');
    }
  };

  const updateStatus = async () => {
    try {
      await api.post(`/admin/update-status`, null, {
        params: statusForm
      });
      alert('Status updated');
      fetchAll();
    } catch {
      alert('Failed to update status');
    }
  };

  return (
    <div>
      <h2 className="text-xl font-bold mb-4">All Deliveries</h2>
      <ul className="mb-6">
        {deliveries.map(d => (
          <li key={d.trackingId}>
            <b>{d.trackingId}</b> — {d.status} | Driver: {d.assignedDriver?.fullName || 'Unassigned'}
          </li>
        ))}
      </ul>

      <h3 className="text-lg font-semibold">Assign Driver</h3>
      <input placeholder="Tracking ID" className="border p-1 mr-2"
        onChange={e => setAssignForm({ ...assignForm, trackingId: e.target.value })} />
      <input placeholder="Driver Email" className="border p-1 mr-2"
        onChange={e => setAssignForm({ ...assignForm, driverEmail: e.target.value })} />
      <button onClick={assignDriver} className="bg-blue-500 text-white px-2 py-1 rounded">Assign</button>

      <h3 className="text-lg font-semibold mt-6">Update Status</h3>
      <input placeholder="Tracking ID" className="border p-1 mr-2"
        onChange={e => setStatusForm({ ...statusForm, trackingId: e.target.value })} />
      <select className="border p-1 mr-2"
        onChange={e => setStatusForm({ ...statusForm, status: e.target.value })}>
        <option>IN_TRANSIT</option>
        <option>DELIVERED</option>
        <option>CANCELLED</option>
      </select>
      <button onClick={updateStatus} className="bg-green-600 text-white px-2 py-1 rounded">Update</button>
    </div>
  );
}
