# Frontend Runtime Configuration — Frontend Design

**Date:** 2026-03-25
**Scope:** Frontend-only. No backend changes. Backend endpoint `GET /api/v1/maintenance/frontend-runtime-configuration` already implemented.

---

## Overview

The compiled React SPA cannot read server environment variables at runtime. The BFF endpoint `GET /api/v1/maintenance/frontend-runtime-configuration` solves this by exposing runtime configuration as JSON. The frontend must call this endpoint once at startup, hold the result in a global React context, and expose it to components via hooks.

The first consumer is the **Logout button** in the header: it renders only when `logoutUrl` is configured, and navigates the current tab to that URL when clicked.

---

## Architecture

### New files

| File | Purpose |
|---|---|
| `src/models/FrontendRuntimeConfiguration.ts` | TypeScript type for the API response |
| `src/contexts/AppRuntimeConfigContext.tsx` | React context + provider + `useAppRuntimeConfig` hook |
| `src/hooks/useGetLogoutUrl.ts` | Focused hook — reads `logoutUrl` from context |

### Modified files

| File | Change |
|---|---|
| `src/clients/MaintenanceClient.ts` | Add `frontendRuntimeConfiguration()` function |
| `src/App.tsx` | Wrap tree with `AppRuntimeConfigProvider` |
| `src/components/HeaderSanjy.tsx` | Add `LogoutButton` component; render it in header and mobile nav |

---

## Data Model

```ts
// src/models/FrontendRuntimeConfiguration.ts
export interface FrontendRuntimeConfiguration {
  logoutUrl: string | null;
}
```

Matches the backend DTO `FrontendRuntimeConfigurationControllerResponseDto { String logoutUrl }`.

---

## MaintenanceClient

Add one function to the existing client:

```ts
async function frontendRuntimeConfiguration(): Promise<FrontendRuntimeConfiguration> {
  const response = await HttpClient.get<FrontendRuntimeConfiguration>(
    `${RESOURCE_URL}/frontend-runtime-configuration`
  );
  return response.data;
}
```

Export it alongside `checkAiAvailability` and `projectInfo`.

---

## AppRuntimeConfigContext

Follows the exact pattern of `LoadingContext.tsx`.

```ts
interface AppRuntimeConfigContextType {
  runtimeConfiguration: {
    logoutUrl: string | null;
  };
}
```

**Provider behavior:**
- Initial state: `{ logoutUrl: null }` — safe default, logout button stays hidden until API responds.
- On mount (`useEffect` with `[]`): calls `MaintenanceClient.frontendRuntimeConfiguration()`.
- On success: sets `runtimeConfiguration` with the API response.
- On error: stays at default `{ logoutUrl: null }`. The Axios interceptor in `AxiosConfig.ts` already shows an error notification; no additional error handling needed here.

**Hook:**
```ts
export function useAppRuntimeConfig(): AppRuntimeConfigContextType {
  const context = useContext(AppRuntimeConfigContext);
  if (context === undefined) {
    throw new Error('useAppRuntimeConfig must be used within an AppRuntimeConfigProvider');
  }
  return context;
}
```

---

## useGetLogoutUrl Hook

```ts
// src/hooks/useGetLogoutUrl.ts
export function useGetLogoutUrl(): string | null {
  const { runtimeConfiguration } = useAppRuntimeConfig();
  return runtimeConfiguration.logoutUrl;
}
```

Returns `string | null`. Consumers check for null before rendering logout UI.

---

## App.tsx Integration

`AppRuntimeConfigProvider` is placed **inside `LoadingProvider`** so future UI inside the provider can use the loading overlay if needed. It wraps `BrowserRouter` and everything inside it:

```tsx
<LoadingProvider>
  <AppRuntimeConfigProvider>
    <BrowserRouter>
      ...
    </BrowserRouter>
  </AppRuntimeConfigProvider>
</LoadingProvider>
```

---

## HeaderSanjy — Logout Button

A new `LogoutButton` sub-component inside `HeaderSanjy.tsx`:

```tsx
function LogoutButton() {
  const logoutUrl = useGetLogoutUrl();
  if (!logoutUrl) return null;
  return (
    <Button variant="subtle" size="sm" color="red"
      onClick={() => { window.location.href = logoutUrl; }}>
      Logout
    </Button>
  );
}
```

**Desktop header** — `LogoutButton` is added at the end of the desktop `Group` (after `CurrentDateTime`):
```tsx
<Group gap="md" visibleFrom="sm">
  {navItems.map(...)}
  <CurrentDateTime />
  <LogoutButton />
</Group>
```

**Mobile nav** — `LogoutButton` is rendered at the bottom of `NavigationMenu` (inside `AppShell.Navbar`), after the nav links. Uses the same `LogoutButton` component.

**Behavior:** clicking navigates the current tab to `logoutUrl` via `window.location.href`. No new tab — standard SSO logout redirect.

---

## Error Handling

| Scenario | Behavior |
|---|---|
| API call succeeds, `logoutUrl` is non-null | Logout button visible |
| API call succeeds, `logoutUrl` is null | Logout button hidden |
| API call fails | Default `{ logoutUrl: null }`, button hidden; Axios interceptor shows error notification |

---

## Constraints

- No frontend tests (per project requirements).
- No new npm packages required — uses existing React context API and Axios.
- Must run `mvn clean install` after implementation to validate build.
