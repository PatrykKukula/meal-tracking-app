import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useAuthStore } from '@/shared/store/authStore';
import { APP_ROUTES } from '@/config/constants';

export const Navigation: React.FC = () => {
  const location = useLocation();
  const { isAuthenticated, logout } = useAuthStore();

  const isActive = (path: string) => {
    if (path === APP_ROUTES.HOME) {
      return location.pathname === path;
    }
    return location.pathname.startsWith(path);
  };

  const navLinkClass = (path: string) => `
    px-6 py-3 text-sm font-medium rounded-lg transition-all duration-200
    ${isActive(path)
      ? 'bg-primary-600 text-white shadow-md'
      : 'text-gray-700 hover:bg-primary-50 hover:text-primary-700'
    }
  `;

  return (
    <nav className="bg-white border-b border-gray-200 sticky top-0 z-50 shadow-sm">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          {/* Logo */}
          <Link to={APP_ROUTES.HOME} className="flex items-center gap-3">
            <div className="w-10 h-10 bg-gradient-to-br from-primary-500 to-primary-700 rounded-xl flex items-center justify-center shadow-md">
              <span className="text-white text-xl font-bold">M</span>
            </div>
            <span className="text-xl font-bold bg-gradient-to-r from-primary-600 to-primary-800 bg-clip-text text-transparent">
              Meal Tracker
            </span>
          </Link>

          {/* Navigation Links */}
          <div className="flex items-center gap-2">
            <Link to={APP_ROUTES.DASHBOARD} className={navLinkClass(APP_ROUTES.DASHBOARD)}>
              Dashboard
            </Link>
            <Link to={APP_ROUTES.PRODUCTS} className={navLinkClass(APP_ROUTES.PRODUCTS)}>
              Products
            </Link>

            {/* Auth Button */}
            {isAuthenticated ? (
              <button
                onClick={logout}
                className="ml-4 px-6 py-3 text-sm font-medium text-red-600 hover:bg-red-50 rounded-lg transition-all duration-200"
              >
                Logout
              </button>
            ) : (
              <Link
                to={APP_ROUTES.LOGIN}
                className="ml-4 px-6 py-3 text-sm font-medium text-white bg-primary-600 hover:bg-primary-700 rounded-lg shadow-md hover:shadow-lg transition-all duration-200"
              >
                Login
              </Link>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
};
