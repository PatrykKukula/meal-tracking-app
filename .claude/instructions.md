# Claude Code Instructions - Meal Tracking App (Microservices)

## System Architecture

### Type: Spring Boot Microservices + React SPA
- **Single EntryPoint** - API Gateway (port 8080)
- **Service Discovery** - Eureka Server
- **Config Management** - Spring Cloud Config Sever
- **Authorization** - Keycloak (OAuth2/OIDC)
- **Frontend** - React 19 + TypeScript + Tailwind

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
    - Keycloak-js (authentication)

### Authentication and Authorization
- **KeyCloak** as an Authorization Server
- Flow: Authorization Code + PKCE
- JWT Token in header: 'Authorization: Bearer {Token}'
- Automatic refresh token
- **DO NOT STORE** token in localStorage - use httpOnly cookies or memory
- 
### Authorization errors
- 401 Unauthorized → user not authenticated (frontend should trigger re-login and display notification)
- 403 Forbidden → user authenticated but lacks required role - should redirect to route displaying 
"You do not have access to this page"

### Ports:
- API Gateway: 8080
- Eureka: 8761
- Config Server: 8888
- Keycloak: 8180
- Frontend: 5173 (Vite)
- Services: 8081-8085 (unreachable for browser)

### UI/UX:
- prefer white/green motives
- same header should be on top of each page with login button

### API Communication (always use this)
```typescript
const API_BASE_URL = 'http://localhost:8080/api'
```

### API Gateway routing:
```
/api/users/**          → User Management Service 
/api/diets/**          → Diet Service
/api/products/**       → Product Service
/api/profiles/**       → User Profile Service
```
Email Microservice does not have any API. It communicates asynchronously via RabbitMQ.

## API Documentation
**MAIN SOURCE OF TRUTH** `.claude/gateway-api-spec.md`

Specific Microservice details:
- User Management: `services/user-management-ms/API_SPEC.md`
- Diet Service: `services/diet-service-ms/API_SPEC.md`
- Product Service: `services/product-service-ms/API_SPEC.md`
- User Profile: `services/user-profile-service-ms/API_SPEC.md`

⚠️ **Always check gateway-api-spec.md first** - real endpoints for frontend are available in there!

API_SPEC.md in every microservice will be updated during project implementation. 
Endpoints may be added, deleted and updated.

## Frontend package structure:
```
frontend/src/
├── app/                    # App setup, routing
├── features/               # Feature-based organization
│   ├── auth/              # Keycloak, login, protected routes
│   ├── diet/              # Diet management module
│   ├── products/          # Products catalog
│   ├── profile/           # User profile & settings
│   └── dashboard/         # Main dashboard
├── shared/
│   ├── components/        # Reusable UI components
│   ├── hooks/             # Custom hooks
│   ├── services/          # API clients
│   │   ├── api.ts        # Axios instance with interceptors
│   │   ├── dietApi.ts    # Diet service client
│   │   ├── productApi.ts # Product service client
│   │   └── profileApi.ts # Profile service client
│   ├── types/            # TypeScript types/interfaces
│   └── utils/            # Utility functions
└── config/
    ├── keycloak.ts       # Keycloak configuration
    └── constants.ts      # App constants
```
Note: No auth/ feature for now - will be added in Phase 2.


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
- **Caching** - Caffeine + Redis

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
- **JWT Validation** in individual microservices
- **Role based access** with Spring Security
- **Method level security** in individual services

### Resiliency
- **Resilience4J** - circuit breaker, retry, rate limiter

## Tasks

### Current:
- Products page - display all the products with search filter and categories combo box. List should be pageable with 50 records per page.
Table should display: name, category, calories, protein, carbs, fat. There should be `Add product` button available only for authenticated users. 
Clicking on product should redirect to product  (to be done in future task). 
`Add product` button should redirect to Add product form layout (to be done in future task).

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

Below section is for reference ONLY - do NOT implement now:

Add Keycloak configuration
Update Axios interceptor with JWT token
Create login/logout pages
Add protected routes wrapper
Implement token refresh logic
Add role-based UI elements
Update API calls to include Authorization header