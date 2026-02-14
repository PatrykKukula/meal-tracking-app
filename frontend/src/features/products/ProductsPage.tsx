import React, { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { productApi } from '@/shared/services/productApi';
import { ProductCategory, ProductFilters } from '@/shared/types';
import { Button, Select, Input, Loading, EmptyState } from '@/shared/components';
import { useAuthStore } from '@/shared/store/authStore';
import { PAGINATION, ROLES } from '@/config/constants';
import toast from 'react-hot-toast';

const CATEGORY_OPTIONS = [
  { value: '', label: 'All Categories' },
  ...Object.values(ProductCategory).map(cat => ({ value: cat, label: cat }))
];

export const ProductsPage: React.FC = () => {
  const navigate = useNavigate();
  const { hasRole, user, isAuthenticated } = useAuthStore();
  
  const [filters, setFilters] = useState<ProductFilters>({
    pageNo: 0,
    category: '',
    name: '',
  });

  const [searchName, setSearchName] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('');

  const { data: products = [], isLoading, error, refetch } = useQuery({
    queryKey: ['products', filters],
    queryFn: () => productApi.getProducts(filters),
  });

  const handleFilter = () => {
    setFilters({
      pageNo: 0,
      category: selectedCategory as ProductCategory | '',
      name: searchName,
    });
  };

  const handleReset = () => {
    setSearchName('');
    setSelectedCategory('');
    setFilters({
      pageNo: 0,
      category: '',
      name: '',
    });
  };

  const handleProductClick = (productId: string) => {
    navigate(`/products/${productId}`);
  };

  const handleDelete = async (productId: string, product: any) => {
    // Check permissions
    const isAdmin = hasRole(ROLES.ADMIN);
    const isOwner = product.ownerUsername === user?.username;
    
    if (!isAdmin && !isOwner) {
      toast.error('You do not have permission to delete this product');
      return;
    }

    if (!window.confirm('Are you sure you want to delete this product?')) {
      return;
    }

    try {
      await productApi.deleteProduct(productId);
      toast.success('Product deleted successfully');
      refetch();
    } catch (err: any) {
      toast.error(err.message || 'Failed to delete product');
    }
  };

  const canEditProduct = (product: any) => {
    if (!isAuthenticated) return false;
    if (!product.productId) return false;
    
    const isAdmin = hasRole(ROLES.ADMIN);
    
    // Global products have ownerUsername = null
    const isGlobalProduct = product.ownerUsername === null || product.ownerUsername === undefined;
    
    // Custom products have ownerUsername set
    const isOwnProduct = product.ownerUsername && product.ownerUsername === user?.username;
    
    // ADMIN can edit global products (ownerUsername is null)
    if (isAdmin && isGlobalProduct) {
      return true;
    }
    
    // Users can edit their own custom products (ownerUsername matches)
    if (isOwnProduct) {
      return true;
    }
    
    return false;
  };

  const canDeleteProduct = (product: any) => {
    if (!isAuthenticated) return false;
    if (!product.productId) return false;
    
    const isAdmin = hasRole(ROLES.ADMIN);
    
    // Global products have ownerUsername = null
    const isGlobalProduct = product.ownerUsername === null || product.ownerUsername === undefined;
    
    // Custom products have ownerUsername set
    const isOwnProduct = product.ownerUsername && product.ownerUsername === user?.username;
    
    // ADMIN can delete global products (ownerUsername is null)
    if (isAdmin && isGlobalProduct) {
      return true;
    }
    
    // Users can delete their own custom products (ownerUsername matches)
    if (isOwnProduct) {
      return true;
    }
    
    return false;
  };

  const handlePrevPage = () => {
    setFilters(prev => ({ ...prev, pageNo: Math.max(0, prev.pageNo - 1) }));
  };

  const handleNextPage = () => {
    if (products.length === PAGINATION.PRODUCTS_PER_PAGE) {
      setFilters(prev => ({ ...prev, pageNo: prev.pageNo + 1 }));
    }
  };

  if (isLoading) {
    return (
      <div className="animate-fade-in">
        <Loading size="lg" text="Loading products..." />
      </div>
    );
  }

  if (error) {
    return (
      <div className="animate-fade-in">
        <EmptyState
          title="Error loading products"
          description="There was an error loading the products. Please try again."
          action={<Button onClick={() => refetch()}>Retry</Button>}
        />
      </div>
    );
  }

  return (
    <div className="animate-fade-in">
      {/* Header */}
      <div className="mb-8">
        <h1 className="text-4xl font-bold text-gray-900 mb-2">Products</h1>
        <p className="text-gray-600">Browse and manage your meal tracking products</p>
      </div>

      {/* Filters */}
      <div className="bg-white rounded-2xl shadow-md p-6 mb-6 border border-gray-100">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div className="md:col-span-2">
            <Input
              type="text"
              placeholder="Search by product name..."
              value={searchName}
              onChange={(e) => setSearchName(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && handleFilter()}
            />
          </div>
          
          <Select
            options={CATEGORY_OPTIONS}
            value={selectedCategory}
            onChange={(e) => setSelectedCategory(e.target.value)}
          />
          
          <div className="flex gap-2">
            <Button onClick={handleFilter} className="flex-1">
              Filter
            </Button>
            <Button onClick={handleReset} variant="outline">
              Reset
            </Button>
          </div>
        </div>

        {/* Add Product Button - visible for authenticated users */}
        {(hasRole(ROLES.ADMIN) || hasRole(ROLES.USER)) && (
          <div className="mt-4 pt-4 border-t border-gray-200">
            <Button
              onClick={() => navigate('/products/add')}
              variant="primary"
            >
              + Add Product
            </Button>
          </div>
        )}
      </div>

      {/* Products Table */}
      {products.length === 0 ? (
        <EmptyState
          title="No products found"
          description="Try adjusting your filters or add a new product"
        />
      ) : (
        <div className="bg-white rounded-2xl shadow-md overflow-hidden border border-gray-100">
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-gradient-to-r from-primary-600 to-primary-700 text-white">
                <tr>
                  <th className="px-6 py-4 text-left text-sm font-semibold">Name</th>
                  <th className="px-6 py-4 text-left text-sm font-semibold">Category</th>
                  <th className="px-6 py-4 text-right text-sm font-semibold">Calories</th>
                  <th className="px-6 py-4 text-right text-sm font-semibold">Protein (g)</th>
                  <th className="px-6 py-4 text-right text-sm font-semibold">Carbs (g)</th>
                  <th className="px-6 py-4 text-right text-sm font-semibold">Fat (g)</th>
                  <th className="px-6 py-4 text-center text-sm font-semibold">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200">
                {products.map((product: any, index: number) => (
                  <tr
                    key={product.productId || index}
                    onClick={() => product.productId && handleProductClick(product.productId)}
                    className="hover:bg-primary-50/50 cursor-pointer transition-colors duration-150"
                  >
                    <td className="px-6 py-4 text-sm font-medium text-gray-900">
                      {product.name}
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-600">
                      <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-primary-100 text-primary-800">
                        {product.productCategory}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-sm text-right text-gray-900">
                      {product.calories}
                    </td>
                    <td className="px-6 py-4 text-sm text-right text-gray-900">
                      {product.protein}
                    </td>
                    <td className="px-6 py-4 text-sm text-right text-gray-900">
                      {product.carbs}
                    </td>
                    <td className="px-6 py-4 text-sm text-right text-gray-900">
                      {product.fat}
                    </td>
                    <td className="px-6 py-4 text-sm text-center">
                      <div className="flex items-center justify-center gap-2" onClick={(e) => e.stopPropagation()}>
                        {canEditProduct(product) && (
                          <Button
                            size="sm"
                            variant="outline"
                            onClick={() => navigate(`/products/${product.productId}/edit`)}
                          >
                            Edit
                          </Button>
                        )}
                        {canDeleteProduct(product) && (
                          <Button
                            size="sm"
                            variant="danger"
                            onClick={() => handleDelete(product.productId, product)}
                          >
                            Delete
                          </Button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {/* Pagination */}
          <div className="bg-gray-50 px-6 py-4 flex items-center justify-between border-t border-gray-200">
            <div className="text-sm text-gray-600">
              Page {filters.pageNo + 1}
            </div>
            <div className="flex gap-2">
              <Button
                onClick={handlePrevPage}
                disabled={filters.pageNo === 0}
                variant="outline"
                size="sm"
              >
                Previous
              </Button>
              <Button
                onClick={handleNextPage}
                disabled={products.length < PAGINATION.PRODUCTS_PER_PAGE}
                variant="outline"
                size="sm"
              >
                Next
              </Button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
