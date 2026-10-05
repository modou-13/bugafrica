# Bug Africa API

Backend de la plateforme d'entraide pour développeurs africains.
Stack : Java 21, Spring Boot 3.3, Spring Security (JWT), Spring Data JPA, MySQL 8, Flyway, Docker.

## Démarrage rapide (Docker)

```bash
cp .env.example .env
# Édite .env : mots de passe MySQL + JWT_SECRET (openssl rand -base64 48)
docker compose up --build
```

- API : http://localhost:8080
- Swagger : http://localhost:8080/swagger-ui.html
- Santé : http://localhost:8080/actuator/health

Le schéma de la base est créé automatiquement par Flyway (`src/main/resources/db/migration`).

## Développement sans Docker

Avec un MySQL local (base `bugafrica`, utilisateur `root` / mot de passe `root` par défaut) :

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Les variables `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` permettent de changer la connexion.

## Endpoints

| Méthode | Route | Accès | Rôle |
|---|---|---|---|
| POST | `/api/auth/register` | public | Inscription |
| POST | `/api/auth/login` | public | Connexion, renvoie le JWT |
| GET | `/api/users/me` | connecté | Mon profil |
| GET | `/api/tags` | public | Liste des tags |
| GET | `/api/bugs?q=&tag=&solved=&page=&size=` | public | Recherche paginée |
| GET | `/api/bugs/{id}` | public | Détail d'un bug |
| POST | `/api/bugs` | connecté | Poster un bug |
| PUT | `/api/bugs/{id}` | auteur / admin | Modifier |
| DELETE | `/api/bugs/{id}` | auteur / admin | Supprimer |
| GET | `/api/bugs/{id}/answers` | public | Réponses d'un bug |
| POST | `/api/bugs/{id}/answers` | connecté | Répondre |
| POST | `/api/answers/{id}/accept` | auteur du bug / admin | Marquer comme solution (+15 réputation) |

Authentification : en-tête `Authorization: Bearer <token>`.

## Exemple

```bash
curl -X POST localhost:8080/api/auth/register -H "Content-Type: application/json" \
  -d '{"username":"awa","email":"awa@mail.com","password":"motdepasse123","country":"Sénégal"}'

curl -X POST localhost:8080/api/bugs -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"title":"Erreur 401 API Wave","description":"Mon appel renvoie 401...","codeSnippet":"fetch(...)","tags":["wave","api-rest"]}'
```

## Sécurité mise en place

- Mots de passe hachés avec BCrypt, jamais renvoyés par l'API
- JWT signé (HS256), secret obligatoire de 32 caractères minimum, l'application refuse de démarrer sinon
- API stateless, CORS restreint aux origines déclarées
- Validation stricte de toutes les entrées, requêtes JPA paramétrées (pas d'injection SQL)
- Erreurs au format JSON uniforme, jamais de stack trace exposée
- Secrets uniquement via variables d'environnement, `.env` ignoré par git
- Docker : image finale sans root, MySQL non exposé à l'extérieur
- Hibernate ne modifie jamais le schéma (Flyway uniquement)

## Performance

Pool de connexions Hikari, pagination plafonnée à 50, index SQL sur les colonnes filtrées,
`open-in-view` désactivé, chargement par lots (anti N+1), compression des réponses JSON,
réputation mise à jour de façon atomique en base.

## Prochaines étapes

Votes sur les réponses, commentaires, limitation des tentatives de connexion, tests d'intégration.
