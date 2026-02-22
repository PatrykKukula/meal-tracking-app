import api from './api';
import { DietDayDto, DietDayResponseDto } from '@/shared/types';

// CRITICAL: Gateway routes diets through /diets/api/**
const DIET_BASE = '/diets/api/diets';

export const dietApi = {
  // Create new diet day
  createDietDay: async (dietDay: DietDayDto): Promise<DietDayResponseDto> => {
    const response = await api.post(DIET_BASE, dietDay);
    return response.data;
  },

  // Get diet day by ID
  getDietDayById: async (dietDayId: number): Promise<DietDayResponseDto> => {
    const response = await api.get(`${DIET_BASE}/${dietDayId}`);
    return response.data;
  },

  // Get diet days for a date range (if needed for calendar)
  getDietDaysByMonth: async (year: number, month: number): Promise<DietDayResponseDto[]> => {
    // This endpoint might need to be added to backend later
    // For now, we'll fetch individual days as needed
    const response = await api.get(`${DIET_BASE}`, {
      params: { year, month }
    });
    return response.data;
  },
};
