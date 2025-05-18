import { getAuthToken } from '../utils/authUtils';

const API_URL = '/api/admin';

export const getAllDeliveries = async () => {
    const token = getAuthToken();
    const response = await fetch(`${API_URL}/deliveries`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });
    if (!response.ok) {
        throw new Error('Failed to fetch deliveries');
    }
    return response.json();
};

export const assignDriverToDelivery = async (deliveryId, driverId) => {
    const token = getAuthToken();
    const response = await fetch(`${API_URL}/deliveries/${deliveryId}/assign?driverId=${driverId}`, {
        method: 'PUT',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });
    if (!response.ok) {
        throw new Error('Failed to assign driver');
    }
    return response.json();
};

export const getDrivers = async () => {
    const token = getAuthToken();
    const response = await fetch(`${API_URL}/users/drivers`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });
    if (!response.ok) {
        throw new Error('Failed to fetch drivers');
    }
    return response.json();
}; 