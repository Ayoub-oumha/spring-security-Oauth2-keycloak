# Workflow de l'Application - Système de Sécurité Tricol

## 📋 Table des Matières
1. [Architecture de Sécurité](#architecture-de-sécurité)
2. [Workflow d'Authentification (Login)](#workflow-dauthentification-login)
3. [Workflow des Permissions](#workflow-des-permissions)
4. [Système de Rôles et Permissions](#système-de-rôles-et-permissions)
5. [Exemples Pratiques](#exemples-pratiques)

---

## 🔐 Architecture de Sécurité

### Composants Principaux

```
┌─────────────────────────────────────────────────────────┐
│                    Application Client                    │
└─────────────────────┬───────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────┐
│              SecurityFilterChain                         │
│  ┌────────────────────────────────────────────────┐    │
│  │  1. JwtAuthenticationFilter                     │    │
│  │  2. AuthenticationManager                       │    │
│  │  3. UserDetailsService                          │    │
│  │  4. PasswordEncoder                             │    │
│  └────────────────────────────────────────────────┘    │
└─────────────────────┬───────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────┐
│              Base de Données                             │
│  ┌──────────┐  ┌──────────┐  ┌──────────────┐         │
│  │  users   │  │  roles   │  │ permissions  │         │
│  └──────────┘  └──────────┘  └──────────────┘         │
└─────────────────────────────────────────────────────────┘
```

---

## 🔑 Workflow d'Authentification (Login)

### Étape par Étape

#### **Étape 1 : L'utilisateur envoie une requête de login**

```http
POST /api/auth/login
Content-Type: application/json

{
    "username": "admin",
    "password": "admin123"
}
```

**Fichiers impliqués :**
- `AuthController.java` - Reçoit la requête
- `LoginRequest.java` - DTO de la requête

---

#### **Étape 2 : Validation des identifiants**

```java
// AuthServiceImpl.java
public LoginResponse login(LoginRequest request) {
    // 1. Authentification via Spring Security
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getUsername(),
            request.getPassword()
        )
    );
}
```

**Ce qui se passe :**
1. `AuthenticationManager` prend les credentials
2. Appelle `CustomUserDetailsService.loadUserByUsername()`
3. `UserRepository` cherche l'utilisateur dans la BD
4. Compare le mot de passe hashé avec `BCryptPasswordEncoder`

**Diagramme :**
```
Client → AuthController → AuthService → AuthenticationManager
                                              ↓
                                    UserDetailsService
                                              ↓
                                       UserRepository
                                              ↓
                                      Base de Données
```

---

#### **Étape 3 : Chargement des permissions**

```java
// CustomUserDetailsService.java
public UserDetails loadUserByUsername(String username) {
    UserApp user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    
    // Récupération des permissions depuis :
    // 1. Les permissions du rôle
    Set<Permission> rolePermissions = user.getRole().getPermissions();
    
    // 2. Les permissions spécifiques de l'utilisateur
    Set<Permission> userPermissions = user.getUserPermissions()
        .stream()
        .map(UserPermission::getPermission)
        .collect(Collectors.toSet());
}
```

**Structure des données :**
```
User (admin)
  └── Role (ADMIN)
       └── Permissions (26 permissions)
            ├── FOURNISSEUR_CREATE
            ├── FOURNISSEUR_READ
            ├── PRODUIT_CREATE
            └── ... (toutes les permissions)
```

---

#### **Étape 4 : Génération du JWT Token**

```java
// JwtService.java
public String generateToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    
    // Extraction des permissions
    Set<String> permissions = userDetails.getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toSet());
    
    // Ajout dans le token
    claims.put("permissions", permissions);
    
    // Génération du JWT
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
        .signWith(getSignInKey(), SignatureAlgorithm.HS256)
        .compact();
}
```

**Structure du JWT Token :**
```json
{
  "header": {
    "alg": "HS256",
    "typ": "JWT"
  },
  "payload": {
    "sub": "admin",
    "permissions": [
      "FOURNISSEUR_CREATE",
      "FOURNISSEUR_READ",
      "PRODUIT_CREATE",
      "USER_MANAGE",
      "..."
    ],
    "iat": 1735862400,
    "exp": 1735948800
  },
  "signature": "..."
}
```

---

#### **Étape 5 : Réponse au client**

```json
{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "username": "admin",
    "email": "admin@tricol.com",
    "role": "ADMIN",
    "permissions": [
        "FOURNISSEUR_CREATE",
        "FOURNISSEUR_READ",
        "PRODUIT_CREATE",
        "..."
    ]
}
```

---

### Diagramme de Séquence Complet - Login

```
┌────────┐     ┌──────────────┐     ┌─────────────┐     ┌──────────────────┐     ┌──────────┐
│ Client │     │ AuthController│     │ AuthService │     │ UserDetailsService│     │    DB    │
└────┬───┘     └──────┬───────┘     └──────┬──────┘     └────────┬─────────┘     └────┬─────┘
     │                │                     │                     │                     │
     │ POST /login    │                     │                     │                     │
     │───────────────>│                     │                     │                     │
     │                │  authenticate()     │                     │                     │
     │                │────────────────────>│                     │                     │
     │                │                     │ loadUserByUsername()│                     │
     │                │                     │────────────────────>│                     │
     │                │                     │                     │  findByUsername()   │
     │                │                     │                     │────────────────────>│
     │                │                     │                     │<────────────────────│
     │                │                     │                     │   User + Role +     │
     │                │                     │                     │    Permissions      │
     │                │                     │<────────────────────│                     │
     │                │                     │  UserDetails        │                     │
     │                │                     │                     │                     │
     │                │  generateToken()    │                     │                     │
     │                │────────────────────>│                     │                     │
     │                │<────────────────────│                     │                     │
     │                │   JWT Token         │                     │                     │
     │<───────────────│                     │                     │                     │
     │  LoginResponse │                     │                     │                     │
     │  with Token    │                     │                     │                     │
```

---

## 🛡️ Workflow des Permissions

### Comment les Permissions Sont Vérifiées

#### **1. Requête Authentifiée**

Chaque requête après le login doit inclure le JWT token :

```http
GET /api/produits
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

#### **2. Filtrage par JwtAuthenticationFilter**

```java
// JwtAuthenticationFilter.java
@Override
protected void doFilterInternal(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain filterChain
) {
    // 1. Extraction du token
    String jwt = extractTokenFromRequest(request);
    
    // 2. Validation du token
    String username = jwtService.extractUsername(jwt);
    
    // 3. Chargement de l'utilisateur
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    
    // 4. Création du contexte de sécurité
    UsernamePasswordAuthenticationToken authToken = 
        new UsernamePasswordAuthenticationToken(
            userDetails, 
            null, 
            userDetails.getAuthorities() // ← LES PERMISSIONS
        );
    
    SecurityContextHolder.getContext().setAuthentication(authToken);
    
    filterChain.doFilter(request, response);
}
```

---

#### **3. Vérification des Permissions sur les Endpoints**

##### **Option 1 : Annotation @PreAuthorize**

```java
// ProduitController.java
@PreAuthorize("hasAuthority('PRODUIT_CREATE')")
@PostMapping
public ResponseEntity<ProduitResponse> createProduit(
    @RequestBody ProduitRequest request
) {
    // Cette méthode ne s'exécute que si l'utilisateur 
    // possède la permission PRODUIT_CREATE
}

@PreAuthorize("hasAuthority('PRODUIT_READ')")
@GetMapping
public ResponseEntity<List<ProduitResponse>> getAllProduits() {
    // Accessible avec la permission PRODUIT_READ
}

@PreAuthorize("hasAuthority('PRODUIT_DELETE')")
@DeleteMapping("/{id}")
public ResponseEntity<Void> deleteProduit(@PathVariable Long id) {
    // Accessible avec la permission PRODUIT_DELETE
}
```

##### **Option 2 : Vérification Programmatique**

```java
// Dans le Service
public void deleteProduct(Long id) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    
    boolean hasPermission = auth.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals("PRODUIT_DELETE"));
    
    if (!hasPermission) {
        throw new AccessDeniedException("Permission refusée");
    }
    
    // Logique de suppression
}
```

---

### Diagramme de Flux - Vérification des Permissions

```
┌────────┐     ┌────────────────┐     ┌──────────────┐     ┌────────────┐
│ Client │     │ JwtAuthFilter  │     │  Controller  │     │  Service   │
└───┬────┘     └───────┬────────┘     └──────┬───────┘     └─────┬──────┘
    │                  │                      │                   │
    │ GET /produits    │                      │                   │
    │ + JWT Token      │                      │                   │
    │─────────────────>│                      │                   │
    │                  │                      │                   │
    │              Valider JWT                │                   │
    │              Extraire Permissions       │                   │
    │              Créer SecurityContext      │                   │
    │                  │                      │                   │
    │                  │──────────────────────>│                   │
    │                  │                      │                   │
    │                  │      Vérifier        │                   │
    │                  │      @PreAuthorize   │                   │
    │                  │      (PRODUIT_READ)  │                   │
    │                  │                      │                   │
    │                  │                  Permission OK?          │
    │                  │                      │                   │
    │                  │                      │ getProduits()     │
    │                  │                      │──────────────────>│
    │                  │                      │<──────────────────│
    │                  │                      │   List<Produit>   │
    │<─────────────────────────────────────────│                   │
    │   Response 200   │                      │                   │
    │                  │                      │                   │
    
    Si permission manquante:
    │<─────────────────────────────────────────│
    │   Response 403   │                      │
    │   Access Denied  │                      │
```

---

## 📊 Système de Rôles et Permissions

### Hiérarchie des Permissions par Rôle

```
┌─────────────────────────────────────────────────────────────┐
│                         ADMIN                                │
│  ✓ Toutes les permissions (26 permissions)                  │
└─────────────────────────────────────────────────────────────┘
                           │
                           │
        ┌──────────────────┼──────────────────┐
        │                  │                  │
        ▼                  ▼                  ▼
┌──────────────┐   ┌──────────────┐   ┌──────────────┐
│ RESPONSABLE  │   │ MAGASINIER   │   │CHEF_ATELIER  │
│   ACHATS     │   │              │   │              │
└──────────────┘   └──────────────┘   └──────────────┘
│ 13 perms     │   │ 8 perms      │   │ 4 perms      │
└──────────────┘   └──────────────┘   └──────────────┘
```

### Matrice des Permissions

| Permission | ADMIN | RESPONSABLE_ACHATS | MAGASINIER | CHEF_ATELIER |
|------------|-------|-------------------|------------|--------------|
| **FOURNISSEUR** | | | | |
| FOURNISSEUR_CREATE | ✅ | ✅ | ❌ | ❌ |
| FOURNISSEUR_UPDATE | ✅ | ✅ | ❌ | ❌ |
| FOURNISSEUR_DELETE | ✅ | ❌ | ❌ | ❌ |
| FOURNISSEUR_READ | ✅ | ✅ | ❌ | ❌ |
| **PRODUIT** | | | | |
| PRODUIT_CREATE | ✅ | ❌ | ❌ | ❌ |
| PRODUIT_UPDATE | ✅ | ❌ | ❌ | ❌ |
| PRODUIT_DELETE | ✅ | ❌ | ❌ | ❌ |
| PRODUIT_READ | ✅ | ✅ | ✅ | ✅ |
| PRODUIT_CONFIGURE_SEUIL | ✅ | ✅ | ❌ | ❌ |
| **COMMANDE** | | | | |
| COMMANDE_CREATE | ✅ | ✅ | ❌ | ❌ |
| COMMANDE_UPDATE | ✅ | ✅ | ❌ | ❌ |
| COMMANDE_VALIDATE | ✅ | ✅ | ❌ | ❌ |
| COMMANDE_CANCEL | ✅ | ✅ | ❌ | ❌ |
| COMMANDE_RECEIVE | ✅ | ❌ | ✅ | ❌ |
| COMMANDE_READ | ✅ | ✅ | ✅ | ❌ |
| **STOCK** | | | | |
| STOCK_READ | ✅ | ✅ | ✅ | ✅ |
| STOCK_VALORISATION | ✅ | ✅ | ❌ | ❌ |
| STOCK_HISTORIQUE | ✅ | ✅ | ✅ | ❌ |
| **BON_SORTIE** | | | | |
| BON_SORTIE_CREATE | ✅ | ❌ | ✅ | ✅ |
| BON_SORTIE_VALIDATE | ✅ | ❌ | ✅ | ❌ |
| BON_SORTIE_CANCEL | ✅ | ❌ | ❌ | ❌ |
| BON_SORTIE_READ | ✅ | ❌ | ✅ | ✅ |
| **GESTION** | | | | |
| USER_MANAGE | ✅ | ❌ | ❌ | ❌ |
| AUDIT_READ | ✅ | ❌ | ❌ | ❌ |

---

## 💡 Exemples Pratiques

### Exemple 1 : Login et Accès aux Produits

#### **Scénario : Un MAGASINIER veut consulter les produits**

**1. Login**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "magasinier",
    "password": "magasinier123"
  }'
```

**Réponse :**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJwZXJtaXNzaW9ucyI6WyJQUk9EVUlUX1JFQUQiLCJDT01NQU5ERV9SRUFEIiwi...",
  "username": "magasinier",
  "email": "magasinier@tricol.com",
  "role": "MAGASINIER",
  "permissions": [
    "PRODUIT_READ",
    "COMMANDE_READ",
    "COMMANDE_RECEIVE",
    "STOCK_READ",
    "STOCK_HISTORIQUE",
    "BON_SORTIE_CREATE",
    "BON_SORTIE_VALIDATE",
    "BON_SORTIE_READ"
  ]
}
```

**2. Accès aux Produits (✅ AUTORISÉ)**
```bash
curl -X GET http://localhost:8080/api/produits \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

**Résultat :** ✅ **200 OK** - Le magasinier possède `PRODUIT_READ`

**3. Tentative de Suppression (❌ REFUSÉ)**
```bash
curl -X DELETE http://localhost:8080/api/produits/1 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

**Résultat :** ❌ **403 Forbidden** - Le magasinier n'a pas `PRODUIT_DELETE`

```json
{
  "timestamp": "2026-01-02T10:30:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied",
  "path": "/api/produits/1"
}
```

---

### Exemple 2 : Workflow Complet - Création de Commande

#### **Acteurs :**
- **RESPONSABLE_ACHATS** : Peut créer et valider les commandes
- **MAGASINIER** : Peut réceptionner les commandes

**Étape 1 : RESPONSABLE_ACHATS crée une commande**

```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "responsable",
    "password": "responsable123"
  }'

# Création de la commande
curl -X POST http://localhost:8080/api/commandes \
  -H "Authorization: Bearer {token_responsable}" \
  -H "Content-Type: application/json" \
  -d '{
    "fournisseurId": 1,
    "lignes": [
      {"produitId": 1, "quantite": 100, "prixUnitaire": 50.0}
    ]
  }'
