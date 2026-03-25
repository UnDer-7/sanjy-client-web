# Frontend Runtime Configuration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Expose a public BFF endpoint `GET /api/v1/maintenance/frontend-runtime-configuration` that returns runtime configuration values (starting with `logoutUrl`) sourced from environment variables, so the compiled React SPA can load environment-specific values without a rebuild.

**Architecture:** A new optional env var maps through `application.yml` into a new `FrontendRuntimeConfigurationProp` nested record in `SanjyClientWebConfigProp`. A new service reads the config and maps it to a response DTO. A new endpoint in the existing `MaintenanceController` delegates to the service.

**Tech Stack:** Java 21, Spring Boot, `@ConfigurationProperties` with `@Validated`, Hibernate Validator (`@URL`), Lombok, Spring Web

> **Note on commits:** Do NOT run `git add`, `git commit`, or `git push`. The user manages all git operations manually.

> **Note on tests:** Tests are explicitly out of scope for this task. Build validation (`mvn clean install`) replaces test-running steps.

---

## File Map

| Action | File | Responsibility |
|--------|------|----------------|
| Edit | `.env` | Add optional env var with blank default |
| Edit | `src/main/resources/application.yml` | Add `frontend-runtime-configuration` block under `sanjy-client-web` |
| Edit | `src/main/java/br/com/gorillaroxo/sanjy/client/web/config/SanjyClientWebConfigProp.java` | Add `frontendRuntimeConfiguration` field and `FrontendRuntimeConfigurationProp` nested record |
| Create | `src/main/java/br/com/gorillaroxo/sanjy/client/web/controller/dto/response/FrontendRuntimeConfigurationControllerResponseDto.java` | Response DTO for the endpoint |
| Create | `src/main/java/br/com/gorillaroxo/sanjy/client/web/service/FrontendRuntimeConfigurationService.java` | Reads config, maps to DTO |
| Edit | `src/main/java/br/com/gorillaroxo/sanjy/client/web/controller/MaintenanceController.java` | Add new endpoint, inject new service |

---

## Task 1: Add environment variable and YAML configuration

**Files:**
- Modify: `.env`
- Modify: `src/main/resources/application.yml`

- [ ] **Step 1: Add the env var to `.env`**

Open `.env`. Find the last section (currently `# ::: OPEN_API :::`). Add a new section after it:

```
# ::: FRONTEND RUNTIME CONFIGURATION :::
SANJY_CLIENT_WEB_FRONTEND_RUNTIME_CONFIGURATION_LOGOUT_URL=
```

Leave the value blank (empty string = not configured locally).

- [ ] **Step 2: Add the YAML block to `application.yml`**

Open `src/main/resources/application.yml`. Find the `sanjy-client-web:` block. Locate the `cors:` entry (currently the last entry under `sanjy-client-web`). Add the new block at the same indentation level, after `cors:`:

```yaml
  frontend-runtime-configuration:
    logout-url: ${SANJY_CLIENT_WEB_FRONTEND_RUNTIME_CONFIGURATION_LOGOUT_URL:}
```

The full `sanjy-client-web:` section should now end with:

```yaml
  upload:
    max-file-size-in-mb: ${SANJY_CLIENT_WEB_SERVER_UPLOAD_MAX_FILE_SIZE_IN_MB}
  ai:
    # ... existing ai config ...
  cors:
    allowed-origins: ${SANJY_CLIENT_WEB_CORS_ALLOWED_ORIGINS:}
  frontend-runtime-configuration:
    logout-url: ${SANJY_CLIENT_WEB_FRONTEND_RUNTIME_CONFIGURATION_LOGOUT_URL:}
```

The `:` with no default causes Spring to inject `""` when the env var is absent — the compact constructor in the next task will normalize `""` to `null`.

---

## Task 2: Add `FrontendRuntimeConfigurationProp` to `SanjyClientWebConfigProp`

**Files:**
- Modify: `src/main/java/br/com/gorillaroxo/sanjy/client/web/config/SanjyClientWebConfigProp.java`

**Context:** `SanjyClientWebConfigProp` is annotated with `@Validated` and `@ConfigurationProperties(prefix = "sanjy-client-web", ignoreUnknownFields = false)`. Adding the YAML block in Task 1 without this Java field would cause a startup failure ("unrecognized field"). Both tasks must be applied together before the next build.

- [ ] **Step 1: Add the new field to the record's constructor parameters**

Open `SanjyClientWebConfigProp.java`. The record currently has these constructor parameters:

```java
public record SanjyClientWebConfigProp(
        @NotNull @Valid ExternalApisProp externalHttpClients,
        @NotNull @Valid ApplicationProp application,
        @NotNull @Valid LoggingProp logging,
        @NotNull @Valid UploadProp upload,
        @NotNull @Valid AiProp ai,
        @Valid CorsProp cors) {
```

Add the new field as the last parameter:

```java
public record SanjyClientWebConfigProp(
        @NotNull @Valid ExternalApisProp externalHttpClients,
        @NotNull @Valid ApplicationProp application,
        @NotNull @Valid LoggingProp logging,
        @NotNull @Valid UploadProp upload,
        @NotNull @Valid AiProp ai,
        @Valid CorsProp cors,
        @NotNull @Valid FrontendRuntimeConfigurationProp frontendRuntimeConfiguration) {
```

