# DocuTrust Product Requirements Document

## 1. Product

DocuTrust is a document-intelligence workspace that helps teams identify, explain, and track trust, compliance, privacy, and security concerns in business documents.

## 2. Problem

Document review is often manual, inconsistent, slow, and difficult to audit. Reviewers need a repeatable way to submit documents, receive explainable findings, and retain a clear review status without exposing sensitive content unnecessarily.

## 3. Target users

- Compliance and risk reviewers
- Security and privacy teams
- Procurement and vendor-management teams
- Legal and operations reviewers
- Engineering teams integrating document analysis into workflows

## 4. MVP outcome

A user can submit extracted document text, see the document stored in the system, inspect its analysis status, and review structured findings with severity, category, title, and explanation.

## 5. Core features

### Current

- React dashboard for document submission and review
- REST API under `/api/v1`
- Document persistence through JPA
- PostgreSQL Docker deployment
- H2 fallback for local development
- Synchronous mock analysis without secrets
- OpenRouter-compatible analysis provider
- Structured findings validation
- Document status and findings endpoints
- Health endpoint
- Security configuration ready for OIDC/RBAC expansion

### Production roadmap

- OIDC login and enforced JWT authorization
- Organization and tenant isolation
- Role-based permissions
- PDF, DOCX, and image upload
- Text extraction and OCR
- Asynchronous analysis jobs
- OpenRouter, Hugging Face, local Ollama/TGI, and Google provider adapters
- Policy-based provider routing and failover
- Audit history and reviewer decisions
- Object storage and retention policies
- Database migrations
- Metrics, traces, structured logs, and alerting
- CI/CD and hardened deployment

## 6. Non-goals for the first production increment

- Fully autonomous legal advice
- Automatic approval without human review
- Unbounded document retention
- Sending sensitive documents to external providers without an explicit policy decision

## 7. Success criteria

- A new document can be submitted and retrieved through the UI and API.
- Every finding has a clear severity and explanation.
- Provider failures are visible and do not produce false success responses.
- Mock mode works without credentials.
- External-provider mode never stores credentials in source control.
- API access can be restricted by authenticated identity and role.
