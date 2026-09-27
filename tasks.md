# DocuTrust Delivery Tasks

## Completed

- [x] Create Spring Boot backend scaffold
- [x] Create React/Vite frontend scaffold
- [x] Add document and finding domain model
- [x] Add submission, listing, status, and findings endpoints
- [x] Add H2 fallback and PostgreSQL profile
- [x] Add mock LLM provider
- [x] Add OpenRouter adapter and environment configuration
- [x] Add backend and frontend Dockerfiles
- [x] Add PostgreSQL/API/frontend Compose stack
- [x] Add Nginx API proxy
- [x] Add health endpoint
- [x] Add text/JSON multipart upload with size and type validation
- [x] Add document audit event persistence and retrieval
- [x] Add optional OIDC JWT resource-server configuration
- [x] Add Flyway initial schema migration
- [x] Add background analysis executor and failure state
- [x] Add policy-lite provider routing with OpenRouter-to-mock failover
- [x] Dispatch background analysis only after the submit transaction commits
- [x] Add focused analysis success/failure tests
- [x] Add Vercel, Netlify, Render, and Cloud Run deployment configuration
- [x] Add GitHub Actions build workflow
- [x] Link local project to Neon production branch and scaffold policy from live state
- [x] Add README and project governance documents
- [x] Verify local Docker stack and end-to-end submission flow

## Next: production foundation

- [x] Add Flyway initial schema migration
- [x] Add generic OIDC JWT resource-server configuration
- [ ] Add authenticated user and organization context
- [ ] Add tenant-scoped document queries
- [ ] Add roles and method-level authorization
- [ ] Expand audit events for review decisions and exports
- [ ] Add structured API error schema and correlation IDs

## Next: document pipeline

- [x] Add text/JSON multipart upload endpoint
- [x] Add file-size and MIME validation
- [ ] Add object-storage abstraction
- [ ] Add PDF/DOCX text extraction
- [ ] Add OCR provider boundary
- [ ] Add malware scanning hook
- [ ] Add retention and deletion workflow

## Next: analysis platform

- [ ] Add durable analysis job entity and external queue
- [ ] Add retry/backoff and dead-letter handling
- [ ] Add OpenRouter structured-output tests
- [ ] Add Hugging Face adapter
- [ ] Add Ollama/TGI local adapter
- [ ] Add policy router based on sensitivity, task, size, and cost
- [ ] Add provider health and usage metrics

## Next: operations

- [x] Add focused backend analysis tests and run local submission/status/findings/audit smoke test
- [ ] Expand backend integration and frontend component/accessibility test suites
- [ ] Add frontend component and accessibility tests
- [ ] Add CI build, test, security scan, and image scan
- [ ] Add production secret-management instructions
- [ ] Add TLS and reverse-proxy hardening
- [ ] Add metrics, logs, traces, and alerting
- [ ] Perform threat model and load test before release
