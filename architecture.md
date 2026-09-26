# DocuTrust Architecture

## 1. System flow

```text
Browser
  -> React/Vite dashboard
  -> Nginx reverse proxy
  -> Spring Boot REST API
      -> security/authentication boundary
      -> document service
      -> JPA repositories
      -> PostgreSQL or H2 fallback
      -> analysis provider abstraction
          -> mock provider
          -> OpenRouter provider
          -> future local/cloud providers
```

## 2. Repository layout

```text
docutrust/
├── backend/
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/main/
│       ├── java/com/docutrust/api/
│       │   ├── analysis/
│       │   ├── config/
│       │   └── document/
│       └── resources/
├── frontend/
│   ├── package.json
│   ├── Dockerfile
│   ├── nginx.conf
│   └── src/
├── docker-compose.yml
├── .env.example
├── README.md
├── prd.md
├── architecture.md
├── rules.md
├── design.md
├── tasks.md
└── memory.md
```

## 3. Technology choices

- Java 21 and Spring Boot 3
- Maven
- Spring Web, Validation, Security, Data JPA, and Actuator
- PostgreSQL 16 for deployed local/self-hosted environments
- H2 for secret-free local fallback
- React 18 and Vite
- Nginx for static serving and API proxying
- Docker Compose for the self-hosted baseline

## 4. API boundaries

- `POST /api/v1/documents`: submit extracted text
- `GET /api/v1/documents`: list documents
- `GET /api/v1/documents/{id}`: retrieve one document
- `GET /api/v1/documents/{id}/status`: retrieve status
- `GET /api/v1/documents/{id}/findings`: retrieve findings
- `GET /actuator/health`: service health

## 5. Provider boundary

`LlmProvider` is the stable application interface. Providers receive a document and return validated finding drafts. Provider implementations must not own persistence or HTTP response formatting.

The provider selector is configuration-driven. `mock` is the default. `openrouter` requires an API key and an explicitly selected model.

## 6. Deployment

Compose runs PostgreSQL, the API, and the frontend. The API waits for the PostgreSQL health check. Nginx serves the compiled frontend and proxies `/api` to the API service. Production secrets must come from an external secret manager or deployment environment.
