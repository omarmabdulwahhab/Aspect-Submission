// TODO: Replace with your actual API base URL
const API_BASE_URL = 'http://localhost:8080/api/auth'; // Assuming Spring Boot runs on 8080 by default

async function handleResponse(response) {
  if (!response.ok) {
    let errorMessage = `API Error: ${response.status} ${response.statusText}`;
    try {
      // Backend might return plain text error for auth, or JSON
      const textError = await response.text();
      try {
        const jsonError = JSON.parse(textError);
        errorMessage = jsonError.message || jsonError.error || textError;
      } catch (e) {
        errorMessage = textError || errorMessage;
      }
    } catch (e) {
      // Ignore if response body is not readable
    }
    throw new Error(errorMessage);
  }
  // Auth endpoints return plain text (token or message)
  return response.text(); 
}

/**
 * Registers a new user.
 * Backend: POST /api/auth/register
 * @param {object} userData - The user registration data (e.g., { name, email, password, role })
 * @returns {Promise<string>} The response message from the server.
 */
export const register = async (userData) => {
  const response = await fetch(`${API_BASE_URL}/register`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(userData),
  });
  return handleResponse(response); // Expects text response
};

/**
 * Logs in an existing user.
 * Backend: POST /api/auth/login
 * @param {object} credentials - The user login credentials (e.g., { email, password })
 * @returns {Promise<string>} The JWT token string from the server.
 */
export const login = async (credentials) => {
  const response = await fetch(`${API_BASE_URL}/login`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(credentials),
  });
  return handleResponse(response); // Expects text response (token)
}; 