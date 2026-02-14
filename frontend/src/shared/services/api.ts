import axios from 'axios';
import { API_BASE_URL } from '@/config/constants';
import keycloak from '@/config/keycloak';
import toast from 'react-hot-toast';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add JWT token
api.interceptors.request.use(
  (config) => {
    if (keycloak.authenticated && keycloak.token) {
      config.headers.Authorization = `Bearer ${keycloak.token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // Handle 401 - Unauthorized (token expired)
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const refreshed = await keycloak.updateToken(5);
        if (refreshed && keycloak.token) {
          originalRequest.headers.Authorization = `Bearer ${keycloak.token}`;
          return api(originalRequest);
        }
      } catch (refreshError) {
        keycloak.login();
        return Promise.reject(refreshError);
      }
    }

    // Handle 403 - Forbidden
    if (error.response?.status === 403) {
      const message = error.response.data?.message || 'You do not have permission to perform this action';
      toast.error(message);
      
      // Redirect to home
      window.location.href = '/';
      return Promise.reject(error);
    }

    // Handle other errors
    if (error.response?.data?.message) {
      // Don't show toast for technical errors, let components handle them
      return Promise.reject(error.response.data);
    }

    return Promise.reject(error);
  }
);

export default api;
