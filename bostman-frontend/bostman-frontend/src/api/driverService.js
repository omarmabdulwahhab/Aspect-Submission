import { getAuthToken } from '../utils/authUtils';

const API_BASE_URL = 'http://localhost:8080/api/driver'; // Assuming Spring Boot runs on 8080

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
 * Retrieves deliveries assigned to the authenticated driver.
 * Backend: GET /api/driver/deliveries (Requires Auth - Driver Role)
 * @returns {Promise<Array<object>>} A list of delivery response DTOs for the driver.
 */
export const getAssignedDeliveries = async () => {
  const token = getAuthToken();
  if (!token) throw new Error('Authentication token not found. Please login.');

  const response = await fetch(`${API_BASE_URL}/deliveries`, {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`,
    },
  });
  return handleResponse(response); // Expects JSON array
};

/**
 * Marks a delivery as complete by the driver.
 * Backend: POST /api/driver/complete (Requires Auth - Driver Role)
 * @param {string} trackingId - The tracking ID of the delivery to mark as complete.
 * @returns {Promise<object>} An object with a message property (e.g. { message: "Success" })
 */
export const completeDelivery = async (trackingId) => {
  const token = getAuthToken();
  if (!token) throw new Error('Authentication token not found. Please login.');

  const response = await fetch(`${API_BASE_URL}/complete?trackingId=${trackingId}`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
    },
  });
  const textResponse = await handleResponse(response); // Expects text response
  return { message: textResponse }; // Wrap in an object for consistency
}; 