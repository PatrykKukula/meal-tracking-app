import React from 'react';
import { DietCalendar } from './DietCalendar';

export const DietsPage: React.FC = () => {
  // TODO: Fetch diet days from API for the displayed month
  // For now, using empty Map - will be populated when backend endpoint is available
  const dietDays = new Map();

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

      <DietCalendar dietDays={dietDays} />
    </div>
  );
};
