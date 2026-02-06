# API Gateway Specification - Meal tracking app

Phase 1 (CURRENT): Core Features - No Auth

Phase 2 (FUTURE): Add Keycloak Auth

**IMPORTANT:** this is only entry point for frontend!

## Base Configuration
```
Base URL: http://localhost:8080/api
Authentication: Bearer JWT (Keycloak) - skip for now - do not add Authorization header
CORS: Enabled for http://localhost:5173
```
## Authentication Flow - Skip for now - will be implemented in future (Phase 2)

### 1. Keycloak Login
Frontend uses `keycloak-js` SDK:
```typescript
keycloak.init({
  onLoad: 'login-required',
  checkLoginIframe: false
})
```

### 2. Token in each request
```
Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI...
```

### 3. Token validation
API Gateway validates token with Keycloak before forwarding request to microservice

## API Endpoints (through Gateway)

### User Management Service
**Prefix:** `/api/users`

#### GET /api/users/me
Fetch logged user data

**Response 200:**
```json
{
  "id": "uuid-123",
  "email": "user@example.com",
  "firstName": "Jan",
  "lastName": "Kowalski",
  "roles": ["USER"],
  "createdAt": "2025-01-10T10:00:00Z"
}
```


## Resiliency

API Gateway uses Resilience4j:
- Timeout: 5s per request
- Circuit Breaker: Opens after 5 errors within 10s
- Retry: 3 retries with exponential backoff
- Rate limiter: 50 requests per minute for user - disabled in current phase
- Fallback responses for unreachable services with error, static value or value from cache

**Example for fallback response:**
```json
{
  "error": "SERVICE_UNAVAILABLE",
  "message": "Diet Service is temporarily unavailable. Please try again later.",
  "fallback": true
}
```

## Error responses

All error responses from API Gateway follow unified error format handled with @RestControllerAdvice 

Standard error response:
```
{
"statusCode": "...",
"message": "Human readable message",
"timestamp": "...",
"path": "/api/..."
}
```