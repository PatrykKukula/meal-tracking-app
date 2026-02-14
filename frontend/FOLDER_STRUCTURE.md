# 📁 Complete Folder Structure

## Root Directory (frontend/)
```
frontend/
├── .eslintrc.cjs              # ESLint configuration
├── .gitignore                 # Git ignore file
├── index.html                 # HTML entry point
├── package.json               # NPM dependencies and scripts
├── postcss.config.js          # PostCSS configuration for Tailwind
├── README.md                  # Detailed documentation
├── QUICK_START.md             # Quick installation guide
├── tailwind.config.js         # Tailwind CSS configuration
├── tsconfig.json              # TypeScript configuration
├── tsconfig.node.json         # TypeScript config for Node tools
├── vite.config.ts             # Vite bundler configuration
└── src/                       # Source code directory
```

## src/ Directory
```
src/
├── main.tsx                   # Application entry point
├── index.css                  # Global styles and Tailwind imports
├── app/                       # App initialization and routing
├── config/                    # Configuration files
├── features/                  # Feature modules (pages)
└── shared/                    # Shared resources
```

## src/app/
```
src/app/
├── App.tsx                    # Main app component with Keycloak initialization
└── AppRouter.tsx              # Route definitions and routing setup
```

## src/config/
```
src/config/
├── constants.ts               # App constants (API URLs, routes, pagination)
└── keycloak.ts                # Keycloak client configuration
```

## src/features/
```
src/features/
├── auth/
│   └── LoginPage.tsx          # Login page (redirects to Keycloak)
├── dashboard/
│   └── DashboardPage.tsx      # Dashboard page (placeholder)
├── home/
│   └── HomePage.tsx           # Home/landing page
└── products/
    └── ProductsPage.tsx       # Products list page with filters and table
```

## src/shared/
```
src/shared/
├── components/                # Reusable UI components
├── services/                  # API clients
├── store/                     # State management (Zustand)
└── types/                     # TypeScript type definitions
```

## src/shared/components/
```
src/shared/components/
├── Button.tsx                 # Reusable button component
├── EmptyState.tsx             # Empty state component
├── Input.tsx                  # Input field component
├── Layout.tsx                 # Page layout wrapper
├── Loading.tsx                # Loading spinner component
├── Navigation.tsx             # Navigation header component
├── Select.tsx                 # Select dropdown component
└── index.ts                   # Component exports
```

## src/shared/services/
```
src/shared/services/
├── api.ts                     # Axios instance with interceptors
└── productApi.ts              # Product service API client
```

## src/shared/store/
```
src/shared/store/
└── authStore.ts               # Zustand store for authentication state
```

## src/shared/types/
```
src/shared/types/
└── index.ts                   # TypeScript interfaces and types
```

---

## 📋 Complete File Tree

```
frontend/
│
├── Configuration Files (Root Level)
│   ├── .eslintrc.cjs
│   ├── .gitignore
│   ├── index.html
│   ├── package.json
│   ├── postcss.config.js
│   ├── README.md
│   ├── QUICK_START.md
│   ├── tailwind.config.js
│   ├── tsconfig.json
│   ├── tsconfig.node.json
│   └── vite.config.ts
│
└── src/
    │
    ├── Entry Files
    │   ├── main.tsx
    │   └── index.css
    │
    ├── app/
    │   ├── App.tsx
    │   └── AppRouter.tsx
    │
    ├── config/
    │   ├── constants.ts
    │   └── keycloak.ts
    │
    ├── features/
    │   ├── auth/
    │   │   └── LoginPage.tsx
    │   ├── dashboard/
    │   │   └── DashboardPage.tsx
    │   ├── home/
    │   │   └── HomePage.tsx
    │   └── products/
    │       └── ProductsPage.tsx
    │
    └── shared/
        ├── components/
        │   ├── Button.tsx
        │   ├── EmptyState.tsx
        │   ├── Input.tsx
        │   ├── Layout.tsx
        │   ├── Loading.tsx
        │   ├── Navigation.tsx
        │   ├── Select.tsx
        │   └── index.ts
        ├── services/
        │   ├── api.ts
        │   └── productApi.ts
        ├── store/
        │   └── authStore.ts
        └── types/
            └── index.ts
```

---

## 📦 File Purposes

### Root Configuration Files

| File | Purpose |
|------|---------|
| `.eslintrc.cjs` | ESLint rules for code quality |
| `.gitignore` | Files to ignore in Git |
| `index.html` | HTML template, app entry point |
| `package.json` | Dependencies and NPM scripts |
| `postcss.config.js` | PostCSS plugins (Tailwind) |
| `README.md` | Full documentation |
| `QUICK_START.md` | Quick installation guide |
| `tailwind.config.js` | Tailwind theme customization |
| `tsconfig.json` | TypeScript compiler options |
| `tsconfig.node.json` | TypeScript for build tools |
| `vite.config.ts` | Vite dev server and build config |

### Source Files

| File | Purpose |
|------|---------|
| `src/main.tsx` | React app initialization |
| `src/index.css` | Global CSS + Tailwind imports |
| `src/app/App.tsx` | Main app with Keycloak init |
| `src/app/AppRouter.tsx` | React Router setup |
| `src/config/constants.ts` | API URLs, routes, constants |
| `src/config/keycloak.ts` | Keycloak client instance |

### Features (Pages)

| File | Purpose |
|------|---------|
| `features/auth/LoginPage.tsx` | Login page (Keycloak redirect) |
| `features/dashboard/DashboardPage.tsx` | Dashboard page |
| `features/home/HomePage.tsx` | Landing page |
| `features/products/ProductsPage.tsx` | Products list with filters |

### Shared Components

| File | Purpose |
|------|---------|
| `shared/components/Button.tsx` | Reusable button |
| `shared/components/EmptyState.tsx` | Empty state UI |
| `shared/components/Input.tsx` | Text input field |
| `shared/components/Layout.tsx` | Page layout wrapper |
| `shared/components/Loading.tsx` | Loading spinner |
| `shared/components/Navigation.tsx` | Navigation header |
| `shared/components/Select.tsx` | Dropdown select |
| `shared/components/index.ts` | Component exports |

### Services & State

| File | Purpose |
|------|---------|
| `shared/services/api.ts` | Axios client with interceptors |
| `shared/services/productApi.ts` | Product API methods |
| `shared/store/authStore.ts` | Authentication state (Zustand) |
| `shared/types/index.ts` | TypeScript types/interfaces |

---

## 🎯 Key Directories Explained

### `/src/app`
Application initialization and routing configuration. Contains the main App component that handles Keycloak authentication and the router setup.

### `/src/config`
Configuration files for constants, Keycloak settings, and other app-wide configurations.

### `/src/features`
Feature-based organization. Each feature (auth, products, dashboard) has its own directory with related components.

### `/src/shared`
Reusable code shared across features:
- **components**: UI components used in multiple features
- **services**: API clients and external service integrations
- **store**: Global state management
- **types**: TypeScript type definitions

---

## 📝 Installation Instructions

1. **Extract the ZIP file** to your `frontend/` directory
2. **Install dependencies**: `npm install`
3. **Start dev server**: `npm run dev`
4. **Access at**: http://localhost:5173

All files will be in their correct locations when you extract the ZIP!
