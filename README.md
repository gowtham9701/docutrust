# DocuTrust

Document intelligence platform for explainable trust, compliance, privacy, and security findings. The default experience is secret-free: the API uses an in-memory H2 database and a deterministic mock LLM provider.

## Architecture

- **Frontend:** React + Vite single-page dashboard for submitting text, browsing documents, and viewing findings.
- **API:** Spring Boot 3, Java 21, REST under `/api/v1`, Bean Validation, Spring Data JPA, and method-security-ready configuration.
- **Persistence:** H2 in local mode; PostgreSQL via the `postgres` Spring profile and Docker Compose.
- **Analysis:** `LlmProvider` abstraction with deterministic mock mode and an OpenRouter adapter. OpenRouter can expose hosted open-weight/free-tier models behind one OpenAI-compatible API.
- **RBAC seam:** Spring Security and `@EnableMethodSecurity` are enabled. Routes are currently open for MVP demos; replace the authorization rules and add an identity provider before production.

## Local setup

Requirements: Java 21+, Maven 3.9+, Node 20+, and npm.

```bash
cd backend
mvn spring-boot:run
# in another terminal
cd frontend
npm install
npm run dev
```

Open http://localhost:5173. The API runs on http://localhost:8080.

Useful API calls:

```bash
curl -X POST http://localhost:8080/api/v1/documents \
  -H 'Content-Type: application/json' \
  -d '{"fileName":"policy.txt","content":"A vendor may process data for the stated purpose."}'
curl http://localhost:8080/api/v1/documents
curl http://localhost:8080/api/v1/documents/{id}/findings
curl -F 'file=@policy.txt' http://localhost:8080/api/v1/documents/upload
curl http://localhost:8080/api/v1/documents/{id}/audit
```

## Environment variables

| Variable | Default | Purpose |
| --- | --- | --- |
| `PORT` | `8080` | API port |
| `LLM_PROVIDER` | `mock` | Provider selector; mock is safe without credentials |
| `OPENROUTER_API_KEY` | empty | OpenRouter key; required when `LLM_PROVIDER=openrouter` |
| `OPENROUTER_BASE_URL` | `https://openrouter.ai/api/v1` | OpenRouter-compatible API base URL |
| `OPENROUTER_MODEL` | `google/gemma-3-27b-it:free` | Model ID; choose a currently available `:free` model for free-tier usage |
| `DOCUTRUST_APP_URL` | `http://localhost:3000` | Optional OpenRouter application attribution URL |
| `SPRING_PROFILES_ACTIVE` | none | Set to `postgres` to use PostgreSQL |
| `DATABASE_URL` | local Postgres URL | JDBC URL when using the postgres profile |
| `DATABASE_USERNAME` | `docutrust` | PostgreSQL user |
| `DATABASE_PASSWORD` | `docutrust` | PostgreSQL password |
| `OIDC_ISSUER_URI` | empty | Optional external OIDC issuer; when set, all non-health APIs require a JWT |
| `OIDC_AUDIENCES` | empty | Optional audience policy for future claim validation |

## Docker deployment

```bash
docker compose up --build
```

The dashboard is at http://localhost:3000 and the API is at http://localhost:8080. The production Nginx frontend proxies `/api` to the API service. Compose runs PostgreSQL with a named volume and waits for its health check before starting the API. For production, use managed PostgreSQL, secret injection, a real authenticated LLM provider, object storage for files, migrations, TLS, and a non-open RBAC policy.

## Personal free deployment

The simplest public setup is:

1. Deploy the API as a Render Docker web service from [`render.yaml`](./render.yaml).
2. Use a hosted PostgreSQL database such as Neon or Supabase. Render's free web service can sleep, and free database availability/limits vary by region and account.
3. Deploy the frontend as a Render Static Site, Vercel project, or Netlify site.
4. Set the frontend variable `VITE_API_BASE_URL` to the public API URL, for example `https://docutrust-api.onrender.com`.
5. Set the API variable `DOCUTRUST_CORS_ORIGINS` to the exact frontend URL, for example `https://docutrust.vercel.app`.
6. Keep `LLM_PROVIDER=mock` for a zero-secret demo, or configure `LLM_PROVIDER=openrouter` and `OPENROUTER_API_KEY` as a platform secret.

