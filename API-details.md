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

## Set 2: Cart Operations APIs (Shopping Cart Management) ✅

### Cart Management APIs

#### Get All Carts
```http
GET /api/v1/cart
Content-Type: application/json
```
**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "customer": {
      "id": 1,
      "name": "John Doe",
      "email": "john@example.com",
      "loyaltyLevel": "SILVER"
    },
    "status": "ACTIVE",
    "totalAmount": 2000.00,
    "items": [],
    "createdAt": "2025-08-22T10:41:24.512888",
    "updatedAt": "2025-08-22T10:41:39.078028"
  }
]
```

#### Create Cart
```http
POST /api/v1/cart
Content-Type: application/json

{
  "customerId": 1
}
```
**Response:** `201 Created`
```json
{
  "id": 1,
  "customer": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "loyaltyLevel": "SILVER"
  },
  "status": "ACTIVE",
  "totalAmount": 0,
  "items": [],
  "createdAt": "2025-08-22T10:41:24.5128877",
  "updatedAt": "2025-08-22T10:41:24.5128877"
}
```

#### Get Cart with Items
```http
GET /api/v1/cart/{cartId}
Content-Type: application/json
```
**Response:** `200 OK` (cart found) | `404 Not Found` (cart not found)
```json
{
  "id": 1,
  "customer": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "loyaltyLevel": "SILVER"
  },
  "status": "ACTIVE",
  "totalAmount": 2000.00,
  "items": [
    {
      "id": 1,
      "product": {
        "id": 1,
        "name": "Laptop",
        "category": "ELECTRONICS",
        "price": 1000.00,
        "description": "High-performance laptop",
        "stockQuantity": 10
      },
      "quantity": 2,
      "unitPrice": 1000.00,
      "totalPrice": 2000.00,
      "createdAt": "2025-08-22T10:41:39.062265",
      "updatedAt": "2025-08-22T10:41:39.062265"
    }
  ],
  "createdAt": "2025-08-22T10:41:24.512888",
  "updatedAt": "2025-08-22T10:41:39.078028",
  "totalItems": 1
}
```

#### Get Carts by Customer
```http
GET /api/v1/cart/customer/{customerId}
Content-Type: application/json
```
**Response:** `200 OK`

#### Get Carts by Status
```http
GET /api/v1/cart/status/{status}
Content-Type: application/json
```
**Status Values:** `ACTIVE`, `COMPLETED`, `ABANDONED`
**Response:** `200 OK`

#### Delete Cart
```http
DELETE /api/v1/cart/{cartId}
Content-Type: application/json
```
**Response:** `204 No Content` (deleted) | `404 Not Found` (cart not found)

### Cart Item Management APIs

#### Add Item to Cart
```http
POST /api/v1/cart/{cartId}/items
Content-Type: application/json

{
  "productId": 1,
  "quantity": 2
}
```
**Response:** `201 Created` | `404 Not Found` (cart/product not found) | `400 Bad Request` (validation error)
```json
{
  "id": 1,
  "shoppingCart": {
    "id": 1,
    "status": "ACTIVE",
    "totalAmount": 0.00
  },
  "product": {
    "id": 1,
    "name": "Laptop",
    "category": "ELECTRONICS",
    "price": 1000.00,
    "description": "High-performance laptop",
    "stockQuantity": 10
  },
  "quantity": 2,
  "unitPrice": 1000.00,
  "totalPrice": 2000.00,
  "createdAt": "2025-08-22T10:41:39.0622645",
  "updatedAt": "2025-08-22T10:41:39.0622645"
}
```

#### Get Cart Items
```http
GET /api/v1/cart/{cartId}/items
Content-Type: application/json
```
**Response:** `200 OK`

#### Update Cart Item
```http
PUT /api/v1/cart/{cartId}/items/{itemId}
Content-Type: application/json

{
  "quantity": 3
}
```
**Response:** `200 OK` (updated) | `404 Not Found` (item not found) | `400 Bad Request` (validation error)

#### Remove Item from Cart
```http
DELETE /api/v1/cart/{cartId}/items/{itemId}
Content-Type: application/json
```
**Response:** `200 OK`
```json
{
  "message": "Item removed successfully",
  "cartId": "1",
  "itemId": "1"
}
```

#### Clear Cart (Remove All Items)
```http
DELETE /api/v1/cart/{cartId}/items
Content-Type: application/json
```
**Response:** `200 OK`
```json
{
  "message": "Cart cleared successfully",
  "cartId": "1"
}
```

### Cart Business Logic Features

#### Automatic Stock Validation
- Validates product stock availability before adding/updating items
- Prevents overselling by checking `stockQuantity`
- Returns `400 Bad Request` with error message for insufficient stock

#### Automatic Cart Total Calculation
- Automatically calculates and updates cart total when items are added/updated/removed
- Updates `totalAmount` field in shopping cart
- Maintains consistency between item totals and cart total

#### Duplicate Item Handling
- When adding an existing product to cart, increases quantity instead of creating duplicate
- Maintains one cart item per product per cart
- Updates existing item's quantity and total price

#### Comprehensive Error Handling
- **404 Not Found**: Cart, product, or cart item not found
- **400 Bad Request**: Invalid quantity, insufficient stock, validation errors
- **500 Internal Server Error**: Unexpected system errors
- All methods include try-catch blocks with appropriate error responses

---

## Testing Status

### Set 1: Foundation APIs ✅
- **Product Management APIs**: All 7 endpoints tested and working
- **Customer Management APIs**: All 8 endpoints tested and working
- **Error Handling**: Proper validation and error responses verified
- **Database Integration**: H2 database with MyBatis mappers working correctly

### Set 2: Cart Operations APIs ✅
- **Cart Management APIs**: All 10 endpoints tested and working
- **Item Management APIs**: Add, update, remove items functionality verified
- **Exception Handling**: Comprehensive error handling for all scenarios
- **Business Logic**: Stock validation, quantity updates, and cart totals working correctly

### Upcoming Sets
- **Set 3: Pricing Engine APIs** - Not implemented