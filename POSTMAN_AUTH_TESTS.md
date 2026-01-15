# Authentication API - Tests Postman

## Base Configuration
- **Base URL**: `http://localhost:8080`
- **Content-Type**: `application/json`
- **Endpoints**: `/api/auth/register` et `/api/auth/login`

---

## 1. REGISTER - Créer un nouveau compte utilisateur

### Endpoint
**Method**: `POST`  
**URL**: `{{baseUrl}}/api/auth/register`

### Headers
```
Content-Type: application/json
```

### Request Body - Exemple 1 (Admin)
```json
{
    "username": "admin",
    "email": "admin@tricol.com",
    "password": "admin123",
    "fullName": "Administrator Tricol"
}
```

### Request Body - Exemple 2 (User Normal)
```json
{
    "username": "john.doe",
    "email": "john.doe@tricol.com",
    "password": "password123",
    "fullName": "John Doe"
}
```

### Request Body - Exemple 3 (Manager)
```json
{
    "username": "marie.manager",
    "email": "marie.manager@tricol.com",
    "password": "securepass456",
    "fullName": "Marie Manager"
}
```

### Request Body - Exemple 4 (Fournisseur)
```json
{
    "username": "supplier.xyz",
    "email": "supplier@xyz.com",
    "password": "supplier789",
    "fullName": "XYZ Supplier Company"
}
```

### Success Response (201 Created ou 200 OK)
```json
{
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTY0MDk2NjQwMCwiZXhwIjoxNjQxMDUyODAwfQ.abc123def456",
    "tokenType": "Bearer",
    "userId": 1,
    "username": "admin",
    "email": "admin@tricol.com",
    "role": "USER"
}
```

### Error Response - Username déjà existant (400 Bad Request)
```json
{
    "timestamp": "2026-01-02T10:30:00",
    "status": 400,
    "error": "Bad Request",
    "message": "Username already exists",
    "path": "/api/auth/register"
}
```

### Error Response - Email invalide (400 Bad Request)
```json
{
    "timestamp": "2026-01-02T10:30:00",
    "status": 400,
    "error": "Bad Request",
    "message": "Invalid email format",
    "path": "/api/auth/register"
}
```

### Error Response - Mot de passe trop court (400 Bad Request)
```json
{
    "timestamp": "2026-01-02T10:30:00",
    "status": 400,
    "error": "Bad Request",
    "message": "Password must be at least 6 characters",
    "path": "/api/auth/register"
}
```

### Validation Rules
- **username**: 
  - Obligatoire (`@NotBlank`)
  - Entre 3 et 50 caractères (`@Size(min = 3, max = 50)`)
  - Doit être unique
  
- **email**: 
  - Obligatoire (`@NotBlank`)
  - Format email valide (`@Email`)
  - Doit être unique
  
- **password**: 
  - Obligatoire (`@NotBlank`)
  - Minimum 6 caractères (`@Size(min = 6)`)
  
- **fullName**: 
  - Optionnel

---

## 2. LOGIN - Se connecter avec un compte existant

### Endpoint
**Method**: `POST`  
**URL**: `{{baseUrl}}/api/auth/login`

### Headers
```
Content-Type: application/json
```

### Request Body - Exemple 1 (Admin)
```json
{
    "username": "admin",
    "password": "admin123"
}
```

### Request Body - Exemple 2 (User Normal)
```json
{
    "username": "john.doe",
    "password": "password123"
}
```

### Request Body - Exemple 3 (Manager)
```json
{
    "username": "marie.manager",
    "password": "securepass456"
}
```

### Success Response (200 OK)
```json
{
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTY0MDk2NjQwMCwiZXhwIjoxNjQxMDUyODAwfQ.abc123def456",
    "tokenType": "Bearer",
    "userId": 1,
    "username": "admin",
    "email": "admin@tricol.com",
    "role": "ADMIN"
}
```

### Error Response - Identifiants incorrects (401 Unauthorized)
```json
{
    "timestamp": "2026-01-02T10:30:00",
    "status": 401,
    "error": "Unauthorized",
    "message": "Invalid username or password",
    "path": "/api/auth/login"
}
```

### Error Response - Champs manquants (400 Bad Request)
```json
{
    "timestamp": "2026-01-02T10:30:00",
    "status": 400,
    "error": "Bad Request",
    "message": "Username and password are required",
    "path": "/api/auth/login"
}
```

### Validation Rules
- **username**: Obligatoire (`@NotBlank`)
- **password**: Obligatoire (`@NotBlank`)

---

## 3. Collection Postman Complète (JSON)

Vous pouvez importer ce JSON directement dans Postman:

