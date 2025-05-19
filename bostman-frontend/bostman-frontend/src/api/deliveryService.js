import { getAuthToken } from '../utils/authUtils';

const API_BASE_URL = 'http://localhost:8080/api/deliveries'; // Default for Spring Boot

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
  // Check if response is JSON or text before parsing
  const contentType = response.headers.get("content-type");
  if (contentType && contentType.indexOf("application/json") !== -1) {
    return response.json();
  } else {
    return response.text(); 
  }
}

/**
 * Schedules a new delivery.
 * Backend: POST /api/deliveries (Requires Auth)
 * @param {object} deliveryRequestDTO - The delivery request data.
 * @returns {Promise<string>} The tracking ID of the new delivery.
 */
export const scheduleDelivery = async (deliveryRequestDTO) => {
  const token = getAuthToken();
  if (!token) throw new Error('Authentication token not found. Please login.');

  const response = await fetch(`${API_BASE_URL}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`,
    },
    body: JSON.stringify(deliveryRequestDTO),
  });
  return handleResponse(response); // Expects text response (trackingId)
};

/**
 * Retrieves deliveries for the authenticated user.
 * Backend: GET /api/deliveries/my-deliveries (Requires Auth)
 * @returns {Promise<Array<object>>} A list of delivery response DTOs.
 */
export const getMyDeliveries = async () => {
  const token = getAuthToken();
  if (!token) throw new Error('Authentication token not found. Please login.');

  const response = await fetch(`${API_BASE_URL}/my-deliveries`, { // Using the specific endpoint
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`,
    },
  });
  return handleResponse(response); // Expects JSON array
};

/**
 * Gets details of a specific delivery by its tracking ID.
 * Backend: GET /api/deliveries/{trackingId} (Potentially public, or requires Auth)
 * @param {string} trackingId - The tracking ID of the delivery.
 * @returns {Promise<object>} The delivery response DTO.
 */
export const getDeliveryByTrackingId = async (trackingId) => {
  // const token = getAuthToken(); // Uncomment if auth is required for this endpoint
  const response = await fetch(`${API_BASE_URL}/${trackingId}`, {
    method: 'GET',
    // headers: token ? { 'Authorization': `Bearer ${token}` } : {},
  });
  return handleResponse(response); // Expects JSON object
};

/**
 * Updates the status of a specific delivery (by user).
 * Backend: PATCH /api/deliveries/{trackingId}/status (Requires Auth)
 * @param {string} trackingId - The tracking ID of the delivery.
 * @param {object} statusUpdateDTO - The new status data (e.g., { newStatus: 'CANCELLED' }).
 * @returns {Promise<object>} The updated delivery response DTO.
 */
export const updateDeliveryStatus = async (trackingId, statusUpdateDTO) => {
  const token = getAuthToken();
  if (!token) throw new Error('Authentication token not found. Please login.');

  const response = await fetch(`${API_BASE_URL}/${trackingId}/status`, {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`,
    },
    body: JSON.stringify(statusUpdateDTO),
  });
  return handleResponse(response); // Expects JSON object
};

/**
 * Retrieves all deliveries (Admin only).
 * Backend: GET /api/deliveries/all (Requires Auth - Admin Role)
 * @returns {Promise<Array<object>>} A list of all delivery response DTOs.
 */
export const getAllDeliveries = async () => {
  const token = getAuthToken();
  if (!token) throw new Error('Authentication token not found. Please login.');

  const response = await fetch(`${API_BASE_URL}/all`, {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`,
    },
  });
  return handleResponse(response); // Expects JSON array
};

/**
 * Deletes a specific delivery by its tracking ID.
 * Backend: DELETE /api/deliveries/{trackingId} (Requires Auth - User must own the delivery or be Admin)
 * @param {string} trackingId - The tracking ID of the delivery to delete.
 * @returns {Promise<void>} Resolves if successful, throws error otherwise.
 */
export const deleteDelivery = async (trackingId) => {
  const token = getAuthToken();
  if (!token) throw new Error('Authentication token not found. Please login.');

  const response = await fetch(`${API_BASE_URL}/${trackingId}`, {
    method: 'DELETE',
    headers: {
      'Authorization': `Bearer ${token}`,
    },
  });
  // For DELETE, a 204 No Content is success and response.json() or response.text() would fail
  if (!response.ok) {
    let errorMessage = `API Error: ${response.status} ${response.statusText}`;
    try {
      // Try to get more specific error message from backend if available
      const errorBody = await response.json(); 
      errorMessage = errorBody.message || errorBody.error || errorMessage;
    } catch (e) {
      // Backend might not send a JSON body for all errors, or for 204 on success before this check
    }
    throw new Error(errorMessage);
  }
  // No content expected on successful DELETE (204)
  return; 
};

/**
 * Updates details of a specific delivery.
 * Backend: PUT /api/deliveries/{trackingId} (Requires Auth - User must own or be Admin)
 * @param {string} trackingId - The tracking ID of the delivery.
 * @param {object} deliveryUpdateDTO - The delivery data to update.
 * @returns {Promise<object>} The updated delivery response DTO.
 */
export const editDelivery = async (trackingId, deliveryUpdateDTO) => {
  const token = getAuthToken();
  if (!token) throw new Error('Authentication token not found. Please login.');

 const response = await fetch(`http://localhost:8080/api/deliveries/${trackingId}`, {
  method: 'PUT',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`,
  },
  body: JSON.stringify(deliveryUpdateDTO),
});

  return handleResponse(response); // Expects JSON object (updated delivery)
}; 