```

**Résultat :** ✅ **201 Created** - Possède `COMMANDE_CREATE`

**Étape 2 : RESPONSABLE_ACHATS valide la commande**

```bash
curl -X PUT http://localhost:8080/api/commandes/1/validate \
  -H "Authorization: Bearer {token_responsable}"
```

**Résultat :** ✅ **200 OK** - Possède `COMMANDE_VALIDATE`

**Étape 3 : MAGASINIER réceptionne la commande**

```bash
# Login magasinier
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "magasinier",
    "password": "magasinier123"
  }'

# Réception
curl -X PUT http://localhost:8080/api/commandes/1/receive \
  -H "Authorization: Bearer {token_magasinier}"
```

**Résultat :** ✅ **200 OK** - Possède `COMMANDE_RECEIVE`

---

### Exemple 3 : Gestion des Erreurs

#### **Cas 1 : Token Expiré**

```bash
curl -X GET http://localhost:8080/api/produits \
  -H "Authorization: Bearer {expired_token}"
```

**Réponse :**
```json
{
  "timestamp": "2026-01-02T10:35:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "JWT token has expired",
  "path": "/api/produits"
}
```

**Solution :** Se reconnecter pour obtenir un nouveau token

---

#### **Cas 2 : Permission Insuffisante**

```bash
# CHEF_ATELIER essaie de créer une commande
curl -X POST http://localhost:8080/api/commandes \
  -H "Authorization: Bearer {token_chef_atelier}" \
  -H "Content-Type: application/json" \
  -d '{...}'
