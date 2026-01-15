# Guide de Diagnostic - Erreur CI/CD "exit code 1"

## 🔍 Étapes de diagnostic

### 1. Vérifier les logs détaillés dans GitHub Actions

Allez sur GitHub → votre repo → onglet **Actions** → cliquez sur le workflow qui a échoué

Cherchez l'étape qui a échoué et notez le message d'erreur exact.

---

## ❌ Erreurs communes et solutions

### Erreur 1: "Error: Username and password required"
**Cause:** Les secrets ne sont pas configurés ou mal nommés

**Solution:**
1. Vérifiez que vous avez bien créé les secrets dans GitHub
2. Les noms DOIVENT être exactement:
   - `DOCKERHUB_USERNAME` (pas DOCKER_USERNAME)
   - `DOCKERHUB_TOKEN` (pas DOCKERHUB_PASSWORD)

### Erreur 2: "denied: requested access to the resource is denied"
**Cause:** Token Docker Hub invalide ou permissions insuffisantes

**Solution:**
1. Allez sur https://hub.docker.com/settings/security
2. Supprimez l'ancien token
3. Créez un nouveau token avec permissions **Read, Write, Delete**
4. Mettez à jour le secret `DOCKERHUB_TOKEN` dans GitHub

### Erreur 3: "repository does not exist"
**Cause:** Le nom du repository Docker n'existe pas

**Solution:**
Le repository sera créé automatiquement lors du premier push.
Assurez-vous que `DOCKERHUB_USERNAME` contient bien votre username exact (sensible à la casse).

### Erreur 4: "Maven build failed"
**Cause:** Problème de compilation Java

**Solution:**
Vérifiez que le projet compile localement:
```cmd
mvn clean package -DskipTests
```

Si ça échoue localement, il faut corriger les erreurs de compilation d'abord.

### Erreur 5: "ERROR: failed to solve: failed to compute cache key"
**Cause:** Problème avec Docker Buildx ou le Dockerfile

**Solution:**
Utiliser une approche plus simple sans Buildx (voir workflow alternatif ci-dessous)

---

## 🔧 Workflow Alternatif (Plus Simple)

Si le workflow actuel ne fonctionne pas, essayez cette version ultra-simple:

```yaml
name: CI - Build & Docker Push

on:
  push:
    branches:
      - main

jobs:
  build-and-docker:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout repository
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Build application
        run: mvn clean package -DskipTests

      - name: Docker Login
        run: echo "${{ secrets.DOCKERHUB_TOKEN }}" | docker login -u "${{ secrets.DOCKERHUB_USERNAME }}" --password-stdin

      - name: Build Docker Image
        run: docker build -t ${{ secrets.DOCKERHUB_USERNAME }}/tricol-supplierchain:latest .

      - name: Push Docker Image
        run: docker push ${{ secrets.DOCKERHUB_USERNAME }}/tricol-supplierchain:latest
```

---

## 📋 Checklist de vérification

- [ ] Les secrets sont bien nommés `DOCKERHUB_USERNAME` et `DOCKERHUB_TOKEN`
- [ ] Le token Docker Hub a les permissions Read, Write, Delete
- [ ] Le token Docker Hub n'est pas expiré
- [ ] Votre username Docker Hub est correct (vérifiez sur hub.docker.com)
- [ ] Le Dockerfile existe bien à la racine du projet
- [ ] Le projet compile localement avec `mvn clean package`
- [ ] Vous avez bien push les derniers changements sur GitHub

---

## 🆘 Si rien ne fonctionne

1. **Supprimez les secrets actuels dans GitHub**
2. **Créez un nouveau token Docker Hub**
3. **Recréez les secrets avec les bons noms**
4. **Utilisez le workflow alternatif ci-dessus**
5. **Faites un nouveau commit et push**

---

## 📞 Message d'erreur à partager

Si le problème persiste, partagez le message d'erreur exact depuis:
GitHub → Actions → [votre workflow] → cliquez sur l'étape qui a échoué → copiez le message d'erreur complet

