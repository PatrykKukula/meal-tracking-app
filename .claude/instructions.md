# Claude Code Instructions - Meal Tracking App (Microservices)

## System Architecture

### Type: Spring Boot Microservices + React SPA
- **Single EntryPoint** - API Gateway (port 8080)
- **Service Discovery** - Eureka Server
- **Config Management** - Spring Cloud Config Sever
- **Authorization** - Keycloak (OAuth2/OIDC) (port 7080)
- **Frontend** - React 19 + TypeScript + Tailwind
- **Async** - RabbitMQ

### CRITICAL: Frontend does not have access to specific microservices.
Browser communicates only through API Gateway:

Browser -> API Gateway -> Microservice

### Backend (package: backend/)
- **DO NOT MODIFY** any code
- **ONLY READ** for context
- **ALWAYS** use API Gateway as a single entry point
- Ask for API changes before frontend implementation

### Frontend (package: frontend/)
- **Complete implementation freedom**
- Tech Stack:
    - React 19 + TypeScript
    - Tailwind CSS
    - React Query v5 (server state management)
    - Zustand (client state management)
    - React Router v6
    - Axios
    - Keycloak-js (authentication & authorization)

### Authentication and Authorization
- **KeyCloak** as an Authorization Server
- Flow: Authorization Code + PKCE
- JWT Token in header: 'Authorization: Bearer {Token}'
- Automatic refresh token with keycloak-js
- CSRF protection with keycloak-js
- **DO NOT STORE** token in localStorage - use httpOnly cookies or memory

Realm name: MealTrackingApp
Use this Client ID: mealtrackingappclient

### Authorization errors
- 401 Unauthorized → user not authenticated (frontend should trigger re-login and display notification)
- 403 Forbidden → user authenticated but lacks required role - should redirect to /home route and display notification

### Ports:
- API Gateway: 8080
- Eureka: 8761
- Config Server: 8888
- Keycloak: 7080
- Frontend: 5173 (Vite)
- Services: unreachable for browser

### UI/UX:
- prefer white/green motives
- modern style design
- each page has common navigation bar header with tabs: dashboard, products, login/logout
- components visibility based on roles and sometimes on user id (refer to gateway-api-spec.md)

### API Communication (always use this)
```typescript
const API_BASE_URL = 'http://localhost:8080'
```

### API Gateway routing:
```
/api/profile/**       → User profile Service
/api/diet/**          → Diet Service
/api/products/**      → Product Service
/api/statistics/**    → Statistics Service
```

## API Documentation
**MAIN SOURCE OF TRUTH** `.claude/gateway-api-spec.md`

⚠️ **Always check gateway-api-spec.md first** - real endpoints for frontend are available in there!

gateway-api-spec.md will be updated during project implementation. 
Endpoints may be added, deleted and updated.
Always check for any changes.

## Frontend package structure:
```
frontend/src/
├── app/                   # App setup, routing
├── features/              # Feature-based organization
│   ├── auth/              # Keycloak, login, register, protected routes
│   ├── diet/              # Diet management module
│   ├── products/          # Products catalog
│   ├── profile/           # User profile & settings
│   ├── statistics/        # Statistics
│   └── dashboard/         # Main dashboard
├── shared/
│   ├── components/        # Reusable UI components
│   ├── hooks/             # Custom hooks
│   ├── services/          # API clients
│   │   ├── api.ts         # Axios instance with interceptors
│   │   ├── dietApi.ts     # Diet service client
│   │   ├── productApi.ts  # Product service client
│   │   └── profileApi.ts  # Profile service client
│   ├── types/             # TypeScript types/interfaces
│   └── utils/             # Utility functions
└── config/
    ├── keycloak.ts        # Keycloak configuration
    └── constants.ts       # App constants
```

## API Client (axios instance):
```typescript
// Always use this setup
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});
```

## Error Handling:
- HTTP errors: React Query error boundaries
- Validation errors: Zod schemas
- Business errors: Toast notifications (react-hot-toast)

Technical errors should never be shown as toast messages.

All error responses have common pattern with followed example:
```
{
  "statusMessage": "Not found",
  "statusCode": 404,
  "message": "not found",
  "path": "api/products",
  "occurrenceTime": "dd-mm-YYYY hh:MM"
}
```

## Technology Stack

### Infrastructure
- **Service Discovery** - Eureka Server
- **API Gateway** - Spring Cloud Gateway
- **Config Server** - Spring Cloud Config Server
- **Authorization Server** - KeyCloak (OAuth2/OIDC)
- **Message Broker** - RabbitMQ
- **Caching** - Caffeine

### Microservices
- **Framework** - Spring Boot 4.0.2
- **Java** - 21
- **Database** - PostgreSQL (per service)
- **ORM** - Spring Data JPA + Hibernate

### Frontend
- **Framework** - React 19
- **Language** - TypeScript 5
- **Bundler** - Vite
- **Styling** - Tailwind CSS
- **Anything else** - up to Claude choice

### Security
- **JWT Validation** in API Gateway - gateway validates and forwards token to microservice
- **JWT Validation** in individual microservices - token validation and role based access
- **Method level security** in individual services

### Resiliency
- **Resilience4J** - circuit breaker, retry, rate limiter

## Tasks

### Current:
- [ ] Products page:
  - table view with products data: name, calories, protein, carbs, fat
  - pageable (50 products/page)
  - filterable (product category combo box, product name text field, filter button)
  

### Finished:

### Planned:
- add product page
- product page

## Important notes
1. **ALWAYS** use API Gateway - never directly connect to specific microservice
2. **NEVER** hardcore microservices ports in frontend
3. **CHECK** gateway API_SPEC.md before every implementation
4. **CORS** handled in API Gateway
5. **Rate limiting** handled in API Gateway
6. **NEVER** modify /backend
7. **NEVER** hardcode sensitive data in frontend
8. gateway API_SPEC.md wll include some frontend requirements

Below section is for reference ONLY - do NOT implement now:

Add Keycloak configuration
Update Axios interceptor with JWT token
Create login/logout pages
Add protected routes wrapper
Implement token refresh logic
Add role-based UI elements
Update API calls to include Authorization header