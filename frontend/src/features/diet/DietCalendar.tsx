import React, { useState, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { CalendarDay } from '@/shared/types';
import { Select } from '@/shared/components';

const DAYS_OF_WEEK = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
const MONTHS = [
  'January', 'February', 'March', 'April', 'May', 'June',
  'July', 'August', 'September', 'October', 'November', 'December'
];

interface DietCalendarProps {
  dietDays?: Map<string, { calories: number; protein: number; carbs: number; fat: number }>;
}

const formatDateToISO = (date: Date): string => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
};

export const DietCalendar: React.FC<DietCalendarProps> = ({ dietDays = new Map() }) => {
  const navigate = useNavigate();
  const today = new Date();
  
  const [selectedYear, setSelectedYear] = useState(today.getFullYear());
  const [selectedMonth, setSelectedMonth] = useState(today.getMonth());

  // Generate year options (current year ± 5 years)
  const yearOptions = useMemo(() => {
    const currentYear = today.getFullYear();
    const years = [];
    for (let i = currentYear - 5; i <= currentYear + 5; i++) {
      years.push({ value: i.toString(), label: i.toString() });
    }
    return years;
  }, [today]);

  // Generate month options
  const monthOptions = MONTHS.map((month, index) => ({
    value: index.toString(),
    label: month
  }));


  // Generate calendar days for selected month/year
  const calendarDays = useMemo((): CalendarDay[] => {
    const firstDay = new Date(selectedYear, selectedMonth, 1);
    const lastDay = new Date(selectedYear, selectedMonth + 1, 0);
    
    // Get the day of week (0 = Sunday, need to convert to Monday = 0)
    let firstDayOfWeek = firstDay.getDay();
    firstDayOfWeek = firstDayOfWeek === 0 ? 6 : firstDayOfWeek - 1; // Convert Sunday from 0 to 6
    
    const daysInMonth = lastDay.getDate();
    const days: CalendarDay[] = [];

    // Add previous month's days to fill first week
    const prevMonthLastDay = new Date(selectedYear, selectedMonth, 0).getDate();
    for (let i = firstDayOfWeek - 1; i >= 0; i--) {
      const date = new Date(selectedYear, selectedMonth - 1, prevMonthLastDay - i);
      const dateString = formatDateToISO(date);
      days.push({
        date,
        dateString,
        dayOfMonth: prevMonthLastDay - i,
        isCurrentMonth: false,
        isToday: false,
        hasDiet: dietDays.has(dateString),
        ...dietDays.get(dateString)
      });
    }

    // Add current month's days
    for (let day = 1; day <= daysInMonth; day++) {
      const date = new Date(selectedYear, selectedMonth, day);
      const dateString = formatDateToISO(date);
      const isToday = 
        date.getDate() === today.getDate() &&
        date.getMonth() === today.getMonth() &&
        date.getFullYear() === today.getFullYear();

      days.push({
        date,
        dateString,
        dayOfMonth: day,
        isCurrentMonth: true,
        isToday,
        hasDiet: dietDays.has(dateString),
        ...dietDays.get(dateString)
      });
    }

    // Add next month's days to complete the grid
    const remainingDays = 42 - days.length; // 6 rows × 7 days
    for (let day = 1; day <= remainingDays; day++) {
      const date = new Date(selectedYear, selectedMonth + 1, day);
      const dateString = formatDateToISO(date);
      days.push({
        date,
        dateString,
        dayOfMonth: day,
        isCurrentMonth: false,
        isToday: false,
        hasDiet: dietDays.has(dateString),
        ...dietDays.get(dateString)
      });
    }

    return days;
  }, [selectedYear, selectedMonth, dietDays, today]);


  const handleDayClick = (day: CalendarDay) => {
    // Navigate to add diet day page with the selected date
    navigate(`/diets/add?date=${day.dateString}`);
  };

  return (
    <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
      {/* Header with month/year selectors */}
      <div className="mb-6 flex items-center justify-between">
        <h2 className="text-2xl font-bold text-gray-900">
          Diet Calendar
        </h2>
        <div className="flex gap-3">
          <Select
            value={selectedMonth.toString()}
            onChange={(e) => setSelectedMonth(Number(e.target.value))}
            options={monthOptions}
            className="w-36"
          />
          <Select
            value={selectedYear.toString()}
            onChange={(e) => setSelectedYear(Number(e.target.value))}
            options={yearOptions}
            className="w-28"
          />
        </div>
      </div>

      {/* Calendar Grid */}
      <div className="grid grid-cols-7 gap-2">
        {/* Day names header */}
        {DAYS_OF_WEEK.map((day) => (
          <div
            key={day}
            className="text-center text-sm font-semibold text-gray-600 py-2"
          >
            {day}
          </div>
        ))}

        {/* Calendar days */}
        {calendarDays.map((day, index) => (
          <button
            key={index}
            onClick={() => handleDayClick(day)}
            className={`
              relative aspect-square p-2 rounded-lg border transition-all duration-200
              ${day.isCurrentMonth 
                ? 'bg-white hover:bg-primary-50 border-gray-200 hover:border-primary-300' 
                : 'bg-gray-50 border-gray-100 text-gray-400'
              }
              ${day.isToday 
                ? 'ring-2 ring-primary-500 border-primary-500' 
                : ''
              }
              ${day.hasDiet 
                ? 'bg-primary-50/50 border-primary-200' 
                : ''
              }
              hover:shadow-md active:scale-95
            `}
          >
            {/* Day number */}
            <div className={`
              text-sm font-semibold mb-1
              ${day.isToday ? 'text-primary-600' : ''}
              ${day.isCurrentMonth ? 'text-gray-900' : 'text-gray-400'}
            `}>
              {day.dayOfMonth}
            </div>

            {/* Nutrition info (if diet exists) */}
            {day.hasDiet && day.calories !== undefined && (
              <div className="text-xs space-y-0.5">
                <div className="font-medium text-primary-600 truncate">
                  {day.calories} kcal
                </div>
                <div className="text-gray-500 text-[10px] truncate">
                  P: {day.protein}g
                </div>
                <div className="text-gray-500 text-[10px] truncate">
                  C: {day.carbs}g | F: {day.fat}g
                </div>
              </div>
            )}

            {/* Today indicator */}
            {day.isToday && (
              <div className="absolute top-1 right-1">
                <div className="w-2 h-2 bg-primary-500 rounded-full"></div>
              </div>
            )}
          </button>
        ))}
      </div>

      {/* Legend */}
      <div className="mt-6 flex items-center justify-center gap-6 text-sm text-gray-600">
        <div className="flex items-center gap-2">
          <div className="w-3 h-3 rounded-full bg-primary-500"></div>
          <span>Today</span>
        </div>
        <div className="flex items-center gap-2">
          <div className="w-3 h-3 rounded border-2 border-primary-200 bg-primary-50/50"></div>
          <span>Has diet plan</span>
        </div>
      </div>
    </div>
  );
};
