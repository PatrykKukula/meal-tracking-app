import React, { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { productApi } from '@/shared/services/productApi';
import { Product, ProductCategory, ProductFilters } from '@/shared/types';
import { Button, Input, Select, Loading } from '@/shared/components';

interface ProductSelectionDialogProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: (selectedProducts: Array<{ productId: number; name: string; quantity: number }>) => void;
}

const CATEGORY_OPTIONS = [
  { value: '', label: 'All Categories' },
  ...Object.values(ProductCategory).map(cat => ({ value: cat, label: cat }))
];

export const ProductSelectionDialog: React.FC<ProductSelectionDialogProps> = ({
  isOpen,
  onClose,
  onConfirm,
}) => {
  const [filters, setFilters] = useState<ProductFilters>({
    pageNo: 0,
    category: '',
    name: '',
  });

  const [searchName, setSearchName] = useState('');
  const [selectedProducts, setSelectedProducts] = useState<
    Map<number, { product: Product; quantity: number }>
  >(new Map());

  const { data: products = [], isLoading } = useQuery({
    queryKey: ['products', filters],
    queryFn: () => productApi.getProducts(filters),
  });

  const handleSearch = () => {
    setFilters({ ...filters, name: searchName, pageNo: 0 });
  };

  const handleCategoryChange = (category: string) => {
    setFilters({ ...filters, category: category as ProductCategory | '', pageNo: 0 });
  };

  const toggleProductSelection = (product: Product) => {
    const newSelected = new Map(selectedProducts);
    if (newSelected.has(product.productId!)) {
      newSelected.delete(product.productId!);
    } else {
      newSelected.set(product.productId!, { product, quantity: 1.0 });
    }
    setSelectedProducts(newSelected);
  };

  const updateQuantity = (productId: number, quantity: number) => {
    const newSelected = new Map(selectedProducts);
    const item = newSelected.get(productId);
    if (item) {
      item.quantity = quantity;
      setSelectedProducts(newSelected);
    }
  };

  const handleConfirm = () => {
    const productsToAdd = Array.from(selectedProducts.values()).map(({ product, quantity }) => ({
      productId: product.productId!,
      name: product.name,
      quantity,
    }));
    onConfirm(productsToAdd);
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-xl shadow-xl max-w-4xl w-full max-h-[90vh] flex flex-col">
        {/* Header */}
        <div className="p-6 border-b border-gray-200">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-2xl font-bold text-gray-900">Select Products</h2>
            <button
              onClick={onClose}
              className="text-gray-400 hover:text-gray-600 transition-colors"
            >
              <svg className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>

          {/* Filters */}
          <div className="flex gap-3">
            <div className="flex-1">
              <Input
                placeholder="Search by product name..."
                value={searchName}
                onChange={(e) => setSearchName(e.target.value)}
                onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
              />
            </div>
            <Select
              value={filters.category || ''}
              onChange={(e) => handleCategoryChange(e.target.value)}
              options={CATEGORY_OPTIONS}
              className="w-48"
            />
            <Button onClick={handleSearch}>Search</Button>
          </div>
        </div>

        {/* Product List */}
        <div className="flex-1 overflow-y-auto p-6">
          {isLoading ? (
            <div className="flex justify-center py-12">
              <Loading size="lg" />
            </div>
          ) : products.length === 0 ? (
            <div className="text-center py-12 text-gray-500">
              No products found. Try different filters.
            </div>
          ) : (
            <div className="space-y-2">
              {products.map((product) => {
                const isSelected = selectedProducts.has(product.productId!);
                const selectedItem = selectedProducts.get(product.productId!);

                return (
                  <div
                    key={product.productId}
                    className={`
                      p-4 border rounded-lg transition-all cursor-pointer
                      ${isSelected 
                        ? 'border-primary-300 bg-primary-50' 
                        : 'border-gray-200 hover:border-primary-200 hover:bg-gray-50'
                      }
                    `}
                    onClick={() => !isSelected && toggleProductSelection(product)}
                  >
                    <div className="flex items-center justify-between">
                      <div className="flex-1">
                        <div className="flex items-center gap-3">
                          <input
                            type="checkbox"
                            checked={isSelected}
                            onChange={() => toggleProductSelection(product)}
                            className="w-5 h-5 text-primary-600 rounded focus:ring-primary-500"
                            onClick={(e) => e.stopPropagation()}
                          />
                          <div>
                            <div className="font-medium text-gray-900">{product.name}</div>
                            <div className="text-sm text-gray-500">
                              {product.productCategory} • {product.calories} kcal
                            </div>
                          </div>
                        </div>
                      </div>

                      {isSelected && (
                        <div className="flex items-center gap-2" onClick={(e) => e.stopPropagation()}>
                          <Input
                            type="number"
                            step="0.1"
                            min="0.1"
                            value={selectedItem?.quantity || 1}
                            onChange={(e) =>
                              updateQuantity(product.productId!, parseFloat(e.target.value) || 1)
                            }
                            className="w-24"
                          />
                          <span className="text-gray-600">× 100g</span>
                        </div>
                      )}
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>

        {/* Footer */}
        <div className="p-6 border-t border-gray-200">
          <div className="flex items-center justify-between">
            <div className="text-sm text-gray-600">
              {selectedProducts.size} product{selectedProducts.size !== 1 ? 's' : ''} selected
            </div>
            <div className="flex gap-3">
              <Button variant="outline" onClick={onClose}>
                Cancel
              </Button>
              <Button
                onClick={handleConfirm}
                disabled={selectedProducts.size === 0}
              >
                Add Selected Products
              </Button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
