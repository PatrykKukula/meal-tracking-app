import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { productApi } from '@/shared/services/productApi';
import { ProductCategory } from '@/shared/types';
import { Button, Loading, EmptyState } from '@/shared/components';
import { useAuthStore } from '@/shared/store/authStore';
import { ROLES } from '@/config/constants';
import { ProductFormData, validateField, validateProductForm } from '@/shared/utils/validation';
import toast from 'react-hot-toast';

const CATEGORY_OPTIONS = [
  { value: '', label: 'Select a category' },
  ...Object.values(ProductCategory).map(cat => ({ value: cat, label: cat }))
];

export const EditProductPage: React.FC = () => {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const { productId } = useParams<{ productId: string }>();
  const { hasRole, user } = useAuthStore();
  
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

  // Fetch existing product data
  const { data: product, isLoading, error } = useQuery({
    queryKey: ['product', productId],
    queryFn: () => productApi.getProductById(productId!),
    enabled: !!productId,
  });

  // Pre-fill form with existing data
  useEffect(() => {
    if (product) {
      setFormData({
        name: product.name,
        productCategory: product.productCategory,
        calories: product.calories.toString(),
        protein: product.protein.toString(),
        carbs: product.carbs.toString(),
        fat: product.fat.toString(),
      });
    }
  }, [product]);

  const updateMutation = useMutation({
    mutationFn: (data: any) => productApi.updateProduct(productId!, data),
    onSuccess: () => {
      toast.success('Product updated successfully!');
      // Invalidate products query to trigger refetch
      queryClient.invalidateQueries({ queryKey: ['products'] });
      navigate('/products');
    },
    onError: (error: any) => {
      toast.error(error.message || 'Failed to update product');
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

    updateMutation.mutate(productData);
  };

  // Check permissions
  const canEdit = () => {
    if (!product) return false;
    const isAdmin = hasRole(ROLES.ADMIN);
    
    // Global products: ownerUsername is null/undefined
    const isGlobalProduct = product.ownerUsername === null || product.ownerUsername === undefined;
    
    // Custom products: ownerUsername matches current user
    const isOwnProduct = product.ownerUsername && product.ownerUsername === user?.username;
    
    // ADMIN can edit global products
    if (isAdmin && isGlobalProduct) {
      return true;
    }
    
    // Users can edit their own custom products
    if (isOwnProduct) {
      return true;
    }
    
    return false;
  };

  if (isLoading) {
    return <Loading size="lg" text="Loading product..." />;
  }

  if (error || !product) {
    return (
      <EmptyState
        title="Product not found"
        description="The product you're looking for doesn't exist or has been deleted."
        action={<Button onClick={() => navigate('/products')}>Back to Products</Button>}
      />
    );
  }

  if (!canEdit()) {
    return (
      <EmptyState
        title="Permission denied"
        description="You don't have permission to edit this product."
        action={<Button onClick={() => navigate('/products')}>Back to Products</Button>}
      />
    );
  }

  return (
    <div className="animate-fade-in max-w-2xl mx-auto">
      <div className="mb-8">
        <h1 className="text-4xl font-bold text-gray-900 mb-2">Edit Product</h1>
        <p className="text-gray-600">Update product information</p>
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
              disabled={updateMutation.isPending}
              className="flex-1"
            >
              {updateMutation.isPending ? 'Updating...' : 'Update Product'}
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
