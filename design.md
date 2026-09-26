# DocuTrust Design System

## Visual direction

DocuTrust should feel calm, precise, trustworthy, and review-oriented rather than flashy. The interface should make document state and finding severity immediately understandable.

## Color tokens

- Page background: `#f5f7fb`
- Surface: `#ffffff`
- Primary: `#5670d9`
- Primary soft: `#e8edff`
- Text: `#18212f`
- Secondary text: `#657083`
- Border: `#e8ebf2`
- Low severity: blue
- Medium severity: amber
- High severity: red
- Approved/success: green

## Typography

- Use a modern system sans-serif stack.
- Use large, compact headings with restrained letter spacing.
- Use 13–15px body text for dense review data.
- Use uppercase labels sparingly for metadata and severity.

## Components

- Cards use white surfaces, subtle borders, rounded corners, and restrained shadows.
- Primary actions use the blue primary color and clear verbs.
- Document rows show file name, submission time, and status.
- Findings show severity first, followed by title, explanation, and category.
- Empty states explain what the user can do next.
- Error messages are visible near the action that failed.

## UX requirements

- Keyboard-accessible controls and visible focus states.
- Never rely on color alone to communicate severity.
- Preserve user input when a submission fails.
- Show progress for asynchronous analysis.
- Make provider and data-sensitivity decisions visible to authorized reviewers.
- Keep destructive actions explicit and confirmable.

## Responsive behavior

- Desktop: two-column submit/list layout with findings below.
- Mobile: single-column layout.
- Long file names and findings must wrap or truncate safely.
