import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Layout } from '@/shared/components/Layout';
import { HomePage } from '@/features/home/HomePage';
import { DashboardPage } from '@/features/dashboard/DashboardPage';
import { ProductsPage } from '@/features/products/ProductsPage';
import { AddProductPage } from '@/features/products/AddProductPage';
import { EditProductPage } from '@/features/products/EditProductPage';
import { LoginPage } from '@/features/auth/LoginPage';
import { APP_ROUTES } from '@/config/constants';

export const AppRouter: React.FC = () => {
  return (
    <BrowserRouter>
      <Layout>
        <Routes>
          <Route path={APP_ROUTES.HOME} element={<HomePage />} />
          <Route path={APP_ROUTES.DASHBOARD} element={<DashboardPage />} />
          <Route path={APP_ROUTES.PRODUCTS} element={<ProductsPage />} />
          <Route path={APP_ROUTES.PRODUCT_ADD} element={<AddProductPage />} />
          <Route path={APP_ROUTES.PRODUCT_EDIT} element={<EditProductPage />} />
          <Route path={APP_ROUTES.LOGIN} element={<LoginPage />} />
          <Route path="*" element={<Navigate to={APP_ROUTES.HOME} replace />} />
        </Routes>
      </Layout>
    </BrowserRouter>
  );
};
