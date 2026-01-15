# UserManagementController - Postman Test Guide

## Base Configuration
- **Base URL**: `http://localhost:8080`
- **Authentication**: Bearer Token (JWT)
- **Content-Type**: `application/json`

---

## Prerequisites
Before testing UserManagementController endpoints, you need:
1. A valid JWT token (obtained from authentication endpoint)
2. A user with `USER_MANAGE` authority/permission
3. Existing user IDs, permission IDs, and role IDs in the database

---

## Endpoints

### 1. Assign Permission to User
Assign a permission to a user.

**Method**: `POST`  
**URL**: `{{baseUrl}}/api/admin/users/permissions`

**Headers**:
```
Authorization: Bearer {{token}}
Content-Type: application/json
```

**Request Body**:
```json
{
    "userId": 2,
    "permissionId": 1
}
```

**Expected Response** (200 OK):
```json
{
    "id": 1,
    "userId": 2,
    "username": "john.doe",
    "permissionId": 1,
    "permissionName": "USER_MANAGE",
    "active": true,
    "grantedBy": 1,
    "grantedAt": "2026-01-02T10:30:00",
    "revokedAt": null
}
```

**Test Scenarios**:
- Valid user and permission IDs
- Non-existent user ID (should return 404)
- Non-existent permission ID (should return 404)
- Duplicate permission assignment
- Without authentication token (should return 401)
- With user without USER_MANAGE authority (should return 403)

---

### 2. Remove Permission from User
Remove a permission from a user.

**Method**: `DELETE`  
**URL**: `{{baseUrl}}/api/admin/users/{userId}/permissions/{permissionId}`

**Example**: `{{baseUrl}}/api/admin/users/2/permissions/1`

**Headers**:
```
Authorization: Bearer {{token}}
```

**Path Variables**:
- `userId`: 2
- `permissionId`: 1

**Expected Response** (200 OK):
```json
"Permission removed successfully"
```

**Test Scenarios**:
- Valid userId and permissionId
- Non-existent userId (should return 404)
- Non-existent permissionId (should return 404)
- Permission not assigned to user (should return 404)
- Without authentication (should return 401)
- Without USER_MANAGE authority (should return 403)

---

### 3. Activate Permission
Activate a previously deactivated permission for a user.

**Method**: `PATCH`  
**URL**: `{{baseUrl}}/api/admin/users/{userId}/permissions/{permissionId}/activate`

**Example**: `{{baseUrl}}/api/admin/users/2/permissions/1/activate`

**Headers**:
```
Authorization: Bearer {{token}}
```

**Path Variables**:
- `userId`: 2
- `permissionId`: 1

**Expected Response** (200 OK):
```json
"Permission activated successfully"
```

**Test Scenarios**:
- Activate a deactivated permission
- Activate an already active permission
- Non-existent userId or permissionId
- Without authentication (should return 401)
- Without USER_MANAGE authority (should return 403)

---

### 4. Deactivate Permission
Deactivate a permission for a user without removing it.

**Method**: `PATCH`  
**URL**: `{{baseUrl}}/api/admin/users/{userId}/permissions/{permissionId}/deactivate`

**Example**: `{{baseUrl}}/api/admin/users/2/permissions/1/deactivate`

**Headers**:
```
Authorization: Bearer {{token}}
```

**Path Variables**:
- `userId`: 2
- `permissionId`: 1

**Expected Response** (200 OK):
```json
"Permission deactivated successfully"
```

**Test Scenarios**:
- Deactivate an active permission
- Deactivate an already deactivated permission
- Non-existent userId or permissionId
- Without authentication (should return 401)
- Without USER_MANAGE authority (should return 403)

---

### 5. Assign Role to User
Assign a role to a user.

**Method**: `POST`  
**URL**: `{{baseUrl}}/api/admin/users/{userId}/role/{roleId}`

**Example**: `{{baseUrl}}/api/admin/users/2/role/3`

**Headers**:
```
Authorization: Bearer {{token}}
```

**Path Variables**:
- `userId`: 2
- `roleId`: 3

**Expected Response** (200 OK):
```json
"Role assigned successfully"
```

**Test Scenarios**:
- Valid userId and roleId
- Non-existent userId (should return 404)
- Non-existent roleId (should return 404)
- Duplicate role assignment
- Without authentication (should return 401)
- Without USER_MANAGE authority (should return 403)

---

## Postman Environment Variables Setup

Create these variables in your Postman environment:

| Variable | Initial Value | Current Value |
|----------|--------------|---------------|
| baseUrl | http://localhost:8080 | http://localhost:8080 |
| token | | (will be set after login) |
| userId | 2 | 2 |
| permissionId | 1 | 1 |
| roleId | 3 | 3 |

---

## Complete Test Flow

### Step 1: Login to get JWT Token
**Endpoint**: `POST /api/auth/login`

**Request Body**:
```json
{
    "username": "admin",
    "password": "admin123"
}
```

**Response**:
```json
{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "type": "Bearer",
    "username": "admin",
    "authorities": ["USER_MANAGE", "ADMIN"]
}
```

**Postman Test Script** (to save token):
```javascript
if (pm.response.code === 200) {
    var jsonData = pm.response.json();
    pm.environment.set("token", jsonData.token);
}
```

---

### Step 2: Test Each Endpoint in Order

