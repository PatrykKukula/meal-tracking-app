import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import toast from 'react-hot-toast';
import { Button, Input, Loading, EmptyState } from '@/shared/components';
import { useAuthStore } from '@/shared/store/authStore';
import { dietApi } from '@/shared/services/dietApi';
import { MealDto, ProductQuantityDto } from '@/shared/types';
import { ProductSelectionDialog } from './ProductSelectionDialog';

export const ViewDietDayPage: React.FC = () => {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const { isAuthenticated } = useAuthStore();
  const { dietDayId } = useParams<{ dietDayId: string }>();

  const [isProductDialogOpen, setIsProductDialogOpen] = useState(false);
  const [currentMealId, setCurrentMealId] = useState<number | null>(null);
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);
  const [isAddingNewMeal, setIsAddingNewMeal] = useState(false); // Track if adding new meal
  const [showDeleteMealConfirm, setShowDeleteMealConfirm] = useState(false);
  const [mealToDelete, setMealToDelete] = useState<number | null>(null);
  const [showDeleteProductConfirm, setShowDeleteProductConfirm] = useState(false);
  const [productToDelete, setProductToDelete] = useState<number | null>(null);

  // Redirect to login if not authenticated
  useEffect(() => {
    if (!isAuthenticated) {
      toast.error('Please log in to view diet plans');
      navigate('/login');
    }
  }, [isAuthenticated, navigate]);

  // Fetch diet day
  const { data: dietDay, isLoading, error } = useQuery({
    queryKey: ['dietDay', dietDayId],
    queryFn: () => dietApi.getDietDayById(Number(dietDayId)),
    enabled: !!dietDayId,
  });

  // Delete diet day mutation
  const deleteDietDayMutation = useMutation({
    mutationFn: () => dietApi.deleteDietDay(Number(dietDayId)),
    onSuccess: () => {
      toast.success('Diet day deleted successfully!');
      queryClient.invalidateQueries({ queryKey: ['dietDays'] });
      navigate('/diets');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Failed to delete diet day');
    },
  });

  // Add meal mutation
  const addMealMutation = useMutation({
    mutationFn: (meal: MealDto) => dietApi.addMealToDietDay(Number(dietDayId), meal),
    onSuccess: () => {
      toast.success('Meal added successfully!');
      queryClient.invalidateQueries({ queryKey: ['dietDay', dietDayId] });
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Failed to add meal');
    },
  });

  // Delete meal mutation
  const deleteMealMutation = useMutation({
    mutationFn: (mealId: number) => dietApi.deleteMeal(mealId),
    onSuccess: () => {
      toast.success('Meal removed successfully!');
      queryClient.invalidateQueries({ queryKey: ['dietDay', dietDayId] });
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Failed to remove meal');
    },
  });

  // Add quantity mutation
  const addQuantityMutation = useMutation({
    mutationFn: ({ mealId, quantity }: { mealId: number; quantity: ProductQuantityDto }) =>
      dietApi.addQuantityToMeal(mealId, quantity),
    onSuccess: () => {
      toast.success('Product added to meal!');
      queryClient.invalidateQueries({ queryKey: ['dietDay', dietDayId] });
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Failed to add product');
    },
  });

  // Delete quantity mutation
  const deleteQuantityMutation = useMutation({
    mutationFn: (quantityId: number) => dietApi.deleteQuantity(quantityId),
    onSuccess: () => {
      // Silent update - no toast notification
      queryClient.invalidateQueries({ queryKey: ['dietDay', dietDayId] });
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Failed to remove product');
    },
  });

  // Update quantity mutation
  const updateQuantityMutation = useMutation({
    mutationFn: ({ quantityId, quantity }: { quantityId: number; quantity: number }) =>
      dietApi.updateQuantity(quantityId, quantity),
    onSuccess: () => {
      // Silent update - no toast notification
      queryClient.invalidateQueries({ queryKey: ['dietDay', dietDayId] });
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Failed to update quantity');
    },
  });

  const handleDeleteDietDay = () => {
    setShowDeleteConfirm(true);
  };

  const confirmDeleteDietDay = () => {
    deleteDietDayMutation.mutate();
    setShowDeleteConfirm(false);
  };

  const handleAddMeal = () => {
    // Open product dialog for new meal
    setIsAddingNewMeal(true);
    setCurrentMealId(null); // No meal ID yet
    setIsProductDialogOpen(true);
  };

  const handleDeleteMeal = (mealId: number, mealsCount: number) => {
    // Check if this is the only meal
    if (mealsCount === 1) {
      toast.error('Cannot remove the only meal from diet day. Please delete the entire diet day instead.');
      return;
    }
    setMealToDelete(mealId);
    setShowDeleteMealConfirm(true);
  };

  const confirmDeleteMeal = () => {
    if (mealToDelete !== null) {
      deleteMealMutation.mutate(mealToDelete);
    }
    setShowDeleteMealConfirm(false);
    setMealToDelete(null);
  };

  const handleOpenProductDialog = (mealId: number) => {
    setCurrentMealId(mealId);
    setIsProductDialogOpen(true);
  };

  const handleProductsSelected = (selectedProducts: Array<{ productId: number; name: string; quantity: number }>) => {
    if (isAddingNewMeal) {
      // Creating new meal with selected products
      const newMeal: MealDto = {
        name: '',
        quantities: selectedProducts.map(p => ({
          productId: p.productId,
          quantity: p.quantity,
        })),
      };
      addMealMutation.mutate(newMeal);
      setIsAddingNewMeal(false);
    } else if (currentMealId !== null) {
      // Adding products to existing meal
      selectedProducts.forEach((product) => {
        addQuantityMutation.mutate({
          mealId: currentMealId,
          quantity: {
            productId: product.productId,
            quantity: product.quantity,
          },
        });
      });
    }
    
    setIsProductDialogOpen(false);
    setCurrentMealId(null);
  };

  const handleDeleteQuantity = (quantityId: number) => {
    setProductToDelete(quantityId);
    setShowDeleteProductConfirm(true);
  };

  const confirmDeleteProduct = () => {
    if (productToDelete !== null) {
      deleteQuantityMutation.mutate(productToDelete);
    }
    setShowDeleteProductConfirm(false);
    setProductToDelete(null);
  };

  const handleUpdateQuantity = (quantityId: number, newQuantity: number) => {
    if (newQuantity <= 0) {
      toast.error('Quantity must be greater than 0');
      return;
    }
    // Silent update - no toast notification
    updateQuantityMutation.mutate({ quantityId, quantity: newQuantity });
  };

  if (isLoading) {
    return (
      <div className="flex justify-center items-center min-h-[400px]">
        <Loading size="lg" text="Loading diet day..." />
      </div>
    );
  }

  if (error || !dietDay) {
    return (
      <EmptyState
        title="Diet day not found"
        description="The diet day you're looking for doesn't exist or has been deleted."
        action={<Button onClick={() => navigate('/diets')}>Back to Calendar</Button>}
      />
    );
  }

  return (
    <div className="max-w-6xl mx-auto">
      {/* Header */}
      <div className="mb-8">
        <div className="flex items-center justify-between mb-4">
          <h1 className="text-3xl font-bold text-gray-900">Diet Day</h1>
          <div className="flex gap-3">
            <Button variant="outline" onClick={() => navigate('/diets')}>
              Back to Calendar
            </Button>
            <Button variant="danger" onClick={handleDeleteDietDay}>
              Delete Diet Day
            </Button>
          </div>
        </div>

        {/* Date Display */}
        <div className="bg-primary-50 border border-primary-200 rounded-lg p-4">
          <div className="text-sm font-medium text-primary-900 mb-1">Date</div>
          <div className="text-2xl font-bold text-primary-600">
            {new Date(dietDay.date + 'T00:00:00').toLocaleDateString('en-US', {
              weekday: 'long',
              year: 'numeric',
              month: 'long',
              day: 'numeric',
            })}
          </div>
          <div className="text-sm text-gray-600 mt-2">
            Owner: {dietDay.ownerUsername}
          </div>
        </div>
      </div>

      {/* Meals Grid */}
      <div className="space-y-6">
        {dietDay.meals.length === 0 ? (
          <div className="bg-gray-50 border-2 border-dashed border-gray-300 rounded-xl p-12 text-center">
            <p className="text-gray-600 mb-4">No meals in this diet day</p>
            <Button onClick={handleAddMeal}>Add First Meal</Button>
          </div>
        ) : (
          dietDay.meals.map((meal, index) => (
            <div
              key={meal.mealId}
              className="bg-white rounded-xl shadow-sm border border-gray-200 p-6"
            >
              {/* Meal Header */}
              <div className="flex items-center justify-between mb-4">
                <div className="flex items-center gap-4">
                  <div className="flex items-center justify-center w-10 h-10 bg-primary-100 text-primary-600 font-bold rounded-lg">
                    {index + 1}
                  </div>
                  <div>
                    <div className="font-semibold text-gray-900">
                      {meal.name || 'Unnamed Meal'}
                    </div>
                    <div className="text-sm text-gray-500">
                      Meal ID: {meal.mealId}
                    </div>
                  </div>
                </div>
                <Button
                  variant="danger"
                  size="sm"
                  onClick={() => handleDeleteMeal(meal.mealId, dietDay.meals.length)}
                >
                  Remove Meal
                </Button>
              </div>

              {/* Products List */}
              {meal.products.length === 0 ? (
                <div className="border-2 border-dashed border-gray-200 rounded-lg p-6 text-center">
                  <p className="text-gray-500 mb-3">No products in this meal</p>
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() => handleOpenProductDialog(meal.mealId)}
                  >
                    Add Products
                  </Button>
                </div>
              ) : (
                <div className="space-y-2">
                  {meal.products.map((product) => (
                    <div
                      key={product.productQuantityId}
                      className="flex items-center gap-4 p-3 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors"
                    >
                      <div className="flex-1">
                        <div className="font-medium text-gray-900">{product.name}</div>
                        <div className="text-sm text-gray-500">
                          {product.productCategory} • {product.calories} kcal •
                          P: {product.protein}g • C: {product.carbs}g • F: {product.fat}g
                        </div>
                      </div>
                      <div className="flex items-center gap-2">
                        <Input
                          type="number"
                          step="0.1"
                          min="0.1"
                          defaultValue={product.quantity}
                          onBlur={(e) => {
                            const newQuantity = parseFloat(e.target.value) || product.quantity;
                            // Only update if value actually changed
                            if (newQuantity !== product.quantity) {
                              handleUpdateQuantity(product.productQuantityId, newQuantity);
                            }
                          }}
                          onKeyDown={(e) => {
                            if (e.key === 'Enter') {
                              e.currentTarget.blur(); // Trigger onBlur
                            }
                          }}
                          className="w-24"
                        />
                        <span className="text-gray-600">× 100g</span>
                        <button
                          onClick={() => handleDeleteQuantity(product.productQuantityId)}
                          className="w-8 h-8 flex items-center justify-center bg-red-100 hover:bg-red-200 text-red-600 rounded-lg transition-colors"
                          title="Remove product"
                        >
                          <svg
                            className="w-4 h-4"
                            fill="none"
                            viewBox="0 0 24 24"
                            stroke="currentColor"
                          >
                            <path
                              strokeLinecap="round"
                              strokeLinejoin="round"
                              strokeWidth={2}
                              d="M6 18L18 6M6 6l12 12"
                            />
                          </svg>
                        </button>
                      </div>
                    </div>
                  ))}
                  <div className="pt-2">
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => handleOpenProductDialog(meal.mealId)}
                    >
                      Add More Products
                    </Button>
                  </div>
                </div>
              )}
            </div>
          ))
        )}

        {/* Add Meal Button */}
        {dietDay.meals.length > 0 && dietDay.meals.length < 10 && (
          <div className="flex justify-center">
            <Button variant="outline" onClick={handleAddMeal}>
              Add Another Meal
            </Button>
          </div>
        )}
      </div>

      {/* Delete Confirmation Dialog */}
      {showDeleteConfirm && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-xl shadow-xl max-w-md w-full p-6">
            <h3 className="text-xl font-bold text-gray-900 mb-4">
              Delete Diet Day?
            </h3>
            <p className="text-gray-600 mb-6">
              Do you really want to delete this Diet Day? This action cannot be undone.
            </p>
            <div className="flex gap-3 justify-end">
              <Button
                variant="outline"
                onClick={() => setShowDeleteConfirm(false)}
              >
                Cancel
              </Button>
              <Button
                variant="danger"
                onClick={confirmDeleteDietDay}
                disabled={deleteDietDayMutation.isPending}
              >
                {deleteDietDayMutation.isPending ? 'Deleting...' : 'Delete'}
              </Button>
            </div>
          </div>
        </div>
      )}

      {/* Delete Meal Confirmation Dialog */}
      {showDeleteMealConfirm && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-xl shadow-xl max-w-md w-full p-6">
            <h3 className="text-xl font-bold text-gray-900 mb-4">
              Remove Meal?
            </h3>
            <p className="text-gray-600 mb-6">
              Are you sure you want to remove this meal? All products in this meal will be removed.
            </p>
            <div className="flex gap-3 justify-end">
              <Button
                variant="outline"
                onClick={() => {
                  setShowDeleteMealConfirm(false);
                  setMealToDelete(null);
                }}
              >
                Cancel
              </Button>
              <Button
                variant="danger"
                onClick={confirmDeleteMeal}
                disabled={deleteMealMutation.isPending}
              >
                {deleteMealMutation.isPending ? 'Removing...' : 'Remove'}
              </Button>
            </div>
          </div>
        </div>
      )}

      {/* Delete Product Confirmation Dialog */}
      {showDeleteProductConfirm && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-xl shadow-xl max-w-md w-full p-6">
            <h3 className="text-xl font-bold text-gray-900 mb-4">
              Remove Product?
            </h3>
            <p className="text-gray-600 mb-6">
              Are you sure you want to remove this product from the meal?
            </p>
            <div className="flex gap-3 justify-end">
              <Button
                variant="outline"
                onClick={() => {
                  setShowDeleteProductConfirm(false);
                  setProductToDelete(null);
                }}
              >
                Cancel
              </Button>
              <Button
                variant="danger"
                onClick={confirmDeleteProduct}
                disabled={deleteQuantityMutation.isPending}
              >
                {deleteQuantityMutation.isPending ? 'Removing...' : 'Remove'}
              </Button>
            </div>
          </div>
        </div>
      )}

      {/* Product Selection Dialog */}
      {isProductDialogOpen && (
        <ProductSelectionDialog
          isOpen={isProductDialogOpen}
          onClose={() => {
            setIsProductDialogOpen(false);
            setCurrentMealId(null);
            setIsAddingNewMeal(false);
          }}
          onConfirm={handleProductsSelected}
        />
      )}
    </div>
  );
};
