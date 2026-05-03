import api from './api';
import { DietDayDto, DietDayResponseDto, MealDto, ProductQuantityDto } from '@/shared/types';

// CRITICAL: Gateway routes diets through /diet/api/**
const DIET_BASE = '/diet/api/diets';

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

  // Delete diet day
  deleteDietDay: async (dietDayId: number): Promise<void> => {
    await api.delete(`${DIET_BASE}/${dietDayId}`);
  },

  // Add meal to diet day
  addMealToDietDay: async (dietDayId: number, meal: MealDto): Promise<MealDto> => {
    const response = await api.post(`${DIET_BASE}/${dietDayId}/add_meal`, meal);
    return response.data;
  },

  // Delete meal from diet day
  deleteMeal: async (mealId: number): Promise<void> => {
    await api.delete(`${DIET_BASE}/meal/${mealId}`);
  },

  // Add product quantity to meal
  addQuantityToMeal: async (mealId: number, quantity: ProductQuantityDto): Promise<ProductQuantityDto> => {
    const response = await api.post(`${DIET_BASE}/meal/${mealId}/add_quantity`, quantity);
    return response.data;
  },

  // Delete product quantity from meal
  deleteQuantity: async (quantityId: number): Promise<void> => {
    await api.delete(`${DIET_BASE}/quantity/${quantityId}`);
  },

  // Update product quantity
  updateQuantity: async (quantityId: number, quantity: number): Promise<ProductQuantityDto> => {
    const response = await api.patch(`${DIET_BASE}/quantity/${quantityId}`, { quantity });
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
