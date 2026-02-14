# Quick Start Guide

## Installation Steps

1. **Copy all files to your frontend folder**
   - Copy the entire contents to your `/frontend` directory

2. **Install dependencies**
   ```bash
   cd frontend
   npm install
   ```

3. **Verify backend services are running**
   - Keycloak: http://localhost:7080
   - API Gateway: http://localhost:8080
   - Make sure the Keycloak realm "MealTrackingApp" is configured
   - Make sure the client "mealtrackingappclient" exists

4. **Start the development server**
   ```bash
   npm run dev
   ```

5. **Open browser**
   - Navigate to http://localhost:5173
   - The app should load with the home page

## What's Included

✅ Complete React 19 + TypeScript + Vite setup
✅ Keycloak authentication integration
✅ Products page with filtering and pagination
✅ Navigation header with role-based visibility
✅ API client with axios interceptors
✅ Zustand store for auth state
✅ React Query for server state
✅ Tailwind CSS styling
✅ Reusable UI components
✅ TypeScript types
✅ Error handling with toast notifications

## Project Structure

```
frontend/
├── src/
│   ├── app/                 # App initialization & routing
│   ├── config/              # Configuration (Keycloak, constants)
│   ├── features/            # Feature modules (products, auth, etc.)
│   └── shared/              # Shared components, services, types
├── index.html
├── package.json
├── vite.config.ts
├── tsconfig.json
└── tailwind.config.js
```

## Features Implemented

### 1. Navigation Header
- Dashboard, Products, Login/Logout buttons
- Buttons highlighted on hover
- Active route highlighting
- Role-based visibility

### 2. Products Page
- Table with: name, category, calories, protein, carbs, fat
- Clickable rows → navigate to `/products/{productId}`
- Row hover highlighting
- Pagination (50 products per page)
- Filters:
  - Category dropdown (all categories)
  - Product name text input
  - Filter button
  - Reset button
- Action buttons with role-based visibility:
  - Add Product (ADMIN, USER)
  - Edit Product (ADMIN for global, USER for own)
  - Delete Product (ADMIN for global, USER for own)
- "Products not found" message when empty

### 3. Authentication
- Keycloak integration with OAuth2/PKCE
- Automatic token refresh
- 401 → re-login
- 403 → redirect to home + notification
- Roles: ADMIN, USER

## Next Steps

The foundation is ready! You can now:

1. Add product detail page (`/products/{productId}`)
2. Add product form page (`/products/add`, `/products/{productId}/edit`)
3. Implement other features (diet, statistics, profile)
4. Customize styling and colors

## Troubleshooting

**Issue: Can't connect to backend**
- Verify API Gateway is running on port 8080
- Check CORS settings in API Gateway

**Issue: Authentication not working**
- Verify Keycloak is running on port 7080
- Check realm name: "MealTrackingApp"
- Check client ID: "mealtrackingappclient"
- Ensure PKCE is enabled in Keycloak client settings

**Issue: npm install fails**
- Use Node.js 18 or higher
- Clear npm cache: `npm cache clean --force`
- Delete node_modules and package-lock.json, then reinstall

## Design Notes

The design uses a modern, clean aesthetic with:
- Custom Outfit font (Google Fonts)
- Green primary color (#22c55e)
- White/gray backgrounds
- Smooth transitions and hover effects
- Responsive design
- Professional shadows and borders

All components are fully typed with TypeScript and follow React 19 best practices.
