import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { productApi } from '@/shared/services/productApi';
import { ProductCategory } from '@/shared/types';
import { Button, Input, Select } from '@/shared/components';
import { useAuthStore } from '@/shared/store/authStore';
import { ROLES } from '@/config/constants';
import { ProductFormData, validateField, validateProductForm } from '@/shared/utils/validation';
import toast from 'react-hot-toast';

const CATEGORY_OPTIONS = [
  { value: '', label: 'Select a category' },
  ...Object.values(ProductCategory).map(cat => ({ value: cat, label: cat }))
];

export const AddProductPage: React.FC = () => {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const { hasRole } = useAuthStore();
  
  const [formData, setFormData] = useState<ProductFormData>({
    name: '',
    productCategory: '',
    calories: '',
    protein: '',
    carbs: '',
    fat: '',
  });

  const [errors, setErrors] = useState<Record<string, string>>({});
  const [touched, setTouched] = useState<Record<string, boolean>>({});

  const isAdmin = hasRole(ROLES.ADMIN);

  const addMutation = useMutation({
    mutationFn: (data: any) => {
      return isAdmin ? productApi.addGlobalProduct(data) : productApi.addCustomProduct(data);
    },
    onSuccess: () => {
      toast.success(`${isAdmin ? 'Global' : 'Custom'} product added successfully!`);
      // Invalidate products query to trigger refetch
      queryClient.invalidateQueries({ queryKey: ['products'] });
      navigate('/products');
    },
    onError: (error: any) => {
      toast.error(error.message || 'Failed to add product');
    },
  });

  const handleChange = (field: keyof ProductFormData) => (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const value = e.target.value;
    setFormData(prev => ({ ...prev, [field]: value }));
    
    // Live validation
    const error = validateField(field, value);
    setErrors(prev => ({ ...prev, [field]: error }));
    setTouched(prev => ({ ...prev, [field]: true }));
  };

  const handleBlur = (field: keyof ProductFormData) => () => {
    setTouched(prev => ({ ...prev, [field]: true }));
    const error = validateField(field, formData[field]);
    setErrors(prev => ({ ...prev, [field]: error }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    // Validate all fields
    const validationErrors = validateProductForm(formData);
    setErrors(validationErrors);

    // Mark all fields as touched
    const allTouched = Object.keys(formData).reduce((acc, key) => {
      acc[key] = true;
      return acc;
    }, {} as Record<string, boolean>);
    setTouched(allTouched);

    // If there are errors, don't submit
    if (Object.keys(validationErrors).length > 0) {
      toast.error('Please fix validation errors before submitting');
      return;
    }

    // Convert string numbers to integers
    const productData = {
      name: formData.name,
      productCategory: formData.productCategory as ProductCategory,
      calories: parseInt(formData.calories),
      protein: parseInt(formData.protein),
      carbs: parseInt(formData.carbs),
      fat: parseInt(formData.fat),
    };

    addMutation.mutate(productData);
  };

  return (
    <div className="animate-fade-in max-w-2xl mx-auto">
      <div className="mb-8">
        <h1 className="text-4xl font-bold text-gray-900 mb-2">
          Add {isAdmin ? 'Global' : 'Custom'} Product
        </h1>
        <p className="text-gray-600">
          {isAdmin 
            ? 'Add a global product available to all users' 
            : 'Add a custom product for your personal use'}
        </p>
      </div>

      <div className="bg-white rounded-2xl shadow-md p-8 border border-gray-100">
        <form onSubmit={handleSubmit}>
          <div className="space-y-6">
            {/* Product Name */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Product Name *
              </label>
              <input
                type="text"
                value={formData.name}
                onChange={handleChange('name')}
                onBlur={handleBlur('name')}
                className={`
                  w-full px-4 py-2 border rounded-lg
                  focus:ring-2 focus:ring-primary-500 focus:border-transparent
                  ${touched.name && errors.name ? 'border-red-500' : 'border-gray-300'}
                `}
                placeholder="e.g., Salmon"
              />
              {touched.name && errors.name && (
                <p className="mt-1 text-xs text-red-600">{errors.name}</p>
              )}
            </div>

            {/* Product Category */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Category *
              </label>
              <select
                value={formData.productCategory}
                onChange={handleChange('productCategory')}
                onBlur={handleBlur('productCategory')}
                className={`
                  w-full px-4 py-2 border rounded-lg
                  focus:ring-2 focus:ring-primary-500 focus:border-transparent
                  ${touched.productCategory && errors.productCategory ? 'border-red-500' : 'border-gray-300'}
                `}
              >
                {CATEGORY_OPTIONS.map(option => (
                  <option key={option.value} value={option.value}>
                    {option.label}
                  </option>
                ))}
              </select>
              {touched.productCategory && errors.productCategory && (
                <p className="mt-1 text-xs text-red-600">{errors.productCategory}</p>
              )}
            </div>

            {/* Calories */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Calories *
              </label>
              <input
                type="number"
                value={formData.calories}
                onChange={handleChange('calories')}
                onBlur={handleBlur('calories')}
                min="0"
                step="1"
                className={`
                  w-full px-4 py-2 border rounded-lg
                  focus:ring-2 focus:ring-primary-500 focus:border-transparent
                  ${touched.calories && errors.calories ? 'border-red-500' : 'border-gray-300'}
                `}
                placeholder="e.g., 670"
              />
              {touched.calories && errors.calories && (
                <p className="mt-1 text-xs text-red-600">{errors.calories}</p>
              )}
            </div>

            {/* Protein */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Protein (g) *
              </label>
              <input
                type="number"
                value={formData.protein}
                onChange={handleChange('protein')}
                onBlur={handleBlur('protein')}
                min="0"
                step="1"
                className={`
                  w-full px-4 py-2 border rounded-lg
                  focus:ring-2 focus:ring-primary-500 focus:border-transparent
                  ${touched.protein && errors.protein ? 'border-red-500' : 'border-gray-300'}
                `}
                placeholder="e.g., 21"
              />
              {touched.protein && errors.protein && (
                <p className="mt-1 text-xs text-red-600">{errors.protein}</p>
              )}
            </div>

            {/* Carbs */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Carbs (g) *
              </label>
              <input
                type="number"
                value={formData.carbs}
                onChange={handleChange('carbs')}
                onBlur={handleBlur('carbs')}
                min="0"
                step="1"
                className={`
                  w-full px-4 py-2 border rounded-lg
                  focus:ring-2 focus:ring-primary-500 focus:border-transparent
                  ${touched.carbs && errors.carbs ? 'border-red-500' : 'border-gray-300'}
                `}
                placeholder="e.g., 0"
              />
              {touched.carbs && errors.carbs && (
                <p className="mt-1 text-xs text-red-600">{errors.carbs}</p>
              )}
            </div>

            {/* Fat */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Fat (g) *
              </label>
              <input
                type="number"
                value={formData.fat}
                onChange={handleChange('fat')}
                onBlur={handleBlur('fat')}
                min="0"
                step="1"
                className={`
                  w-full px-4 py-2 border rounded-lg
                  focus:ring-2 focus:ring-primary-500 focus:border-transparent
                  ${touched.fat && errors.fat ? 'border-red-500' : 'border-gray-300'}
                `}
                placeholder="e.g., 67"
              />
              {touched.fat && errors.fat && (
                <p className="mt-1 text-xs text-red-600">{errors.fat}</p>
              )}
            </div>
          </div>

          {/* Actions */}
          <div className="flex gap-4 mt-8 pt-6 border-t border-gray-200">
            <Button
              type="submit"
              disabled={addMutation.isPending}
              className="flex-1"
            >
              {addMutation.isPending ? 'Adding...' : 'Add Product'}
            </Button>
            <Button
              type="button"
              variant="outline"
              onClick={() => navigate('/products')}
              className="flex-1"
            >
              Cancel
            </Button>
          </div>
        </form>
      </div>
    </div>
  );
};
