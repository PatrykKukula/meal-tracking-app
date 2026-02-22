import React, { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import toast from 'react-hot-toast';
import { Button, Input } from '@/shared/components';
import { dietApi } from '@/shared/services/dietApi';
import { DietDayDto, MealDto, ProductQuantityDto } from '@/shared/types';
import { ProductSelectionDialog } from './ProductSelectionDialog';

interface MealFormData {
  id: string; // Temporary ID for React keys
  name: string;
  quantities: Array<ProductQuantityDto & { _tempId: string; productName?: string }>;
}

export const AddDietDayPage: React.FC = () => {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [searchParams] = useSearchParams();
  const dateParam = searchParams.get('date');

  const [selectedDate, setSelectedDate] = useState<string>(
    dateParam || new Date().toISOString().split('T')[0]
  );
  const [meals, setMeals] = useState<MealFormData[]>([]);
  const [isProductDialogOpen, setIsProductDialogOpen] = useState(false);
  const [currentMealIndex, setCurrentMealIndex] = useState<number | null>(null);

  const createDietMutation = useMutation({
    mutationFn: (dietDay: DietDayDto) => dietApi.createDietDay(dietDay),
    onSuccess: () => {
      toast.success('Diet day added successfully!');
      queryClient.invalidateQueries({ queryKey: ['dietDays'] });
      navigate('/diets');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Failed to create diet day');
    },
  });

  const addMeal = () => {
    if (meals.length >= 10) {
      toast.error('Maximum 10 meals per day');
      return;
    }

    setMeals([
      ...meals,
      {
        id: `meal-${Date.now()}`,
        name: '',
        quantities: [],
      },
    ]);
  };

  const removeMeal = (index: number) => {
    setMeals(meals.filter((_, i) => i !== index));
  };

  const updateMealName = (index: number, name: string) => {
    const updatedMeals = [...meals];
    updatedMeals[index].name = name;
    setMeals(updatedMeals);
  };

  const openProductDialog = (mealIndex: number) => {
    setCurrentMealIndex(mealIndex);
    setIsProductDialogOpen(true);
  };

  const handleProductsSelected = (selectedProducts: Array<{ productId: number; name: string; quantity: number }>) => {
    if (currentMealIndex === null) return;

    const updatedMeals = [...meals];
    const meal = updatedMeals[currentMealIndex];

    // Add selected products
    selectedProducts.forEach((product) => {
      if (meal.quantities.length >= 50) {
        toast.error('Maximum 50 products per meal');
        return;
      }

      meal.quantities.push({
        productId: product.productId,
        quantity: product.quantity,
        _tempId: `product-${Date.now()}-${Math.random()}`,
        productName: product.name,
      });
    });

    setMeals(updatedMeals);
    setIsProductDialogOpen(false);
    setCurrentMealIndex(null);
  };

  const removeProduct = (mealIndex: number, productTempId: string) => {
    const updatedMeals = [...meals];
    updatedMeals[mealIndex].quantities = updatedMeals[mealIndex].quantities.filter(
      (q) => q._tempId !== productTempId
    );
    setMeals(updatedMeals);
  };

  const updateProductQuantity = (mealIndex: number, productTempId: string, quantity: number) => {
    const updatedMeals = [...meals];
    const product = updatedMeals[mealIndex].quantities.find((q) => q._tempId === productTempId);
    if (product) {
      product.quantity = quantity;
      setMeals(updatedMeals);
    }
  };

  const handleSubmit = () => {
    // Validation
    if (meals.length === 0) {
      toast.error('Add at least one meal');
      return;
    }

    for (const meal of meals) {
      if (meal.quantities.length === 0) {
        toast.error('Each meal must have at least one product');
        return;
      }
    }

    // Convert to DTO format
    const dietDay: DietDayDto = {
      date: selectedDate,
      meals: meals.map((meal) => ({
        name: meal.name || undefined,
        quantities: meal.quantities.map((q) => ({
          productId: q.productId,
          quantity: q.quantity,
        })),
      })),
    };

    createDietMutation.mutate(dietDay);
  };

  const handleCancel = () => {
    navigate('/diets');
  };

  return (
    <div className="max-w-6xl mx-auto">
      {/* Header */}
      <div className="mb-8">
        <div className="flex items-center justify-between mb-4">
          <h1 className="text-3xl font-bold text-gray-900">Add Diet Day</h1>
          <div className="flex gap-3">
            <Button variant="outline" onClick={handleCancel}>
              Cancel
            </Button>
            <Button
              onClick={handleSubmit}
              disabled={createDietMutation.isPending}
            >
              {createDietMutation.isPending ? 'Saving...' : 'Save Diet Day'}
            </Button>
          </div>
        </div>

        {/* Date Display */}
        <div className="bg-primary-50 border border-primary-200 rounded-lg p-4">
          <div className="text-sm font-medium text-primary-900 mb-1">Selected Date</div>
          <div className="text-2xl font-bold text-primary-600">
            {new Date(selectedDate + 'T00:00:00').toLocaleDateString('en-US', {
              weekday: 'long',
              year: 'numeric',
              month: 'long',
              day: 'numeric',
            })}
          </div>
        </div>
      </div>

      {/* Meals Grid */}
      <div className="space-y-6">
        {meals.length === 0 ? (
          <div className="bg-gray-50 border-2 border-dashed border-gray-300 rounded-xl p-12 text-center">
            <div className="text-gray-400 mb-4">
              <svg
                className="mx-auto h-12 w-12"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M12 6v6m0 0v6m0-6h6m-6 0H6"
                />
              </svg>
            </div>
            <p className="text-gray-600 mb-4">No meals added yet</p>
            <Button onClick={addMeal}>Add First Meal</Button>
          </div>
        ) : (
          meals.map((meal, mealIndex) => (
            <div
              key={meal.id}
              className="bg-white rounded-xl shadow-sm border border-gray-200 p-6"
            >
              {/* Meal Header */}
              <div className="flex items-center justify-between mb-4">
                <div className="flex items-center gap-4 flex-1">
                  <div className="flex items-center justify-center w-10 h-10 bg-primary-100 text-primary-600 font-bold rounded-lg">
                    {mealIndex + 1}
                  </div>
                  <Input
                    placeholder="Meal name (optional, e.g., Breakfast)"
                    value={meal.name}
                    onChange={(e) => updateMealName(mealIndex, e.target.value)}
                    className="flex-1 max-w-xs"
                  />
                </div>
                <Button
                  variant="danger"
                  size="sm"
                  onClick={() => removeMeal(mealIndex)}
                >
                  Remove Meal
                </Button>
              </div>

              {/* Products List */}
              {meal.quantities.length === 0 ? (
                <div className="border-2 border-dashed border-gray-200 rounded-lg p-6 text-center">
                  <p className="text-gray-500 mb-3">No products added to this meal</p>
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() => openProductDialog(mealIndex)}
                  >
                    Add Products
                  </Button>
                </div>
              ) : (
                <div className="space-y-2">
                  {meal.quantities.map((product) => (
                    <div
                      key={product._tempId}
                      className="flex items-center gap-4 p-3 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors"
                    >
                      <div className="flex-1">
                        <div className="font-medium text-gray-900">
                          {product.productName || `Product ID: ${product.productId}`}
                        </div>
                      </div>
                      <div className="flex items-center gap-2">
                        <Input
                          type="number"
                          step="0.1"
                          min="0.1"
                          value={product.quantity}
                          onChange={(e) =>
                            updateProductQuantity(
                              mealIndex,
                              product._tempId,
                              parseFloat(e.target.value) || 0
                            )
                          }
                          className="w-24"
                        />
                        <span className="text-gray-600">× 100g</span>
                        <Button
                          variant="danger"
                          size="sm"
                          onClick={() => removeProduct(mealIndex, product._tempId)}
                        >
                          Remove
                        </Button>
                      </div>
                    </div>
                  ))}
                  <div className="pt-2">
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => openProductDialog(mealIndex)}
                      disabled={meal.quantities.length >= 50}
                    >
                      Add More Products {meal.quantities.length >= 50 && '(Max reached)'}
                    </Button>
                  </div>
                </div>
              )}
            </div>
          ))
        )}

        {/* Add Meal Button */}
        {meals.length > 0 && meals.length < 10 && (
          <div className="flex justify-center">
            <Button variant="outline" onClick={addMeal}>
              Add Another Meal
            </Button>
          </div>
        )}
      </div>

      {/* Product Selection Dialog */}
      {isProductDialogOpen && currentMealIndex !== null && (
        <ProductSelectionDialog
          isOpen={isProductDialogOpen}
          onClose={() => {
            setIsProductDialogOpen(false);
            setCurrentMealIndex(null);
          }}
          onConfirm={handleProductsSelected}
        />
      )}
    </div>
  );
};
