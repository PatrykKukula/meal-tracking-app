import { ProductCategory } from '../types';

export interface ValidationError {
  field: string;
  message: string;
}

export interface ProductFormData {
  name: string;
  productCategory: ProductCategory | '';
  calories: string;
  protein: string;
  carbs: string;
  fat: string;
}

export const validateProductForm = (data: ProductFormData): Record<string, string> => {
  const errors: Record<string, string> = {};

  // Name validation
  if (!data.name || data.name.trim() === '') {
    errors.name = 'Product name cannot be empty';
  } else if (data.name.length > 64) {
    errors.name = 'Product name cannot exceed 64 characters';
  }

  // Product category validation
  if (!data.productCategory || data.productCategory === '') {
    errors.productCategory = 'Product category is required';
  }

  // Calories validation
  if (data.calories === '') {
    errors.calories = 'Calories is required';
  } else {
    const caloriesNum = Number(data.calories);
    if (isNaN(caloriesNum)) {
      errors.calories = 'Calories must be a number';
    } else if (caloriesNum < 0) {
      errors.calories = 'Calories cannot be less than 0';
    }
  }

  // Protein validation
  if (data.protein === '') {
    errors.protein = 'Protein is required';
  } else {
    const proteinNum = Number(data.protein);
    if (isNaN(proteinNum)) {
      errors.protein = 'Protein must be a number';
    } else if (proteinNum < 0) {
      errors.protein = 'Protein cannot be less than 0';
    }
  }

  // Carbs validation
  if (data.carbs === '') {
    errors.carbs = 'Carbs is required';
  } else {
    const carbsNum = Number(data.carbs);
    if (isNaN(carbsNum)) {
      errors.carbs = 'Carbs must be a number';
    } else if (carbsNum < 0) {
      errors.carbs = 'Carbs cannot be less than 0';
    }
  }

  // Fat validation
  if (data.fat === '') {
    errors.fat = 'Fat is required';
  } else {
    const fatNum = Number(data.fat);
    if (isNaN(fatNum)) {
      errors.fat = 'Fat must be a number';
    } else if (fatNum < 0) {
      errors.fat = 'Fat cannot be less than 0';
    }
  }

  return errors;
};

export const validateField = (field: string, value: string | number): string => {
  switch (field) {
    case 'name':
      if (!value || String(value).trim() === '') {
        return 'Product name cannot be empty';
      }
      if (String(value).length > 64) {
        return 'Product name cannot exceed 64 characters';
      }
      return '';

    case 'productCategory':
      if (!value || value === '') {
        return 'Product category is required';
      }
      return '';

    case 'calories':
    case 'protein':
    case 'carbs':
    case 'fat':
      if (value === '' || value === null || value === undefined) {
        return `${field.charAt(0).toUpperCase() + field.slice(1)} is required`;
      }
      const num = Number(value);
      if (isNaN(num)) {
        return `${field.charAt(0).toUpperCase() + field.slice(1)} must be a number`;
      }
      if (num < 0) {
        return `${field.charAt(0).toUpperCase() + field.slice(1)} cannot be less than 0`;
      }
      return '';

    default:
      return '';
  }
};
