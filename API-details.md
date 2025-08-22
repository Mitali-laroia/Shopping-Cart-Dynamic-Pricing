# Shopping Cart Dynamic Pricing - API Reference

## Set 1: Foundation APIs (Product & Customer Management) ✅

### Product Management APIs

#### Get All Products
```http
GET /api/v1/products
Content-Type: application/json
```
**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "name": "Laptop",
    "category": "ELECTRONICS",
    "price": 1000.00,
    "description": "High-performance laptop",
    "stockQuantity": 10,
    "createdAt": "2025-08-22T07:45:26.154709",
    "updatedAt": "2025-08-22T07:45:26.155282"
  }
]
```

#### Get Product by ID
```http
GET /api/v1/products/{id}
Content-Type: application/json
```
**Response:** `200 OK` (product found) | `404 Not Found` (product not found)

#### Get Products by Category
```http
GET /api/v1/products/category/{category}
Content-Type: application/json
```
**Categories:** `ELECTRONICS`, `BOOKS`, `CLOTHING`
**Response:** `200 OK`

#### Create Product
```http
POST /api/v1/products
Content-Type: application/json

{
  "name": "Laptop",
  "category": "ELECTRONICS",
  "price": 1000,
  "description": "High-performance laptop",
  "stockQuantity": 10
}
```
**Response:** `201 Created`
```json
{
  "id": 1,
  "name": "Laptop",
  "category": "ELECTRONICS",
  "price": 1000,
  "description": "High-performance laptop",
  "stockQuantity": 10,
  "createdAt": "2025-08-22T07:45:26.1547093",
  "updatedAt": "2025-08-22T07:45:26.155282"
}
```

#### Update Product
```http
PUT /api/v1/products/{id}
Content-Type: application/json

{
  "name": "Updated Laptop",
  "category": "ELECTRONICS",
  "price": 1200,
  "description": "Updated description",
  "stockQuantity": 15
}
```
**Response:** `200 OK` (updated) | `404 Not Found` (product not found) | `400 Bad Request` (validation error)

#### Delete Product
```http
DELETE /api/v1/products/{id}
Content-Type: application/json
```
**Response:** `204 No Content` (deleted) | `404 Not Found` (product not found)

#### Update Stock
```http
PATCH /api/v1/products/{id}/stock
Content-Type: application/json

{
  "quantity": 15
}
```
**Response:** `200 OK`
```json
{
  "newStock": 15,
  "message": "Stock updated successfully",
  "productId": 1
}
```

### Customer Management APIs

#### Get All Customers
```http
GET /api/v1/customers
Content-Type: application/json
```
**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "loyaltyLevel": "SILVER",
    "createdAt": "2025-08-22T07:45:37.016011",
    "updatedAt": "2025-08-22T07:45:37.016011"
  }
]
```

#### Get Customer by ID
```http
GET /api/v1/customers/{id}
Content-Type: application/json
```
**Response:** `200 OK` (customer found) | `404 Not Found` (customer not found)

#### Get Customer by Email
```http
GET /api/v1/customers/email/{email}
Content-Type: application/json
```
**Response:** `200 OK` (customer found) | `404 Not Found` (customer not found)

#### Get Customers by Loyalty Level
```http
GET /api/v1/customers/loyalty/{loyaltyLevel}
Content-Type: application/json
```
**Loyalty Levels:** `BRONZE`, `SILVER`, `GOLD`
**Response:** `200 OK`

#### Create Customer
```http
POST /api/v1/customers
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "loyaltyLevel": "SILVER"
}
```
**Response:** `201 Created`
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "loyaltyLevel": "SILVER",
  "createdAt": "2025-08-22T07:45:37.0160113",
  "updatedAt": "2025-08-22T07:45:37.0160113"
}
```

#### Update Customer
```http
PUT /api/v1/customers/{id}
Content-Type: application/json

{
  "name": "John Smith",
  "email": "johnsmith@example.com",
  "loyaltyLevel": "GOLD"
}
```
**Response:** `200 OK` (updated) | `404 Not Found` (customer not found) | `400 Bad Request` (validation error)

#### Delete Customer
```http
DELETE /api/v1/customers/{id}
Content-Type: application/json
```
**Response:** `204 No Content` (deleted) | `404 Not Found` (customer not found)

#### Update Loyalty Level
```http
PATCH /api/v1/customers/{id}/loyalty
Content-Type: application/json

{
  "loyaltyLevel": "GOLD"
}
```
**Response:** `200 OK` (updated) | `404 Not Found` (customer not found)

### Common Error Responses

#### Validation Error
```json
{
  "error": "Validation error",
  "message": "Product name is required"
}
```

#### Not Found Error
```json
{
  "error": "Resource not found",
  "message": "Product not found with id: 999"
}
```

#### Internal Server Error
```json
{
  "error": "Internal server error",
  "message": "An unexpected error occurred"
}
```

### Application Health Check
```http
GET /actuator/health
Content-Type: application/json
```
**Response:** `200 OK`
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "H2",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP"
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

---

## Testing Status

### Set 1: Foundation APIs ✅
- **Product Management APIs**: All 7 endpoints tested and working
- **Customer Management APIs**: All 8 endpoints tested and working
- **Error Handling**: Proper validation and error responses verified
- **Database Integration**: H2 database with MyBatis mappers working correctly

### Upcoming Sets
- **Set 2: Cart Operations APIs** - Not implemented
- **Set 3: Pricing Engine APIs** - Not implemented