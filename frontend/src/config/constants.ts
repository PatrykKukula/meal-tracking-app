// CRITICAL: API Gateway base URL WITHOUT /api suffix
// Gateway routes are /api/products/**, /api/profile/**, etc.
export const API_BASE_URL = 'http://localhost:8080';

export const KEYCLOAK_CONFIG = {
  url: 'http://localhost:7080',
  realm: 'MealTrackingApp',
  clientId: 'mealtrackingappclient',
};

export const APP_ROUTES = {
  HOME: '/',
  DASHBOARD: '/dashboard',
  PRODUCTS: '/products',
  PRODUCT_DETAIL: '/products/:productId',
  PRODUCT_ADD: '/products/add',
  PRODUCT_EDIT: '/products/:productId/edit',
  LOGIN: '/login',
} as const;

export const PAGINATION = {
  PRODUCTS_PER_PAGE: 50,
} as const;

export const ROLES = {
  ADMIN: 'ADMIN',
  USER: 'USER',
} as const;