### Current deployment endpoints

| Component | URL / value | Status |
| --- | --- | --- |
| Frontend deployment (Vercel `main` alias) | https://docutrust-git-main-gautams-projects-5965c972.vercel.app | Vercel deployment protection currently redirects to Vercel sign-in |
| Vercel deployment dashboard | https://vercel.com/gautams-projects-5965c972/docutrust/4DQ6VDZiKJaPxpYSUE3UHhzBxE4Y | Private dashboard; requires Vercel account access |
| Backend API (Render) | https://docutrust-yiy1.onrender.com | Health endpoint verified `UP` |
| Backend health | https://docutrust-yiy1.onrender.com/actuator/health | Public health check |
| Neon project | `frosty-pond-86966042` (`production`, AWS us-east-2) | Linked with Neon CLI; live policy matches `neon.ts` |
| Neon JDBC URL | `jdbc:postgresql://ep-frosty-cake-b5ksywk2-pooler.c-7.us-east-2.aws.neon.tech/neondb?sslmode=require` | Host/database only; username and password are intentionally excluded |

The friendly hostname `https://docutrust.vercel.app` currently resolves to a separate medical-records sign-in app, so do not use it as DocuTrust's frontend URL. In Vercel, either disable deployment protection for the intended deployment or assign a verified custom domain, then update `VITE_API_BASE_URL` and the Render CORS origin to match that exact frontend origin.

The deployed Render API currently responds to health checks. A production submission test remained in `ANALYZING` with no findings on the then-running deployment; the local after-commit dispatch fix is verified and must be deployed before considering hosted analysis verified.

### Deployment environment variables

Set these in the Render API service's **Environment** page. Enter credential values directly there as secrets; never put them in this README, Git, or chat.

| Variable | Value or source |
| --- | --- |
| `SPRING_PROFILES_ACTIVE` | `postgres` |
| `DATABASE_URL` | `jdbc:postgresql://ep-frosty-cake-b5ksywk2-pooler.c-7.us-east-2.aws.neon.tech/neondb?sslmode=require` |
| `DATABASE_USERNAME` | `neondb_owner` |
| `DATABASE_PASSWORD` | Set as a Render secret after rotating the exposed Neon password |
| `LLM_PROVIDER` | `mock` for no-key demo, or `openrouter` after configuring a new key |
| `OPENROUTER_API_KEY` | Optional Render secret; revoke the key previously exposed in chat |
| `OPENROUTER_MODEL` | `google/gemma-3-27b-it:free` or another available model |
| `OPENROUTER_BASE_URL` | `https://openrouter.ai/api/v1` |
| `DOCUTRUST_APP_URL` | Exact frontend origin |
| `DOCUTRUST_CORS_ORIGINS` | `https://docutrust-git-main-gautams-projects-5965c972.vercel.app` (update if you disable protection or assign a domain) |
| `OIDC_ISSUER_URI` | Leave unset until an OIDC login flow is configured in the frontend |

Set `VITE_API_BASE_URL=https://docutrust-yiy1.onrender.com` in the Vercel project environment for the frontend build. Redeploy the Vercel project after changing it. Keep local Neon credentials in `.env.local`; `.env.local` and `.neon` are gitignored.

### Neon PostgreSQL setup

The repository can be linked to a Neon project using the Neon CLI. The project context is local-only (`.neon`) and credentials are kept in `.env.local`; both are excluded from Git.

```bash
npx neon@latest login
npx neon@latest skills --agent vscode -y
npx neon@latest mcp --agent vscode -y
npx neon@latest link --project-id YOUR_NEON_PROJECT_ID --branch production -y
npx neon@latest config init --project-id YOUR_NEON_PROJECT_ID --branch production --from-branch
npx neon@latest config status
```

The current Spring Boot application uses Neon as PostgreSQL, not Neon Functions. The live Neon project already has its Auth and object-storage policy configured; the sample `hello.ts` function is not part of the DocuTrust API and should not be deployed as a substitute for the Spring service. Run `neon config plan` before any policy apply/deploy and review any resource changes.

