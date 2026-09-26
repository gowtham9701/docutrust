# DocuTrust Project Memory

## Current state

- The project is a runnable MVP under `/Users/gautam/personal projects/docutrust`.
- Docker Compose runs PostgreSQL, Spring Boot API, and React/Nginx frontend.
- The local UI is served on port 3000.
- The API is served on port 8080.
- `/actuator/health` has returned `{"status":"UP"}`.
- Document submission, status, listing, and findings flows were verified end to end.

## Architectural decisions

- Maven was selected because the backend is Spring Boot and the container provides Maven when it is not installed on the host.
- PostgreSQL is the deployment database; H2 remains a local fallback.
- `LlmProvider` isolates analysis vendors from document workflow and persistence.
- Mock analysis is the safe default and requires no secrets.
- OpenRouter is the first external provider and uses an OpenAI-compatible chat-completions API.
- Provider credentials are environment-only and must never be committed.
- Generic external OIDC is the intended production identity model.
- The intended routing policy is sensitivity/task/size aware with provider failover.

## Security notes

- An API key was previously pasted into chat and must be treated as revoked.
- No exposed key was stored in the repository.
- `.gitignore` excludes `.env` and other local environment files while preserving `.env.example`.
- Production must not leave all API routes open.

## Known limitations

- The current submission flow accepts extracted text, not binary files.
- Analysis is synchronous.
- Full OIDC enforcement, tenant isolation, audit history, migrations, and provider failover remain tasks.
- A live OpenRouter call requires a newly created local key and an available model.

## Resume checklist

1. Read `prd.md`, `architecture.md`, and `rules.md`.
2. Check `tasks.md` for the next incomplete milestone.
3. Inspect `git diff` before editing.
4. Keep secrets in local `.env`, never in tracked files.
5. Run `docker compose config --quiet`.
6. Rebuild and test the smallest affected service.
7. Update this file when an architectural decision or unresolved issue changes.
