import api from './api';
import { Product, ProductFilters } from '@/shared/types';

// CRITICAL: Gateway routes products through /product/api/** (note: product singular)
// NOT /api/products (that was wrong!)
const PRODUCT_BASE = '/product/api/products';

export const productApi = {
  // Get products with filters
  getProducts: async (filters: ProductFilters): Promise<Product[]> => {
    const params = new URLSearchParams();
    params.append('pageNo', filters.pageNo.toString());
    
    if (filters.category) {
      params.append('category', filters.category);
    }
    
    if (filters.name) {
      params.append('name', filters.name);
    }

    const response = await api.get(`${PRODUCT_BASE}?${params.toString()}`);
    return response.data;
  },

  // Get single product by ID
  getProductById: async (productId: string): Promise<Product> => {
    const response = await api.get(`${PRODUCT_BASE}/${productId}`);
    return response.data;
  },

  // Add global product (ADMIN only)
  addGlobalProduct: async (product: Omit<Product, 'id'>): Promise<Product> => {
    const response = await api.post(PRODUCT_BASE, product);
    return response.data;
  },

  // Add custom product (USER/ADMIN)
  addCustomProduct: async (product: Omit<Product, 'id'>): Promise<Product> => {
    const response = await api.post(`${PRODUCT_BASE}/custom`, product);
    return response.data;
  },

  // Update product
  updateProduct: async (productId: string, product: Omit<Product, 'id'>): Promise<Product> => {
    const response = await api.put(`${PRODUCT_BASE}/${productId}`, product);
    return response.data;
  },

  // Delete product
  deleteProduct: async (productId: string): Promise<void> => {
    await api.delete(`${PRODUCT_BASE}/${productId}`);
  },
};
