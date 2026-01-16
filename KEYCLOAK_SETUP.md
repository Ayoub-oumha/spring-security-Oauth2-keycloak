# 🔐 Keycloak Integration Guide

## Quick Start with Docker Compose

```bash
docker-compose up -d
```

This will start:
- MySQL on port 3306
- Keycloak on port 8180

## Keycloak Configuration Steps

### 1. Access Keycloak Admin Console
- URL: http://localhost:8180
- Username: `admin`
- Password: `admin`

### 2. Create Realm
1. Click on dropdown "Keycloak" (top left)
2. Click "Create Realm"
3. Name: `tricol-realm`
4. Click "Create"

### 3. Create Client
1. Go to "Clients" → "Create client"
2. **General Settings:**
   - Client type: `OpenID Connect`
   - Client ID: `tricol-app`
   - Click "Next"

3. **Capability config:**
   - Client authentication: `ON`
   - Authorization: `OFF`
   - Authentication flow: Check all (Standard flow, Direct access grants, etc.)
   - Click "Next"

4. **Login settings:**
   - Valid redirect URIs: `http://localhost:8080/*`
   - Valid post logout redirect URIs: `http://localhost:8080/*`
   - Web origins: `http://localhost:8080`
   - Click "Save"

5. **Get Client Secret:**
   - Go to "Credentials" tab
   - Copy the "Client secret"
   - Update `application.properties`: `keycloak.credentials.secret=YOUR_CLIENT_SECRET`

### 4. Create Roles
1. Go to "Realm roles" → "Create role"
2. Create these roles:
   - `ADMIN`
   - `RESPONSABLE_ACHATS`
   - `MAGASINIER`
   - `CHEF_ATELIER`

### 5. Create Users
1. Go to "Users" → "Create new user"
2. **Example Admin User:**
   - Username: `admin`
   - Email: `admin@tricol.com`
   - First name: `Admin`
   - Last name: `User`
   - Email verified: `ON`
   - Click "Create"

3. **Set Password:**
   - Go to "Credentials" tab
   - Click "Set password"
   - Password: `admin123`
   - Temporary: `OFF`
   - Click "Save"

4. **Assign Roles:**
   - Go to "Role mapping" tab
   - Click "Assign role"
   - Select `ADMIN`
   - Click "Assign"

5. **Repeat for other users** with different roles

## API Testing

### 1. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

Response:
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expires_in": 300,
  "refresh_expires_in": 1800,
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer"
}
```

### 2. Access Protected Endpoint
```bash
curl -X GET http://localhost:8080/api/test/admin \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### 3. Get User Info
```bash
curl -X GET http://localhost:8080/api/test/user \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### 4. Refresh Token
```bash
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "YOUR_REFRESH_TOKEN"
  }'
```

### 5. Logout
```bash
curl -X POST http://localhost:8080/api/auth/logout \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "YOUR_REFRESH_TOKEN"
  }'
```

## Role-Based Endpoints

| Endpoint | Required Role | Description |
|----------|--------------|-------------|
| `/api/test/public` | None | Public access |
| `/api/test/user` | Any authenticated | User info |
| `/api/test/admin` | ADMIN | Admin only |
| `/api/test/responsable` | RESPONSABLE_ACHATS | Responsable only |
| `/api/test/magasinier` | MAGASINIER | Magasinier only |
| `/api/test/chef` | CHEF_ATELIER | Chef only |

## Configuration Files

### application.properties
```properties
# Keycloak Config
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8180/realms/tricol-realm
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:8180/realms/tricol-realm/protocol/openid-connect/certs

keycloak.realm=tricol-realm
keycloak.auth-server-url=http://localhost:8180
keycloak.resource=tricol-app
keycloak.credentials.secret=YOUR_CLIENT_SECRET_HERE
keycloak.use-resource-role-mappings=true
keycloak.bearer-only=true
```

## Troubleshooting

### Issue: "Invalid token issuer"
- Verify `issuer-uri` matches your Keycloak realm URL
- Check Keycloak is running on port 8180

### Issue: "403 Forbidden"
- Verify user has the required role
- Check role mapping in Keycloak
- Ensure role prefix "ROLE_" is handled correctly

### Issue: "Connection refused"
- Ensure Keycloak is running: `docker ps`
- Check Keycloak logs: `docker logs tricol-keycloak`

## Production Considerations

1. **Use HTTPS** in production
2. **Change default passwords**
3. **Configure proper CORS**
4. **Set up Keycloak with PostgreSQL** (not H2)
5. **Enable token encryption**
6. **Configure session timeouts**
7. **Set up proper logging and monitoring**

## Additional Resources

- [Keycloak Documentation](https://www.keycloak.org/documentation)
- [Spring Security OAuth2](https://spring.io/projects/spring-security-oauth)
- [JWT.io](https://jwt.io/) - Decode and verify JWT tokens
