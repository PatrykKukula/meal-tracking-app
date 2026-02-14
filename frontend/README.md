# Meal Tracking App - Frontend

Modern React 19 + TypeScript frontend for the Meal Tracking microservices application.

## Tech Stack

- **React 19** - UI framework
- **TypeScript** - Type safety
- **Vite** - Build tool and dev server
- **Tailwind CSS** - Styling
- **React Query v5** - Server state management
- **Zustand** - Client state management
- **React Router v6** - Routing
- **Axios** - HTTP client
- **Keycloak-js** - Authentication & Authorization
- **React Hot Toast** - Notifications
- **Zod** - Schema validation

## Prerequisites

- Node.js 18+ and npm
- Keycloak server running on http://localhost:7080
- API Gateway running on http://localhost:8080

## Installation

1. Install dependencies:
```bash
npm install
```

2. Start the development server:
```bash
npm run dev
```

The app will be available at http://localhost:5173

## Available Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run preview` - Preview production build
- `npm run lint` - Run ESLint

## Project Structure

```
src/
├── app/                    # App setup and routing
│   ├── App.tsx            # Main app component with Keycloak init
│   └── AppRouter.tsx      # Route definitions
├── features/              # Feature modules
│   ├── auth/              # Authentication
│   ├── dashboard/         # Dashboard page
│   ├── home/              # Home page
│   └── products/          # Products module
├── shared/                # Shared resources
│   ├── components/        # Reusable UI components
│   ├── services/          # API clients
│   ├── store/             # Zustand stores
│   ├── types/             # TypeScript types
│   └── utils/             # Utility functions
└── config/                # Configuration files
    ├── constants.ts       # App constants
    └── keycloak.ts        # Keycloak configuration
```

## Key Features

### Authentication
- Keycloak integration with OAuth2/OIDC
- Authorization Code + PKCE flow
- Automatic token refresh
- Role-based access control (ADMIN, USER)

### Products Page
- Paginated product list (50 per page)
- Filter by category and name
- Clickable rows navigate to product details
- Role-based action buttons (Add, Edit, Delete)
- Responsive table design

### API Communication
- Single entry point through API Gateway (http://localhost:8080)
- Axios interceptors for token management
- Automatic 401 handling with token refresh
- 403 handling with redirect and notification

### Error Handling
- React Query error boundaries
- Toast notifications for user-facing errors
- Structured error responses from backend

## Configuration

### Keycloak Settings
Located in `src/config/keycloak.ts`:
- URL: http://localhost:7080
- Realm: MealTrackingApp
- Client ID: mealtrackingappclient

### API Gateway
Located in `src/config/constants.ts`:
- Base URL: http://localhost:8080/api

## Authentication Flow

1. App initializes with `check-sso` (silent authentication)
2. If not authenticated, user can click Login
3. Redirects to Keycloak login page
4. On success, token stored in memory (not localStorage)
5. Token included in all API requests via axios interceptor
6. Automatic token refresh before expiration

## Role-Based Visibility

### Products Page Actions:
- **Add Product**: Visible to ADMIN and USER roles
- **Edit Product**: 
  - ADMIN: Can edit global products
  - USER: Can edit own custom products
- **Delete Product**:
  - ADMIN: Can delete global products
  - USER: Can delete own custom products

## API Endpoints Used

- `GET /api/products` - Get products with filters
- `GET /api/products/{productId}` - Get single product
- `POST /api/products` - Add global product (ADMIN)
- `POST /api/products/custom` - Add custom product (USER/ADMIN)
- `PUT /api/products/{productId}` - Update product
- `DELETE /api/products/{productId}` - Delete product

## Styling

The app uses a modern, clean design with:
- Green/white color scheme
- Tailwind CSS utility classes
- Custom CSS variables for theming
- Smooth transitions and animations
- Responsive design
- Hover effects on interactive elements

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## Notes

- Frontend never connects directly to microservices
- All requests go through API Gateway
- CORS is handled by API Gateway
- Rate limiting is enforced at Gateway level
- Token validation happens at Gateway and microservice level
