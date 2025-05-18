import React from 'react';
import { useState } from 'react';
import api from '../api/axios';
import { useNavigate } from 'react-router-dom';

export default function Login() {
  const [form, setForm] = useState({ email: '', password: '' });
  const navigate = useNavigate();

  const handleLogin = async () => {
    try {
      const { data } = await api.post('/login/', form); // Use the correct endpoint
      localStorage.setItem('token', data.token); // Save the JWT token
      navigate('/dashboard');
    } catch (err) {
      alert('Login failed. Please check your credentials.');
    }
  };

  return (
    <div className="p-4 max-w-md mx-auto">
      <h1 className="text-xl font-bold">Login</h1>
      <input className="border p-2 w-full my-2" placeholder="Email"
        onChange={e => setForm({ ...form, email: e.target.value })} />
      <input className="border p-2 w-full my-2" placeholder="Password" type="password"
        onChange={e => setForm({ ...form, password: e.target.value })} />
      <button onClick={handleLogin} className="bg-blue-600 text-white px-4 py-2 rounded">Login</button>
    </div>
  );
}
