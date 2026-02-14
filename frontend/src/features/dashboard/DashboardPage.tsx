import React from 'react';
import { useAuthStore } from '@/shared/store/authStore';

export const DashboardPage: React.FC = () => {
  const { user, isAuthenticated } = useAuthStore();

  return (
    <div className="animate-fade-in">
      <div className="bg-white rounded-2xl shadow-md p-8 border border-gray-100">
        <h1 className="text-4xl font-bold text-gray-900 mb-4">Dashboard</h1>
        {isAuthenticated ? (
          <div>
            <p className="text-gray-600 mb-4">Welcome back, {user?.username}!</p>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mt-8">
              <div className="bg-gradient-to-br from-primary-500 to-primary-700 rounded-xl p-6 text-white shadow-lg">
                <h3 className="text-lg font-semibold mb-2">Products</h3>
                <p className="text-3xl font-bold">-</p>
                <p className="text-sm opacity-90 mt-2">Total products</p>
              </div>
              <div className="bg-gradient-to-br from-blue-500 to-blue-700 rounded-xl p-6 text-white shadow-lg">
                <h3 className="text-lg font-semibold mb-2">Meals</h3>
                <p className="text-3xl font-bold">-</p>
                <p className="text-sm opacity-90 mt-2">Tracked meals</p>
              </div>
              <div className="bg-gradient-to-br from-purple-500 to-purple-700 rounded-xl p-6 text-white shadow-lg">
                <h3 className="text-lg font-semibold mb-2">Calories</h3>
                <p className="text-3xl font-bold">-</p>
                <p className="text-sm opacity-90 mt-2">Average daily</p>
              </div>
            </div>
          </div>
        ) : (
          <p className="text-gray-600">Please log in to view your dashboard.</p>
        )}
      </div>
    </div>
  );
};
