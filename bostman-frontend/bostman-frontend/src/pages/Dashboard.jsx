import { useAuth } from '../auth/useAuth';
import CustomerPanel from '../roles/CustomerPanel';
import AdminPanel from '../roles/AdminPanel';
import DriverPanel from '../roles/DriverPanel';

export default function Dashboard() {
  const user = useAuth();
  if (!user) return <p>Unauthorized</p>;

  return (
    <div className="p-6">
      <h1 className="text-2xl mb-4">Welcome, {user.email}</h1>

      {user.roles.includes('USER') && <CustomerPanel />}
      {user.roles.includes('ADMIN') && <AdminPanel />}
      {user.roles.includes('DRIVER') && <DriverPanel />}
    </div>
  );
}
