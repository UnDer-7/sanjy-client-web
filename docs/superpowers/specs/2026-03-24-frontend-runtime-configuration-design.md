# Frontend Runtime Configuration — Design Spec

**Date:** 2026-03-24
**Branch:** next-release-1.2.0
**Scope:** Backend only. No frontend changes. No tests.

---

## Problem

The frontend is a compiled SPA (Vite). Values that vary by deployment environment (e.g., a logout URL managed by a gateway like Cloudflare Access) cannot be embedded at build time. A database is out of scope for this project.

**Solution:** The BFF exposes a public endpoint that returns runtime configuration values sourced from environment variables. The frontend calls this endpoint on startup to load values it cannot know at build time.

---

## Architecture

### Pattern

`ENV VAR → application.yml → SanjyClientWebConfigProp (nested record) → Service → DTO → Controller endpoint`

This is the established pattern in the project. No new patterns are introduced.

### Why public endpoint

Authentication is handled at the gateway level (Cloudflare Access). The Spring Boot application has no Spring Security configured, so all endpoints are already public by default. No changes needed.

---

## Optionality model

Two levels of optionality exist in this feature:

| Level | What | Required? |
|-------|------|-----------|
| Config group | `frontendRuntimeConfiguration` field in `SanjyClientWebConfigProp` | **Required** (`@NotNull`) — the YAML block lives in the repo and is always present |
| Inner value | `logoutUrl` inside the config group | **Optional** — env var can be blank; normalizes to `null` |

This matches the `externalHttpClients` pattern (group is `@NotNull`) rather than the `cors` pattern (group is nullable). The group always exists; its inner values may or may not be configured.

---

## Components

### 1. Environment Variable (`.env`)

```
# ::: FRONTEND RUNTIME CONFIGURATION :::
SANJY_CLIENT_WEB_FRONTEND_RUNTIME_CONFIGURATION_LOGOUT_URL=
```

- Optional. Left blank in local development.
- In production, set to the full logout URL (e.g., `https://gorillaroxo.cloudflareaccess.com/cdn-cgi/access/logout?url=https://sanjy.gorillaroxo.com.br`).

### 2. `application.yml`

Under `sanjy-client-web:`, at the same indentation level as `cors:`, `logging:`, etc.:

```yaml
frontend-runtime-configuration:
  logout-url: ${SANJY_CLIENT_WEB_FRONTEND_RUNTIME_CONFIGURATION_LOGOUT_URL:}
```

The `:` with no default causes Spring to inject `""` when the env var is absent or blank. This YAML block is part of the codebase and is always present.

### 3. `SanjyClientWebConfigProp` — new nested record

`SanjyClientWebConfigProp` already has `@Validated` on the class, which activates Bean Validation at startup for all `@NotNull`, `@URL`, etc. constraints. No change needed for validation to work.

`SanjyClientWebConfigProp` also uses `ignoreUnknownFields = false`, so the new YAML key `frontend-runtime-configuration` must be mapped as a field here (kebab-case key → camelCase field via Spring relaxed binding).

New field added to the main record:

```java
@NotNull @Valid FrontendRuntimeConfigurationProp frontendRuntimeConfiguration
```

`@NotNull` ensures the app fails at startup if the YAML block is ever accidentally removed from `application.yml`. Combined with `@Validated` on the record, this fires as a `ConstraintViolationException` at startup. The field is never `null` at runtime, so the service can read `.logoutUrl()` without a null guard.

New nested record:

```java
public record FrontendRuntimeConfigurationProp(@URL String logoutUrl) {
    public FrontendRuntimeConfigurationProp {
        if (logoutUrl != null && logoutUrl.isBlank()) logoutUrl = null;
    }
}
```

**Validation logic (step by step):**
1. Spring resolves `${...LOGOUT_URL:}` → `""` when env var is absent.
2. Compact constructor runs at binding time (before Bean Validation). It normalizes `""` → `null`. This normalization is required: `@URL` rejects empty strings but passes `null`. Whitespace-only values are also normalized to `null` via `isBlank()`.
3. `@URL` (Hibernate Validator) skips `null` — validation passes.
4. If a non-blank, non-URL value is set, `@URL` fires at startup via `@Validated` and the app fails with `ConstraintViolationException`.

### 4. Response DTO

**File:** `controller/dto/response/FrontendRuntimeConfigurationControllerResponseDto.java`

```java
@Builder
public record FrontendRuntimeConfigurationControllerResponseDto(String logoutUrl) {}
```

- `logoutUrl` will be `null` in the JSON response when not configured. The frontend must handle this gracefully.
- No compact constructor needed — `logoutUrl` is a passthrough `String`.

### 5. Service

**File:** `service/FrontendRuntimeConfigurationService.java`

- Annotated with `@Service`, `@RequiredArgsConstructor`.
- Injects `SanjyClientWebConfigProp`.
- Reads `prop.frontendRuntimeConfiguration().logoutUrl()`. Safe without null guard — `@NotNull` + `@Validated` guarantee the object is non-null at startup.
- Maps to `FrontendRuntimeConfigurationControllerResponseDto` and returns it.
- No business logic — exists to maintain the controller→service separation established in the project.

### 6. Controller

**File:** `controller/MaintenanceController.java` (existing file, new endpoint added)

```
GET /api/v1/maintenance/frontend-runtime-configuration
produces: application/json
```

- Adds `FrontendRuntimeConfigurationService` as a new injected dependency (Lombok `@RequiredArgsConstructor` handles this automatically).
- Delegates to `FrontendRuntimeConfigurationService.execute()`.
- Returns `FrontendRuntimeConfigurationControllerResponseDto` directly (consistent with existing methods in the controller).

---

## Data Flow

```
Request: GET /api/v1/maintenance/frontend-runtime-configuration
  → MaintenanceController.frontendRuntimeConfiguration()
    → FrontendRuntimeConfigurationService.execute()
      → SanjyClientWebConfigProp.frontendRuntimeConfiguration().logoutUrl()
        ← String (null if env var blank, URL string if configured)
      ← FrontendRuntimeConfigurationControllerResponseDto(logoutUrl)
    ← DTO
  ← 200 OK { "logoutUrl": "https://..." } or { "logoutUrl": null }
```

---

## Error Handling

- **Invalid URL at startup:** App fails with `ConstraintViolationException` via `@Validated` + `@URL`. Misconfiguration caught early.
- **YAML block missing at startup:** `ignoreUnknownFields = false` rejects YAML keys with no corresponding Java field (not the reverse). If the YAML block is absent but the Java field exists, Spring binds `null` to the field, and `@NotNull` fires a `ConstraintViolationException` at startup. Prevents runtime NPE.
- **YAML key present but no Java field:** Spring binding error at startup (unrecognized field). This cannot happen in normal operation since the field is added alongside the YAML block.
- **Not configured (blank/absent env var):** Returns `{ "logoutUrl": null }` with HTTP 200. No error.

---

## Files Changed

| Action | File |
|--------|------|
| Edit | `src/main/resources/application.yml` |
| Edit | `.env` |
| Edit | `src/main/java/.../config/SanjyClientWebConfigProp.java` |
| Create | `src/main/java/.../controller/dto/response/FrontendRuntimeConfigurationControllerResponseDto.java` |
| Create | `src/main/java/.../service/FrontendRuntimeConfigurationService.java` |
| Edit | `src/main/java/.../controller/MaintenanceController.java` |

---

## Out of Scope

- Frontend implementation (separate task)
- Tests (explicitly excluded from this task)
- Any database or persistence layer
