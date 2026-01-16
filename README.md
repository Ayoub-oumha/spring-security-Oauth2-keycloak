# Tricol Supply Chain Management System

Application de gestion des approvisionnements et des stocks avec Spring Security et JWT.

## 🚀 Quick Start

### Prérequis
- Java 17+
- Maven 3.6+
- Docker & Docker Compose
- MySQL 8.0+ (ou utiliser Docker Compose)

### Démarrage rapide avec Docker Compose
```bash
# Démarrer Keycloak et MySQL
docker-compose up -d

# Compiler et lancer l'application
mvn clean package
java -jar target/supplierchain-0.0.1-SNAPSHOT.jar
```

**Services disponibles:**
- Application: http://localhost:8080
- Keycloak: http://localhost:8180 (admin/admin)
- MySQL: localhost:3306

## 🐳 Docker

### Construction de l'image Docker
```bash
docker build -t tricol-supplierchain:latest .
```

### Exécution du conteneur
Assurez-vous que MySQL est en cours d'exécution sur votre machine locale, puis:

```bash
docker run -d -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/team_tricol_supplier_chain_security?createDatabaseIfNotExist=true \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=votre_password \
  --name tricol-app \
  tricol-supplierchain:latest
```

L'application sera accessible sur `http://localhost:8080`

### Commandes utiles
```bash
# Voir les logs
docker logs -f tricol-app

# Arrêter le conteneur
docker stop tricol-app

# Supprimer le conteneur
docker rm tricol-app

# Redémarrer le conteneur
docker restart tricol-app
```

## 📦 Pousser l'image vers Docker Hub

```bash
# Se connecter à Docker Hub
docker login

# Tag l'image
docker tag tricol-supplierchain:latest votre-username/tricol-supplierchain:latest

# Push vers Docker Hub
docker push votre-username/tricol-supplierchain:latest
```

## 🔧 Configuration GitHub Actions

### Secrets requis
Configurez les secrets suivants dans votre repository GitHub (Settings → Secrets and variables → Actions):

- `DOCKER_USERNAME`: Votre nom d'utilisateur Docker Hub
- `DOCKER_PASSWORD`: Votre token d'accès Docker Hub

### Créer un token Docker Hub
1. Allez sur https://hub.docker.com/settings/security
2. Cliquez sur "New Access Token"
3. Donnez un nom au token et cliquez sur "Generate"
4. Copiez le token et ajoutez-le comme secret `DOCKER_PASSWORD` dans GitHub

## 🏗️ Build local

```bash
# Compiler le projet
mvn clean package

# Exécuter l'application
java -jar target/supplierchain-0.0.1-SNAPSHOT.jar
```

## 📝 API Endpoints

### Authentication
- `POST /api/auth/register` - Inscription
- `POST /api/auth/login` - Connexion
- `POST /api/auth/refresh` - Refresh token

### Roles
- ADMIN
- RESPONSABLE_ACHATS
- MAGASINIER
- CHEF_ATELIER

## 🔐 Sécurité

L'application utilise:
- **Keycloak** pour l'authentification et l'autorisation OAuth2/OIDC
- Spring Security avec JWT
- Gestion des rôles et permissions
- System d'audit des actions sensibles
- Refresh tokens

### Configuration Keycloak
Voir le guide détaillé: [KEYCLOAK_SETUP.md](KEYCLOAK_SETUP.md)

**Étapes rapides:**
1. Accéder à Keycloak: http://localhost:8180
2. Créer le realm `tricol-realm`
3. Créer le client `tricol-app`
4. Créer les rôles (ADMIN, RESPONSABLE_ACHATS, etc.)
5. Créer des utilisateurs et assigner les rôles
6. Copier le client secret dans `application.properties`

## 🛠️ Technologies

- Spring Boot 3.5.7
- Spring Security + OAuth2 Resource Server
- **Keycloak** (Identity and Access Management)
- JWT (JSON Web Tokens)
- MySQL 8.0
- Liquibase
- Docker & Docker Compose
- GitHub Actions

## 👥 Auteur

Tricol Team
