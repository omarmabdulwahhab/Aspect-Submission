import React from 'react';
import { useAuth } from '../auth/useAuth';

export default function Dashboard() {
  const user = useAuth();
  if (!user) return <p>Unauthorized</p>;

  return (
    <div className="flex flex-col items-center justify-center h-screen bg-gray-100">
      <h1 className="text-4xl font-bold">Welcome, {user.email}</h1>
    </div>
  );
}
