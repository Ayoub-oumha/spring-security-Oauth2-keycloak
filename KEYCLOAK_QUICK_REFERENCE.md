# 🚀 Keycloak Quick Reference

## Files Created

### Configuration
- `KeycloakSecurityConfig.java` - Security configuration with JWT
- `RestTemplateConfig.java` - HTTP client configuration
- `application.properties` - Updated with Keycloak settings
- `docker-compose.yml` - Keycloak + MySQL setup

### Services & Controllers
- `KeycloakService.java` - Login, refresh, logout logic
- `KeycloakAuthController.java` - Auth endpoints
- `TestController.java` - Example protected endpoints

### Documentation
- `KEYCLOAK_SETUP.md` - Complete setup guide
- `Tricol-Keycloak-API.postman_collection.json` - API testing

## Quick Commands

### Start Everything
```bash
# Start Keycloak & MySQL
docker-compose up -d

# Build & Run App
mvn clean package
java -jar target/supplierchain-0.0.1-SNAPSHOT.jar
```

### Test Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### Test Protected Endpoint
```bash
curl -X GET http://localhost:8080/api/test/admin \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## Configuration Checklist

- [ ] Start Keycloak: `docker-compose up -d`
- [ ] Access Keycloak: http://localhost:8180
- [ ] Create realm: `tricol-realm`
- [ ] Create client: `tricol-app` (confidential)
- [ ] Copy client secret to `application.properties`
- [ ] Create roles: ADMIN, RESPONSABLE_ACHATS, MAGASINIER, CHEF_ATELIER
- [ ] Create users and assign roles
- [ ] Test login endpoint
- [ ] Test protected endpoints

## Key Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/auth/login` | Login with username/password | No |
| POST | `/api/auth/refresh` | Refresh access token | No |
| POST | `/api/auth/logout` | Logout user | No |
| GET | `/api/test/user` | Get user info | Yes |
| GET | `/api/test/admin` | Admin only | Yes (ADMIN) |
| GET | `/api/test/responsable` | Responsable only | Yes (RESPONSABLE_ACHATS) |
| GET | `/api/test/magasinier` | Magasinier only | Yes (MAGASINIER) |
| GET | `/api/test/chef` | Chef only | Yes (CHEF_ATELIER) |

## Important Notes

1. **Client Secret**: Get from Keycloak → Clients → tricol-app → Credentials
2. **Token Format**: `Authorization: Bearer <access_token>`
3. **Token Expiry**: Default 5 minutes (configurable in Keycloak)
4. **Refresh Token**: Use to get new access token without re-login
5. **Roles**: Automatically extracted from JWT `realm_access.roles`

## Troubleshooting

### "Invalid token issuer"
→ Check `issuer-uri` in application.properties matches Keycloak realm

### "403 Forbidden"
→ User doesn't have required role, check role mapping in Keycloak

### "Connection refused to Keycloak"
→ Ensure Keycloak is running: `docker ps | grep keycloak`

### "Client secret invalid"
→ Copy correct secret from Keycloak client credentials tab

## Next Steps

1. ✅ Integrate Keycloak with existing controllers
2. ✅ Add role-based access control with `@PreAuthorize`
3. ✅ Test all endpoints with different user roles
4. ✅ Configure CORS for frontend integration
5. ✅ Set up production Keycloak with PostgreSQL
6. ✅ Enable HTTPS in production
7. ✅ Configure token expiration policies
8. ✅ Set up user registration flow in Keycloak
9. ✅ Add email verification
10. ✅ Configure social login (Google, GitHub, etc.)

## Resources

- Keycloak Admin: http://localhost:8180
- API Docs: http://localhost:8080/swagger-ui.html
- Full Guide: [KEYCLOAK_SETUP.md](KEYCLOAK_SETUP.md)
- Postman Collection: Import `Tricol-Keycloak-API.postman_collection.json`
