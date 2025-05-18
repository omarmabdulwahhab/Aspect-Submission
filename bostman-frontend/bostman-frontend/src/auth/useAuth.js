import jwtDecode from 'jwt-decode';

export function useAuth() {
  const token = localStorage.getItem('token');
  if (!token) return null;

  try {
    const decoded = jwtDecode(token);
    return {
      email: decoded.sub,
      roles: decoded.roles || [],  // depends how you store roles
    };
  } catch {
    return null;
  }
}