1. **Assign Permission** → Should succeed
2. **Deactivate Permission** → Should succeed
3. **Activate Permission** → Should succeed
4. **Remove Permission** → Should succeed
5. **Assign Role** → Should succeed

---

## Sample Test Data

### Users (assumed to exist in DB)
```json
{
    "id": 1,
    "username": "admin",
    "authorities": ["USER_MANAGE", "ADMIN"]
},
{
    "id": 2,
    "username": "john.doe",
    "authorities": ["USER_READ"]
}
```

### Permissions (assumed to exist in DB)
```json
{
    "id": 1,
    "name": "USER_MANAGE",
    "description": "Manage users and permissions"
},
{
    "id": 2,
    "name": "USER_READ",
    "description": "Read user information"
},
{
    "id": 3,
    "name": "PRODUCT_MANAGE",
    "description": "Manage products"
}
```

### Roles (assumed to exist in DB)
```json
{
    "id": 1,
    "name": "ADMIN",
    "permissions": ["USER_MANAGE", "PRODUCT_MANAGE"]
},
{
    "id": 2,
    "name": "USER",
    "permissions": ["USER_READ"]
},
{
    "id": 3,
    "name": "MANAGER",
    "permissions": ["USER_READ", "PRODUCT_MANAGE"]
}
```

---

## Common Error Responses

### 401 Unauthorized
```json
{
    "timestamp": "2026-01-02T10:30:00",
    "status": 401,
    "error": "Unauthorized",
    "message": "Full authentication is required to access this resource",
    "path": "/api/admin/users/permissions"
}
```

### 403 Forbidden
```json
{
    "timestamp": "2026-01-02T10:30:00",
    "status": 403,
    "error": "Forbidden",
    "message": "Access Denied",
    "path": "/api/admin/users/permissions"
}
```

### 404 Not Found
```json
{
    "timestamp": "2026-01-02T10:30:00",
    "status": 404,
    "error": "Not Found",
    "message": "User not found with id: 999",
    "path": "/api/admin/users/permissions"
}
```

### 400 Bad Request
```json
{
    "timestamp": "2026-01-02T10:30:00",
    "status": 400,
    "error": "Bad Request",
    "message": "Invalid request body",
    "path": "/api/admin/users/permissions"
}
```

---

## Postman Collection JSON

You can import this JSON directly into Postman:

```json
{
    "info": {
        "name": "UserManagement API",
        "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
    },
    "item": [
        {
            "name": "Assign Permission to User",
            "request": {
                "method": "POST",
                "header": [
                    {
                        "key": "Authorization",
                        "value": "Bearer {{token}}"
                    },
                    {
                        "key": "Content-Type",
                        "value": "application/json"
                    }
                ],
                "body": {
                    "mode": "raw",
                    "raw": "{\n    \"userId\": {{userId}},\n    \"permissionId\": {{permissionId}}\n}"
                },
                "url": {
                    "raw": "{{baseUrl}}/api/admin/users/permissions",
                    "host": ["{{baseUrl}}"],
                    "path": ["api", "admin", "users", "permissions"]
                }
            }
        },
        {
            "name": "Remove Permission from User",
            "request": {
                "method": "DELETE",
                "header": [
                    {
                        "key": "Authorization",
                        "value": "Bearer {{token}}"
                    }
                ],
                "url": {
                    "raw": "{{baseUrl}}/api/admin/users/{{userId}}/permissions/{{permissionId}}",
                    "host": ["{{baseUrl}}"],
                    "path": ["api", "admin", "users", "{{userId}}", "permissions", "{{permissionId}}"]
                }
            }
        },
        {
            "name": "Activate Permission",
            "request": {
                "method": "PATCH",
                "header": [
                    {
                        "key": "Authorization",
                        "value": "Bearer {{token}}"
                    }
                ],
                "url": {
                    "raw": "{{baseUrl}}/api/admin/users/{{userId}}/permissions/{{permissionId}}/activate",
                    "host": ["{{baseUrl}}"],
                    "path": ["api", "admin", "users", "{{userId}}", "permissions", "{{permissionId}}", "activate"]
                }
            }
        },
        {
            "name": "Deactivate Permission",
            "request": {
                "method": "PATCH",
                "header": [
                    {
                        "key": "Authorization",
                        "value": "Bearer {{token}}"
                    }
                ],
                "url": {
                    "raw": "{{baseUrl}}/api/admin/users/{{userId}}/permissions/{{permissionId}}/deactivate",
                    "host": ["{{baseUrl}}"],
                    "path": ["api", "admin", "users", "{{userId}}", "permissions", "{{permissionId}}", "deactivate"]
                }
            }
        },
        {
            "name": "Assign Role to User",
            "request": {
                "method": "POST",
                "header": [
                    {
                        "key": "Authorization",
                        "value": "Bearer {{token}}"
                    }
                ],
                "url": {
                    "raw": "{{baseUrl}}/api/admin/users/{{userId}}/role/{{roleId}}",
                    "host": ["{{baseUrl}}"],
                    "path": ["api", "admin", "users", "{{userId}}", "role", "{{roleId}}"]
                }
            }
        }
    ]
}
```

---

## Notes
- Make sure your Spring Boot application is running on port 8080
- Ensure the database is populated with test users, permissions, and roles
- The JWT token expires after 24 hours (86400000 ms as per configuration)
- All endpoints require the `USER_MANAGE` authority

