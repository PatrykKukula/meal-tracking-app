import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { useAuthStore } from '@/shared/store/authStore';
import { dietApi } from '@/shared/services/dietApi';
import { Button } from '@/shared/components';
import { DietCalendar } from './DietCalendar';

export const DietsPage: React.FC = () => {
  const navigate = useNavigate();
  const { isAuthenticated } = useAuthStore();
  const today = new Date();
  const [selectedYear, setSelectedYear] = useState(today.getFullYear());
  const [selectedMonth, setSelectedMonth] = useState(today.getMonth() + 1);

  // Fetch diet days for the selected month
  const { data: dietDaysResponse = [] } = useQuery({
    queryKey: ['dietDays', selectedYear, selectedMonth],
    queryFn: () => dietApi.getDietDaysByMonth(selectedYear, selectedMonth),
    enabled: isAuthenticated,
  });

  // Convert diet days array to Map for calendar
  const dietDays = new Map(
    dietDaysResponse.map((dietDay) => [
      dietDay.date,
      {
        dietDayId: dietDay.dietDayId,
        calories: dietDay.meals.reduce(
          (sum, meal) =>
            sum +
            meal.products.reduce((mealSum, product) => mealSum + product.calories * product.quantity, 0),
          0
        ),
        protein: dietDay.meals.reduce(
          (sum, meal) =>
            sum +
            meal.products.reduce((mealSum, product) => mealSum + product.protein * product.quantity, 0),
          0
        ),
        carbs: dietDay.meals.reduce(
          (sum, meal) =>
            sum +
            meal.products.reduce((mealSum, product) => mealSum + product.carbs * product.quantity, 0),
          0
        ),
        fat: dietDay.meals.reduce(
          (sum, meal) =>
            sum + meal.products.reduce((mealSum, product) => mealSum + product.fat * product.quantity, 0),
          0
        ),
      },
    ])
  );

  // Show login prompt for unauthenticated users
  if (!isAuthenticated) {
    return (
      <div className="max-w-4xl mx-auto">
        <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-12 text-center">
          <div className="mb-6">
            <div className="w-20 h-20 bg-primary-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <svg
                className="w-10 h-10 text-primary-600"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"
                />
              </svg>
            </div>
            <h2 className="text-2xl font-bold text-gray-900 mb-2">
              Authentication Required
            </h2>
            <p className="text-gray-600 mb-6">
              Please log in or register to access diet planning features and make full use of the application functionality.
            </p>
          </div>
          <div className="flex gap-3 justify-center">
            <Button onClick={() => navigate('/login')}>
              Log In
            </Button>
            <Button variant="outline" onClick={() => navigate('/')}>
              Go to Home
            </Button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-2">
          My Diets
        </h1>
        <p className="text-gray-600">
          Click on any day to add or view your diet plan
        </p>
      </div>

      <DietCalendar 
        dietDays={dietDays}
        onMonthChange={(year, month) => {
          setSelectedYear(year);
          setSelectedMonth(month);
        }}
      />
    </div>
  );
};