```json
{
    "info": {
        "name": "Tricol Authentication API",
        "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
    },
    "item": [
        {
            "name": "Register Admin",
            "request": {
                "method": "POST",
                "header": [
                    {
                        "key": "Content-Type",
                        "value": "application/json"
                    }
                ],
                "body": {
                    "mode": "raw",
                    "raw": "{\n    \"username\": \"admin\",\n    \"email\": \"admin@tricol.com\",\n    \"password\": \"admin123\",\n    \"fullName\": \"Administrator Tricol\"\n}"
                },
                "url": {
                    "raw": "{{baseUrl}}/api/auth/register",
                    "host": ["{{baseUrl}}"],
                    "path": ["api", "auth", "register"]
                }
            },
            "event": [
                {
                    "listen": "test",
                    "script": {
                        "exec": [
                            "if (pm.response.code === 200) {",
                            "    var jsonData = pm.response.json();",
                            "    pm.environment.set(\"token\", jsonData.accessToken);",
                            "    pm.environment.set(\"userId\", jsonData.userId);",
                            "    console.log(\"Token saved: \" + jsonData.accessToken);",
                            "}"
                        ]
                    }
                }
            ]
        },
        {
            "name": "Register User",
            "request": {
                "method": "POST",
                "header": [
                    {
                        "key": "Content-Type",
                        "value": "application/json"
                    }
                ],
                "body": {
                    "mode": "raw",
                    "raw": "{\n    \"username\": \"john.doe\",\n    \"email\": \"john.doe@tricol.com\",\n    \"password\": \"password123\",\n    \"fullName\": \"John Doe\"\n}"
                },
                "url": {
                    "raw": "{{baseUrl}}/api/auth/register",
                    "host": ["{{baseUrl}}"],
                    "path": ["api", "auth", "register"]
                }
            }
        },
        {
            "name": "Register Manager",
            "request": {
                "method": "POST",
                "header": [
                    {
                        "key": "Content-Type",
                        "value": "application/json"
                    }
                ],
                "body": {
                    "mode": "raw",
                    "raw": "{\n    \"username\": \"marie.manager\",\n    \"email\": \"marie.manager@tricol.com\",\n    \"password\": \"securepass456\",\n    \"fullName\": \"Marie Manager\"\n}"
                },
                "url": {
                    "raw": "{{baseUrl}}/api/auth/register",
                    "host": ["{{baseUrl}}"],
                    "path": ["api", "auth", "register"]
                }
            }
        },
        {
            "name": "Login Admin",
            "request": {
                "method": "POST",
                "header": [
                    {
                        "key": "Content-Type",
                        "value": "application/json"
                    }
                ],
                "body": {
                    "mode": "raw",
                    "raw": "{\n    \"username\": \"admin\",\n    \"password\": \"admin123\"\n}"
                },
                "url": {
                    "raw": "{{baseUrl}}/api/auth/login",
                    "host": ["{{baseUrl}}"],
                    "path": ["api", "auth", "login"]
                }
            },
            "event": [
                {
                    "listen": "test",
                    "script": {
                        "exec": [
                            "if (pm.response.code === 200) {",
                            "    var jsonData = pm.response.json();",
                            "    pm.environment.set(\"token\", jsonData.accessToken);",
                            "    pm.environment.set(\"userId\", jsonData.userId);",
                            "    console.log(\"Token saved: \" + jsonData.accessToken);",
                            "    console.log(\"User ID: \" + jsonData.userId);",
                            "}"
                        ]
                    }
                }
            ]
        },
        {
            "name": "Login User",
            "request": {
                "method": "POST",
                "header": [
                    {
                        "key": "Content-Type",
                        "value": "application/json"
                    }
                ],
                "body": {
                    "mode": "raw",
                    "raw": "{\n    \"username\": \"john.doe\",\n    \"password\": \"password123\"\n}"
                },
                "url": {
                    "raw": "{{baseUrl}}/api/auth/login",
                    "host": ["{{baseUrl}}"],
                    "path": ["api", "auth", "login"]
                }
            }
        },
        {
            "name": "Login Manager",
            "request": {
                "method": "POST",
                "header": [
                    {
                        "key": "Content-Type",
                        "value": "application/json"
                    }
                ],
                "body": {
                    "mode": "raw",
                    "raw": "{\n    \"username\": \"marie.manager\",\n    \"password\": \"securepass456\"\n}"
                },
                "url": {
                    "raw": "{{baseUrl}}/api/auth/login",
                    "host": ["{{baseUrl}}"],
                    "path": ["api", "auth", "login"]
                }
            }
        },
        {
            "name": "Login - Invalid Credentials",
            "request": {
                "method": "POST",
                "header": [
                    {
                        "key": "Content-Type",
                        "value": "application/json"
                    }
                ],
                "body": {
                    "mode": "raw",
                    "raw": "{\n    \"username\": \"admin\",\n    \"password\": \"wrongpassword\"\n}"
                },
                "url": {
                    "raw": "{{baseUrl}}/api/auth/login",
                    "host": ["{{baseUrl}}"],
                    "path": ["api", "auth", "login"]
                }
            }
        }
    ],
    "variable": [
        {
            "key": "baseUrl",
            "value": "http://localhost:8080"
        }
    ]
}
```

---

## 4. Variables d'Environnement Postman

Créez un environnement nommé "Tricol Development" avec ces variables:

