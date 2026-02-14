# API Gateway Specification - Meal tracking app

**IMPORTANT:** this is only entry point for frontend!

## Base Configuration
```
Base URL: http://localhost:8080
Authentication: Bearer JWT (Keycloak)
CORS: Enabled for http://localhost:5173
```
## Authentication Flow

### 1. Keycloak Login
Frontend uses `keycloak-js` SDK:
```typescript
keycloak.init({
  onLoad: 'login-required',
  checkLoginIframe: false
})
```

### 2. Token in each request header
```
Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI...
```

### 3. Refresh token
Frontend uses `keycloak-js` for refresh tokens.
Access code lifespan set to 5 minutes in Keycloak.

### 4. Token validation
API Gateway validates token with Keycloak before forwarding request to microservice.

### 5. Roles based access
Each microservice check token for role base access defined in SecurityConfig and method level security.
Available roles are:
ADMIN
USER

UI components are visible based on roles. For example only authenticated users will be displayed `logout` button.

## API Endpoints (through Gateway)

### Product service
For reference check backend/product-ms

**Prefix:** `/product/api` - each endpoint starts with this path in gateway

note: there are two types of products - global products and custom products.
Only admin may add global product and it is available for anyone.
User can add custom product and it available only for that user.

#### POST /products
Add new global product 
Required roles: ADMIN

**Request body:**
```json
{
  "name": "salmon",
  "productCategory": "FISH",
  "calories": 670,
  "protein": "21",
  "carbs": 0,
  "fat": "67"
}
```

**Response 201:**
```json
{
  "name": "salmon",
  "productCategory": "FISH",
  "calories": 670,
  "protein": "21",
  "carbs": 0,
  "fat": "67"
}
```

#### POST /products/custom
Add new custom product
Required roles: ADMIN, USER

**Request body:**
```json
{
  "name": "salmon",
  "productCategory": "FISH",
  "calories": 670,
  "protein": "21",
  "carbs": 0,
  "fat": "67"
}
```

**Response 201:**
```json
{
  "name": "salmon",
  "productCategory": "FISH",
  "calories": 670,
  "protein": "21",
  "carbs": 0,
  "fat": "67"
}
```
#### GET /products/{productId}
Get product by ID. UI will redirect to specific product route by clicking on a product in products table. Table will display
products by it`s name, not by ID.
Required roles: PERMIT ALL

**Response 200:**
```json
{
  "name": "salmon",
  "productCategory": "FISH",
  "calories": 670,
  "protein": "21",
  "carbs": 0,
  "fat": "67"
}
```
#### GET /products
Get products for specified params. UI will have displayed:
- category combo box to choose from valid category - if none select all categories will be matched
- product name text field to insert searched product name
- `filter` button - after clicking filtering will be done and request send to this API
- products will be pageable with 50 products per page
Required roles: PERMIT ALL

**Query params:**
- `pageNo` (int, default 0)
- `category` (String, required: false)
- `name`, (String, default ``)

**Response 200:**
```json
[
    {
      "name": "salmon",
      "productCategory": "FISH",
      "calories": 670,
      "protein": "21",
      "carbs": 0,
      "fat": "67"
    },
  {
    "name": "turkey",
    "productCategory": "MEAT",
    "calories": 125,
    "protein": "21",
    "carbs": 0,
    "fat": "1"
  }
]
```

#### PUT /products/{productId}
Update product data.
Update button displayed based on:
- ADMIN role for global products
- USER role for custom product and user username must match product.ownerUsername

Required roles: ADMIN, USER

**Request body:**
```json
{
  "name": "salmon",
  "productCategory": "FISH",
  "calories": 670,
  "protein": "21",
  "carbs": 0,
  "fat": "67"
}
```
**Response 202:**
```json
{
  "name": "salmon",
  "productCategory": "FISH",
  "calories": 670,
  "protein": "21",
  "carbs": 0,
  "fat": "67"
}
```
#### DELETE /products/{productId}
Delete product from database.
Delete button displayed based on:
- ADMIN role for global products
- USER role for custom product and user username must match product.ownerUsername
Confirmation notification popped after clicking Delete button

Required roles: ADMIN, USER

**Response 204**

## Resiliency

API Gateway uses Resilience4j:
- Timeout: 1s connect-timeout, 2s response-timeout
- Circuit Breaker: 50 % failure threshold
- Retry: 3 retries with exponential backoff - for GET methods only
- Rate limiter: Token Bucket pattern - 10 requests per second for user replenish rate, 20 burst, 1 token per request
- Fallback responses for unreachable services on /api/fallback

### Fallback response
**Response 503**
```json
"Unexpected error occurred. Please try again later or contact support."
```

## Error responses

All error responses from API Gateway follow unified error format handled with @RestControllerAdvice in microservices

Standard error response below. "message" is displayed to end user.
```json
{
"statusCode": "403",
"errorMessage": "Forbidden",
"message": "Human readable message",
"path": "/api/...",
"timestamp": "..."
}
```
### Error types
- `400` - Bad request
- `401` - Unauthorized (invalid/missing token)
- `403` - Forbidden (required role missing)
- `404` - Not found
- `429` - Too many requests (rate limit)
- `500` - Internal server error
- `503` - Service unavailable (circuit breaker open)
