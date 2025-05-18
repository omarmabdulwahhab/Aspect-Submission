import { getAuthToken } from '../utils/authUtils';

const API_BASE_URL = 'http://localhost:8080/api/admin'; // Assuming Spring Boot runs on 8080 by default

async function handleResponse(response) {
  if (!response.ok) {
    let errorMessage = `API Error: ${response.status} ${response.statusText}`;
    try {
      const errorBody = await response.json();
      errorMessage = errorBody.message || errorBody.error || errorMessage;
    } catch (e) {
      // Ignore if response body is not JSON or empty
    }
    throw new Error(errorMessage);
  }
  const contentType = response.headers.get("content-type");
  if (contentType && contentType.indexOf("application/json") !== -1) {
    return response.json();
  } else {
    return response.text();
  }
}

/**
 * Assigns a driver to a delivery.
 * Backend: POST /api/admin/assign-driver (Requires Auth - Admin Role)
 * @param {string} trackingId - The tracking ID of the delivery.
 * @param {string} driverEmail - The email of the driver to assign.
 * @returns {Promise<object>} An object with a message property (e.g. { message: "Success" })
 */
export const assignDriver = async (trackingId, driverEmail) => {
  const token = getAuthToken();
  if (!token) throw new Error('Authentication token not found. Please login.');

  const response = await fetch(`${API_BASE_URL}/assign-driver?trackingId=${trackingId}&driverEmail=${driverEmail}`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      // 'Content-Type': 'application/json', // Not strictly needed for POST with only query params
    },
  });
  const textResponse = await handleResponse(response); // Expects text response
  return { message: textResponse }; // Wrap in an object for consistency
};

/**
 * Updates the status of a delivery by Admin.
 * Backend: POST /api/admin/update-status (Requires Auth - Admin Role)
 * @param {string} trackingId - The tracking ID of the delivery.
 * @param {string} status - The new status for the delivery (e.g., from DeliveryStatus enum).
 * @returns {Promise<object>} An object with a message property (e.g. { message: "Success" })
 */
export const updateStatus = async (trackingId, status) => {
  const token = getAuthToken();
  if (!token) throw new Error('Authentication token not found. Please login.');

  const response = await fetch(`${API_BASE_URL}/update-status?trackingId=${trackingId}&status=${status}`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      // 'Content-Type': 'application/json',
    },
  });
  const textResponse = await handleResponse(response); // Expects text response
  return { message: textResponse }; // Wrap in an object for consistency
}; 