```

**Réponse :**
```json
{
  "timestamp": "2026-01-02T10:40:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied - Required permission: COMMANDE_CREATE",
  "path": "/api/commandes"
}
```

---

#### **Cas 3 : Token Manquant**

```bash
curl -X GET http://localhost:8080/api/produits
# Sans header Authorization
```

**Réponse :**
```json
{
  "timestamp": "2026-01-02T10:45:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource",
  "path": "/api/produits"
}
```

---

## 🔍 Comment Debugger les Permissions

### 1. Vérifier le Token JWT

Utilisez [jwt.io](https://jwt.io) pour décoder votre token et voir les permissions incluses.

### 2. Logs de Débogage

Ajoutez dans `application.properties` :
```properties
logging.level.org.springframework.security=DEBUG
logging.level.org.tricol.supplierchain.security=DEBUG
```

### 3. Endpoint de Test

```java
@GetMapping("/api/test/my-permissions")
public ResponseEntity<?> getMyPermissions() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    
    Set<String> permissions = auth.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toSet());
    
    return ResponseEntity.ok(Map.of(
        "username", auth.getName(),
        "permissions", permissions
    ));
}
```

---

## 📚 Résumé pour les Étudiants

### Points Clés à Retenir

1. **Login Process**
   - L'utilisateur envoie username + password
   - Spring Security valide les credentials
   - Un JWT token est généré avec les permissions
   - Le token doit être inclus dans chaque requête suivante

2. **JWT Token**
   - Contient : username, permissions, date d'expiration
   - Doit être envoyé dans le header `Authorization: Bearer {token}`
   - Expire après 24h (configurable)

3. **Permissions**
   - Chaque endpoint est protégé par une permission spécifique
   - `@PreAuthorize("hasAuthority('PERMISSION_NAME')")` vérifie les droits
   - Si l'utilisateur n'a pas la permission → 403 Forbidden

4. **Rôles**
   - ADMIN : Accès complet
   - RESPONSABLE_ACHATS : Gestion fournisseurs + commandes + stock
   - MAGASINIER : Réception + bons de sortie + consultation stock
   - CHEF_ATELIER : Consultation produits/stock + création bons de sortie

5. **Sécurité**
   - Mots de passe hashés avec BCrypt
   - Sessions stateless (JWT)
   - Endpoints publics : `/api/auth/**`, `/swagger-ui/**`
   - Tout le reste nécessite authentification

---

## 🎯 Exercices Pratiques

### Exercice 1 : Tester les Différents Rôles
1. Connectez-vous avec chaque rôle
2. Notez les permissions de chaque token
3. Testez l'accès aux différents endpoints

### Exercice 2 : Créer un Nouveau Rôle
1. Ajoutez `COMPTABLE` dans `RoleName.java`
2. Définissez ses permissions dans le seeder
3. Créez un utilisateur comptable
4. Testez les accès

### Exercice 3 : Ajouter une Nouvelle Permission
1. Ajoutez `RAPPORT_GENERATE` dans `PermissionName.java`
2. Assignez-la au rôle ADMIN
3. Créez un endpoint protégé avec cette permission
4. Testez l'accès

---

## 📞 Support

Pour toute question sur ce workflow, référez-vous à :
- `SecurityConfig.java` - Configuration de sécurité
- `JwtAuthenticationFilter.java` - Filtrage des requêtes
- `CustomUserDetailsService.java` - Chargement des utilisateurs
- `DataSeeder.java` - Initialisation des données

**Bonne chance dans vos études ! 🚀**

