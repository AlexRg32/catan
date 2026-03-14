# Secure Software Delivery Rules

These rules implement basic security best practices for the project.

## 1. Source Security

- **SECRET SCANNERS**: No secrets in code. Use `.env` with `.env.example` as template.
- **DEPENDENCY AUDIT**: Before adding a library, run `npm audit` / `mvn dependency:analyze`.
- **CODE REVIEWS**: Large architectural changes REQUIRE a review from `@architect`.

## 2. Build Security

- **HERMETIC BUILDS**: Dependencies must be locked (use `pom.xml`, `package-lock.json`).
- **ISOLATED BUILDS**: Docker builds should use multi-stage to minimize final image size.

## 3. Data Integrity & Validation

- **Server-side validation**: All domain constraints and business rules must be enforced in the Service layer.
- **Rate limiting**: Public endpoints should be rate-limited to prevent abuse.
- **Input validation**: All user inputs validated with Bean Validation (JSR 303) on the backend and Zod on the frontend.

## 4. Design Patterns for Security

- **LEAST PRIVILEGE**: Database users only have the permissions they strictly need.
- **CORS**: Properly configured to only allow the frontend origin.
- **CSRF**: Protection enabled for state-changing operations.