For Render, set `DATABASE_URL` to a JDBC-form URL using the Neon pooled host:

```text
jdbc:postgresql://YOUR_NEON_POOLER_HOST/neondb?sslmode=require
```

Set `DATABASE_USERNAME` and `DATABASE_PASSWORD` as separate Render environment secrets. If a database password has been pasted into chat or otherwise exposed, rotate it in Neon first, then update Render with the replacement. Never copy `.env.local` or credentials into Git.

### Render

The included [`render.yaml`](./render.yaml) defines a Docker API service and a static frontend. In the Render dashboard, connect the GitHub repository, choose **Blueprint**, and review the generated services before applying. For PostgreSQL, copy the provider's JDBC URL into `DATABASE_URL`; it must begin with `jdbc:postgresql://`. Set `DATABASE_USERNAME` and `DATABASE_PASSWORD` separately when the provider does not expose them in the URL.

The current personal API deployment is `https://docutrust-yiy1.onrender.com`; verify it with `https://docutrust-yiy1.onrender.com/actuator/health`.

### Vercel

Import the repository, set the project root to `frontend`, and set `VITE_API_BASE_URL` to the Render API URL. [`vercel.json`](./vercel.json) enables SPA history fallback. Vercel is suitable for the static frontend; do not put the Spring Boot service in a Vercel function.

### Netlify

Create a site from the repository with the base directory `frontend`, build command `npm run build`, publish directory `dist`, and `VITE_API_BASE_URL` set to the API URL. [`netlify.toml`](./netlify.toml) provides these defaults and the SPA redirect.

### Cloud Run

Cloud Run can host the API container from [`backend/Dockerfile`]. Build and deploy with Google Cloud Build/Artifact Registry, then set `DATABASE_URL`, credentials, `SPRING_PROFILES_ACTIVE=postgres`, `DOCUTRUST_CORS_ORIGINS`, and LLM secrets as Cloud Run secret-backed environment variables. Cloud Run may have a free monthly allowance, but billing account, region, egress, and managed PostgreSQL costs still apply. Use a hosted PostgreSQL provider rather than an in-container database.

```bash
gcloud builds submit ./backend --tag REGION-docker.pkg.dev/PROJECT/docutrust/api
gcloud run deploy docutrust-api \
  --image REGION-docker.pkg.dev/PROJECT/docutrust/api \
  --region REGION --allow-unauthenticated \
  --set-env-vars SPRING_PROFILES_ACTIVE=postgres,LLM_PROVIDER=mock
```

Before public exposure, configure OIDC, restrict CORS to the frontend origin, use a secret manager, and avoid sending confidential documents to an external LLM without an explicit data policy.

### OpenRouter mode

Copy `.env.example` to `.env`, create an OpenRouter key, and set:

```dotenv
LLM_PROVIDER=openrouter
OPENROUTER_API_KEY=replace-me
OPENROUTER_MODEL=google/gemma-3-27b-it:free
```

Then restart the API:

```bash
docker compose up --build
```

The adapter sends a structured findings prompt to OpenRouter and validates the JSON response before persisting findings. OpenRouter model availability, rate limits, and free-tier eligibility can change, so select an active model from the OpenRouter models catalog. Never commit `.env` or API keys.

Set `LLM_PROVIDER=auto` to prefer OpenRouter and fail over to mock analysis when the external provider is unavailable. Use `LLM_PROVIDER=openrouter` when external analysis is mandatory; that mode fails startup if its API key is missing.

## Production foundation additions

The API supports validated text/JSON multipart uploads up to 5 MB, persisted audit events, an audit retrieval endpoint, optional external OIDC resource-server enforcement, Flyway schema migrations, and background analysis with observable `ANALYZING`, `REVIEW`, and `FAILED` states.

## Current boundaries

Uploads currently support text and JSON files up to 5 MB. PDF/DOCX extraction, object storage, durable queue-backed processing, tenant isolation, and review-decision workflows remain separate production milestones.