Note: `@NotNull` here ensures the app fails at startup if the YAML block is ever removed. `@Valid` cascades validation into the nested record.

- [ ] **Step 2: Add the nested record class inside `SanjyClientWebConfigProp`**

At the end of the class body (before the closing `}`), add the new nested record. Place it after the existing `CorsProp` record:

```java
    public record FrontendRuntimeConfigurationProp(@URL String logoutUrl) {
        public FrontendRuntimeConfigurationProp {
            if (logoutUrl != null && logoutUrl.isBlank()) logoutUrl = null;
        }
    }
```

**Why the compact constructor:** Spring resolves an absent/blank env var to `""`. `@URL` rejects `""` but passes `null`. The compact constructor normalizes `""` (and any whitespace-only value) to `null`, so the `@URL` constraint only fires when a non-blank, non-URL value is set. This chain runs at binding time, before Bean Validation.

Imports needed (add to the imports section if not already present):

```java
import org.hibernate.validator.constraints.URL;
```

---

## Task 3: Create the response DTO

**Files:**
- Create: `src/main/java/br/com/gorillaroxo/sanjy/client/web/controller/dto/response/FrontendRuntimeConfigurationControllerResponseDto.java`

- [ ] **Step 1: Create the DTO file**

Create the file at the path above with the following content:

```java
package br.com.gorillaroxo.sanjy.client.web.controller.dto.response;

import lombok.Builder;

@Builder
public record FrontendRuntimeConfigurationControllerResponseDto(String logoutUrl) {}
```

`logoutUrl` will be `null` in the JSON response when the env var is not configured. The frontend must handle `null` gracefully (e.g., hide the logout button).

---

## Task 4: Create the service

**Files:**
- Create: `src/main/java/br/com/gorillaroxo/sanjy/client/web/service/FrontendRuntimeConfigurationService.java`

- [ ] **Step 1: Create the service file**

Create the file at the path above with the following content:

```java
package br.com.gorillaroxo.sanjy.client.web.service;

import br.com.gorillaroxo.sanjy.client.web.config.SanjyClientWebConfigProp;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.FrontendRuntimeConfigurationControllerResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FrontendRuntimeConfigurationService {

    private final SanjyClientWebConfigProp prop;

    public FrontendRuntimeConfigurationControllerResponseDto execute() {
        return FrontendRuntimeConfigurationControllerResponseDto.builder()
                .logoutUrl(prop.frontendRuntimeConfiguration().logoutUrl())
                .build();
    }
}
```

No null guard on `prop.frontendRuntimeConfiguration()` — `@NotNull` + `@Validated` on the config prop guarantee it is never `null` at runtime.

---

## Task 5: Add the endpoint to `MaintenanceController`

**Files:**
- Modify: `src/main/java/br/com/gorillaroxo/sanjy/client/web/controller/MaintenanceController.java`

- [ ] **Step 1: Add the new service import and field**

Open `MaintenanceController.java`. The class currently injects:

```java
    private final AiAvailabilityService aiAvailabilityService;
    private final MaintenanceProjectInfoService maintenanceProjectInfoService;
```

Add the new service (Lombok `@RequiredArgsConstructor` picks it up automatically):

```java
    private final AiAvailabilityService aiAvailabilityService;
    private final MaintenanceProjectInfoService maintenanceProjectInfoService;
    private final FrontendRuntimeConfigurationService frontendRuntimeConfigurationService;
```

Add the import at the top of the file:

```java
import br.com.gorillaroxo.sanjy.client.web.service.FrontendRuntimeConfigurationService;
import br.com.gorillaroxo.sanjy.client.web.controller.dto.response.FrontendRuntimeConfigurationControllerResponseDto;
```

- [ ] **Step 2: Add the endpoint method**

After the existing `projectInfo()` method, add:

```java
    @GetMapping(value = "/frontend-runtime-configuration", produces = MediaType.APPLICATION_JSON_VALUE)
    public FrontendRuntimeConfigurationControllerResponseDto frontendRuntimeConfiguration() {
        return frontendRuntimeConfigurationService.execute();
    }
```

The full endpoint URL resolves to `GET /api/v1/maintenance/frontend-runtime-configuration` (inherited from `@RequestMapping("/api/v1/maintenance")`).

---

## Task 6: Build validation

- [ ] **Step 1: Run the full build from the project root**

```bash
mvn clean install
```

Expected: `BUILD SUCCESS`

- [ ] **Step 2: If build fails, analyze errors**

Common issues:
- Missing import → add the import in the affected file
- Compilation error in `SanjyClientWebConfigProp` compact constructor → verify the syntax matches Task 2 exactly
- `ignoreUnknownFields` binding error → the YAML block and Java field must both be present

Fix the error, then re-run `mvn clean install`. Repeat until `BUILD SUCCESS`.

- [ ] **Step 3: Manual smoke test (optional but recommended)**

Start the application and call the endpoint:

```bash
curl -s http://localhost:8081/api/v1/maintenance/frontend-runtime-configuration | jq .
```

Expected response (env var not configured):
```json
{
  "logoutUrl": null
}
```

To test with a configured URL, set `SANJY_CLIENT_WEB_FRONTEND_RUNTIME_CONFIGURATION_LOGOUT_URL=https://example.com/logout` in the environment before starting, then repeat the curl.