| Variable | Initial Value | Current Value |
|----------|--------------|---------------|
| baseUrl | http://localhost:8080 | http://localhost:8080 |
| token | | (automatiquement rempli après login) |
| userId | | (automatiquement rempli après login) |

---

## 5. Scénarios de Test Complets

### Scénario 1: Nouveau Utilisateur
1. **Register** → `POST /api/auth/register` avec les données utilisateur
2. Vérifier que le token est retourné
3. Utiliser ce token pour les requêtes authentifiées

### Scénario 2: Utilisateur Existant
1. **Login** → `POST /api/auth/login` avec username et password
2. Le token JWT est automatiquement sauvegardé dans l'environnement
3. Utiliser `{{token}}` dans les headers des autres requêtes: `Authorization: Bearer {{token}}`

### Scénario 3: Test de Validation
1. **Register sans email** → Erreur 400
2. **Register avec mot de passe court** → Erreur 400
3. **Login avec mauvais credentials** → Erreur 401
4. **Login sans username** → Erreur 400

---

## 6. Scripts Postman Automatiques

### Script pour sauvegarder automatiquement le token après login/register

Ajoutez ce script dans l'onglet "Tests" de votre requête login:

```javascript
// Test si la réponse est OK
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

// Test si le token existe
pm.test("Token exists", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.accessToken).to.exist;
});

// Sauvegarder le token dans l'environnement
if (pm.response.code === 200) {
    var jsonData = pm.response.json();
    pm.environment.set("token", jsonData.accessToken);
    pm.environment.set("userId", jsonData.userId);
    pm.environment.set("username", jsonData.username);
    console.log("✓ Token saved successfully");
    console.log("✓ User ID: " + jsonData.userId);
    console.log("✓ Username: " + jsonData.username);
}

// Test si l'email est valide
pm.test("Email is valid", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.email).to.match(/^[^\s@]+@[^\s@]+\.[^\s@]+$/);
});
```

---

## 7. Cas de Test Spécifiques

### Test 1: Register avec username trop court
```json
{
    "username": "ab",
    "email": "test@tricol.com",
    "password": "password123",
    "fullName": "Test User"
}
```
**Résultat attendu**: 400 Bad Request - "Username must be between 3 and 50 characters"

### Test 2: Register avec email invalide
```json
{
    "username": "testuser",
    "email": "invalid-email",
    "password": "password123",
    "fullName": "Test User"
}
```
**Résultat attendu**: 400 Bad Request - "Invalid email format"

### Test 3: Register avec mot de passe trop court
```json
{
    "username": "testuser",
    "email": "test@tricol.com",
    "password": "12345",
    "fullName": "Test User"
}
```
**Résultat attendu**: 400 Bad Request - "Password must be at least 6 characters"

### Test 4: Login avec username inexistant
```json
{
    "username": "nonexistent",
    "password": "password123"
}
```
**Résultat attendu**: 401 Unauthorized - "Invalid username or password"

### Test 5: Login avec champs vides
```json
{
    "username": "",
    "password": ""
}
```
**Résultat attendu**: 400 Bad Request - "Username and password are required"

---

## 8. Utiliser le Token JWT pour les autres endpoints

Après un login réussi, utilisez le token pour accéder aux endpoints protégés:

### Exemple avec UserManagement
```
GET /api/admin/users/permissions
Authorization: Bearer {{token}}
```

Le token est valide pendant **24 heures** (86400000 ms).

---

## 9. Données de Test Recommandées

### 5 Utilisateurs de Test

```json
// Admin
{
    "username": "admin",
    "email": "admin@tricol.com",
    "password": "admin123",
    "fullName": "Admin Tricol"
}

// Manager
{
    "username": "manager",
    "email": "manager@tricol.com",
    "password": "manager123",
    "fullName": "Manager Tricol"
}

// User Standard
{
    "username": "user",
    "email": "user@tricol.com",
    "password": "user123",
    "fullName": "User Standard"
}

// Fournisseur
{
    "username": "supplier",
    "email": "supplier@tricol.com",
    "password": "supplier123",
    "fullName": "Supplier Company"
}

// Gestionnaire Stock
{
    "username": "stock.manager",
    "email": "stock@tricol.com",
    "password": "stock123",
    "fullName": "Stock Manager"
}
```

---

## 10. Troubleshooting

### Problème: "Cannot connect to server"
**Solution**: Vérifiez que votre application Spring Boot est démarrée sur le port 8080

### Problème: "Token expired"
**Solution**: Reconnectez-vous pour obtenir un nouveau token

### Problème: "403 Forbidden"
**Solution**: Votre utilisateur n'a pas les permissions nécessaires. Connectez-vous avec un compte admin.

### Problème: "Username already exists"
**Solution**: Utilisez un username différent ou testez le login avec les credentials existants

---

## Notes Importantes
- Le token JWT expire après **24 heures**
- Les mots de passe doivent contenir au minimum **6 caractères**
- Les usernames doivent contenir entre **3 et 50 caractères**
- L'email doit être au format valide
- Le token est également stocké dans un cookie HTTP-only (géré automatiquement)

