import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import keycloak from '@/config/keycloak';
import { useAuthStore } from '@/shared/store/authStore';

export const LoginPage: React.FC = () => {
  const navigate = useNavigate();
  const { isAuthenticated } = useAuthStore();

  useEffect(() => {
    if (isAuthenticated) {
      navigate('/dashboard');
    } else {
      keycloak.login();
    }
  }, [isAuthenticated, navigate]);

  return (
    <div className="flex items-center justify-center min-h-[60vh]">
      <div className="text-center">
        <div className="w-16 h-16 border-4 border-primary-200 border-t-primary-600 rounded-full animate-spin mx-auto mb-4" />
        <p className="text-gray-600">Redirecting to login...</p>
      </div>
    </div>
  );
};
