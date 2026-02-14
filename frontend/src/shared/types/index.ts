export enum ProductCategory {
  MEAT = 'MEAT',
  VEGETABLE = 'VEGETABLE',
  FRUIT = 'FRUIT',
  DAIRY = 'DAIRY',
  CEREAL = 'CEREAL',
  FISH = 'FISH',
  NUTS = 'NUTS',
  SWEET = 'SWEET',
  OTHER = 'OTHER',
}

export interface Product {
  productId?: number;
  name: string;
  productCategory: ProductCategory;
  calories: number;
  protein: number;
  carbs: number;
  fat: number;
  ownerUsername?: string | null;
}

export interface ProductFormData {
  name: string;
  productCategory: ProductCategory | '';
  calories: number | '';
  protein: number | '';
  carbs: number | '';
  fat: number | '';
}

export interface ProductFilters {
  pageNo: number;
  category?: ProductCategory | '';
  name?: string;
}

export interface ApiError {
  statusCode: number;
  statusMessage: string;
  message: string;
  path: string;
  occurrenceTime: string;
}

export interface PaginationInfo {
  currentPage: number;
  totalPages: number;
  hasMore: boolean;
}

export interface User {
  username: string;
  email?: string;
  roles: string[];
}
