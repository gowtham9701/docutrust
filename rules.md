# DocuTrust Engineering Rules

## Code standards

- Use Java 21 language features only when they improve clarity.
- Keep controllers thin; place business logic in services.
- Use typed DTOs at API boundaries.
- Prefer existing helpers and abstractions over duplicated provider logic.
- Keep frontend components small and accessible.
- Use ASCII by default and preserve existing formatting.

## Security

- Never commit API keys, passwords, tokens, or private documents.
- Use environment variables or a secret manager for credentials.
- Do not log raw document contents or provider secrets.
- Treat external-provider routing as a data-governance decision.
- Require authentication and tenant authorization before production exposure.
- Validate file names, content size, MIME types, and provider responses.

## Error handling

- Return explicit HTTP errors for invalid input, missing resources, provider failures, and authorization failures.
- Do not silently fall back from a configured external provider to mock analysis.
- Mock mode is allowed only when explicitly selected or when no provider is configured for local development.
- Preserve useful correlation/request identifiers in logs.
- Do not catch broad exceptions unless they are converted into a specific, observable application error.

## Testing

- Test the smallest relevant scope first.
- Cover validation, not-found behavior, provider parsing, provider failure, and authorization boundaries.
- Use deterministic mock providers for unit and local integration tests.
- Do not make normal CI tests depend on live API keys or third-party availability.
- Run Compose configuration validation and container builds before deployment approval.

## Scope constraints

- Do not modify files outside `docutrust`.
- Do not introduce a new dependency when an existing library solves the problem.
- Do not add a provider without documenting its endpoint, model configuration, failure behavior, and data implications.
- Do not represent a prototype result as production-ready.
