// Diet-related types matching backend DTOs

export interface ProductQuantityDto {
  productId: number;
  quantity: number;
}

export interface MealDto {
  name?: string;
  quantities: ProductQuantityDto[];
}

export interface DietDayDto {
  dietDayId?: number;
  date: string; // ISO date format YYYY-MM-DD
  meals: MealDto[];
}

export interface DietDayResponseDto {
  dietDayId: number;
  ownerUsername: string;
  date: string;
  meals: MealResponseDto[];
}

export interface MealResponseDto {
  mealId: number;
  name?: string;
  products: ProductInMealDto[];
}

export interface ProductInMealDto {
  productId: number;
  productQuantityId: number;
  quantity: number;
  name: string;
  productCategory: string;
  calories: number;
  protein: number;
  carbs: number;
  fat: number;
}

export interface CalendarDay {
  date: Date;
  dateString: string; // YYYY-MM-DD
  dayOfMonth: number;
  isCurrentMonth: boolean;
  isToday: boolean;
  hasDiet: boolean;
  calories?: number;
  protein?: number;
  carbs?: number;
  fat?: number;
}
