import React from 'react';
import { Link } from 'react-router-dom';
import { useAuthStore } from '@/shared/store/authStore';
import { Button } from '@/shared/components';

export const HomePage: React.FC = () => {
  const { isAuthenticated } = useAuthStore();

  return (
    <div className="animate-fade-in">
      <div className="text-center max-w-4xl mx-auto py-20">
        <div className="mb-8">
          <div className="w-24 h-24 bg-gradient-to-br from-primary-500 to-primary-700 rounded-3xl flex items-center justify-center shadow-2xl mx-auto mb-6">
            <span className="text-white text-5xl font-bold">M</span>
          </div>
          <h1 className="text-6xl font-bold text-gray-900 mb-4">
            Welcome to{' '}
            <span className="bg-gradient-to-r from-primary-600 to-primary-800 bg-clip-text text-transparent">
              Meal Tracker
            </span>
          </h1>
          <p className="text-xl text-gray-600 mb-8">
            Track your meals, monitor nutrition, and achieve your health goals
          </p>
        </div>

        <div className="flex flex-col sm:flex-row gap-4 justify-center">
          {isAuthenticated ? (
            <>
              <Link to="/dashboard">
                <Button size="lg" className="w-full sm:w-auto">
                  Go to Dashboard
                </Button>
              </Link>
              <Link to="/products">
                <Button size="lg" variant="outline" className="w-full sm:w-auto">
                  Browse Products
                </Button>
              </Link>
            </>
          ) : (
            <Link to="/login">
              <Button size="lg" className="w-full sm:w-auto">
                Get Started
              </Button>
            </Link>
          )}
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mt-20">
          <div className="bg-white rounded-2xl p-8 shadow-lg border border-gray-100 hover:shadow-xl transition-shadow duration-300">
            <div className="w-12 h-12 bg-primary-100 rounded-xl flex items-center justify-center mx-auto mb-4">
              <span className="text-2xl">📊</span>
            </div>
            <h3 className="text-xl font-semibold text-gray-900 mb-2">Track Nutrition</h3>
            <p className="text-gray-600">
              Monitor calories, protein, carbs, and fats for all your meals
            </p>
          </div>

          <div className="bg-white rounded-2xl p-8 shadow-lg border border-gray-100 hover:shadow-xl transition-shadow duration-300">
            <div className="w-12 h-12 bg-primary-100 rounded-xl flex items-center justify-center mx-auto mb-4">
              <span className="text-2xl">🥗</span>
            </div>
            <h3 className="text-xl font-semibold text-gray-900 mb-2">Product Database</h3>
            <p className="text-gray-600">
              Access a comprehensive database of food products and nutrition info
            </p>
          </div>

          <div className="bg-white rounded-2xl p-8 shadow-lg border border-gray-100 hover:shadow-xl transition-shadow duration-300">
            <div className="w-12 h-12 bg-primary-100 rounded-xl flex items-center justify-center mx-auto mb-4">
              <span className="text-2xl">🎯</span>
            </div>
            <h3 className="text-xl font-semibold text-gray-900 mb-2">Reach Goals</h3>
            <p className="text-gray-600">
              Set and achieve your health and fitness goals with detailed insights
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};
