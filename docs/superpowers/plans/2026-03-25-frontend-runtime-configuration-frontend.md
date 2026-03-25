# Frontend Runtime Configuration — Frontend Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Fetch runtime configuration from `GET /api/v1/maintenance/frontend-runtime-configuration` on app startup, expose it via a React context, and conditionally render a Logout button in the header when `logoutUrl` is configured.

**Architecture:** A dedicated `AppRuntimeConfigContext` holds a `runtimeConfiguration` object (starting with `logoutUrl`). The provider fetches from `MaintenanceClient` once on mount. A `useGetLogoutUrl` hook reads from the context. The `HeaderSanjy` component renders a `LogoutButton` in both desktop and mobile views only when `logoutUrl` is non-null.

**Tech Stack:** React 19, TypeScript, Mantine v8 (`Button`, `NavLink`), Axios (existing `HttpClient`).

> **No frontend tests** — project requirement. Validation is done via `mvn clean install` and browser inspection.
> **No git operations** — do not run `git add`, `git commit`, or `git push`.

---

## File Map

| Action | Path | Responsibility |
|--------|------|----------------|
| Create | `src/main/frontend/src/models/FrontendRuntimeConfiguration.ts` | TypeScript type matching backend DTO |
| Modify | `src/main/frontend/src/clients/MaintenanceClient.ts` | Add `frontendRuntimeConfiguration()` function |
| Create | `src/main/frontend/src/contexts/AppRuntimeConfigContext.tsx` | Context + provider + `useAppRuntimeConfig` hook |
| Create | `src/main/frontend/src/hooks/useGetLogoutUrl.ts` | Focused hook returning `string | null` |
| Modify | `src/main/frontend/src/App.tsx` | Wrap tree with `AppRuntimeConfigProvider` |
| Modify | `src/main/frontend/src/components/HeaderSanjy.tsx` | Add `LogoutButton` to desktop header and mobile nav |

---

## Task 1: Model + MaintenanceClient

**Files:**
- Create: `src/main/frontend/src/models/FrontendRuntimeConfiguration.ts`
- Modify: `src/main/frontend/src/clients/MaintenanceClient.ts`

- [ ] **Step 1: Create the model type**

Create `src/main/frontend/src/models/FrontendRuntimeConfiguration.ts`:

```ts
export interface FrontendRuntimeConfiguration {
  logoutUrl: string | null;
}
```

- [ ] **Step 2: Add the client function**

In `src/main/frontend/src/clients/MaintenanceClient.ts`, add the import and new function.

Add import at the top:
```ts
import type { FrontendRuntimeConfiguration } from '../models/FrontendRuntimeConfiguration.ts';
```

Add function (before the export):
```ts
async function frontendRuntimeConfiguration(): Promise<FrontendRuntimeConfiguration> {
  const response = await HttpClient.get<FrontendRuntimeConfiguration>(
    `${RESOURCE_URL}/frontend-runtime-configuration`
  );
  return response.data;
}
```

Add `frontendRuntimeConfiguration` to the exported `MaintenanceClient` object:
```ts
export const MaintenanceClient = {
  checkAiAvailability,
  projectInfo,
  frontendRuntimeConfiguration,
};
```

---

## Task 2: AppRuntimeConfigContext

**Files:**
- Create: `src/main/frontend/src/contexts/AppRuntimeConfigContext.tsx`

Follow the exact pattern of `src/main/frontend/src/contexts/LoadingContext.tsx`.

- [ ] **Step 1: Create the context file**

Create `src/main/frontend/src/contexts/AppRuntimeConfigContext.tsx`:

```tsx
import { createContext, useContext, useEffect, useMemo, useState } from 'react';
import type { ReactNode } from 'react';
import { MaintenanceClient } from '../clients/MaintenanceClient.ts';
import type { FrontendRuntimeConfiguration } from '../models/FrontendRuntimeConfiguration.ts';

interface RuntimeConfiguration {
  logoutUrl: string | null;
}

interface AppRuntimeConfigContextType {
  runtimeConfiguration: RuntimeConfiguration;
}

const DEFAULT_RUNTIME_CONFIGURATION: RuntimeConfiguration = {
  logoutUrl: null,
};

const AppRuntimeConfigContext = createContext<AppRuntimeConfigContextType | undefined>(undefined);

export function AppRuntimeConfigProvider({ children }: Readonly<{ children: ReactNode }>) {
  const [runtimeConfiguration, setRuntimeConfiguration] = useState<RuntimeConfiguration>(
    DEFAULT_RUNTIME_CONFIGURATION
  );

  useEffect(() => {
    MaintenanceClient.frontendRuntimeConfiguration()
      .then((data: FrontendRuntimeConfiguration) => {
        setRuntimeConfiguration({ logoutUrl: data.logoutUrl });
      })
      .catch(() => {
        // Error notification is handled by the Axios interceptor in AxiosConfig.ts.
        // Keep the safe default: { logoutUrl: null }.
      });
  }, []);

  const value = useMemo(() => ({ runtimeConfiguration }), [runtimeConfiguration]);

  return (
    <AppRuntimeConfigContext.Provider value={value}>
      {children}
    </AppRuntimeConfigContext.Provider>
  );
}

export function useAppRuntimeConfig(): AppRuntimeConfigContextType {
  const context = useContext(AppRuntimeConfigContext);
  if (context === undefined) {
    throw new Error('useAppRuntimeConfig must be used within an AppRuntimeConfigProvider');
  }
  return context;
}
```

---

## Task 3: useGetLogoutUrl Hook

**Files:**
- Create: `src/main/frontend/src/hooks/useGetLogoutUrl.ts`

- [ ] **Step 1: Create the hook**

Create `src/main/frontend/src/hooks/useGetLogoutUrl.ts`:

```ts
import { useAppRuntimeConfig } from '../contexts/AppRuntimeConfigContext.tsx';

export function useGetLogoutUrl(): string | null {
  const { runtimeConfiguration } = useAppRuntimeConfig();
  return runtimeConfiguration.logoutUrl;
}
```

---

## Task 4: Wrap App.tsx with AppRuntimeConfigProvider

**Files:**
- Modify: `src/main/frontend/src/App.tsx`

- [ ] **Step 1: Add import**

Add to the imports in `App.tsx`:
```ts
import { AppRuntimeConfigProvider } from './contexts/AppRuntimeConfigContext.tsx';
```

- [ ] **Step 2: Wrap the tree**

Inside `LoadingProvider`, wrap `BrowserRouter` with `AppRuntimeConfigProvider`:

Before:
```tsx
<LoadingProvider>
  <BrowserRouter>
```

After:
```tsx
<LoadingProvider>
  <AppRuntimeConfigProvider>
    <BrowserRouter>
```

And close it after `</BrowserRouter>`:
```tsx
    </BrowserRouter>
  </AppRuntimeConfigProvider>
</LoadingProvider>
```

---

## Task 5: Add Logout Button to HeaderSanjy

**Files:**
- Modify: `src/main/frontend/src/components/HeaderSanjy.tsx`

Before editing, consult the Mantine v8 Button docs:
`https://mantine.dev/llms/core-button.md`

- [ ] **Step 1: Add imports**

Add to the existing imports in `HeaderSanjy.tsx`:

```ts
import { Button } from '@mantine/core';
import { useGetLogoutUrl } from '../hooks/useGetLogoutUrl.ts';
```

- [ ] **Step 2: Add LogoutButton sub-component**

Add this function component in `HeaderSanjy.tsx`, after the `CurrentDateTime` component and before `HeaderSanjy`:

```tsx
function LogoutButton() {
  const logoutUrl = useGetLogoutUrl();
  if (!logoutUrl) return null;
  return (
    <Button
      variant="subtle"
      size="compact-sm"
      color="red"
      onClick={() => {
        window.location.href = logoutUrl;
      }}
    >
      Logout
    </Button>
  );
}
```

- [ ] **Step 3: Add LogoutButton to the desktop header**

In the `HeaderSanjy` component, inside the `<Group gap="md" visibleFrom="sm">` block, add `<LogoutButton />` after `<CurrentDateTime />`:

Before:
```tsx
<Group gap="md" visibleFrom="sm">
  {navItems.map((item) => (
    ...
  ))}
  <CurrentDateTime />
</Group>
```

After:
```tsx
<Group gap="md" visibleFrom="sm">
  {navItems.map((item) => (
    ...
  ))}
  <CurrentDateTime />
  <LogoutButton />
</Group>
```

- [ ] **Step 4: Add LogoutButton to the mobile NavigationMenu**

In `NavigationMenu`, add `<LogoutButton />` after the nav link list:

Before:
```tsx
<AppShell.Navbar p="md">
  {navItems.map((item) => (
    <NavLink ... />
  ))}
</AppShell.Navbar>
```

After:
```tsx
<AppShell.Navbar p="md">
  {navItems.map((item) => (
    <NavLink ... />
  ))}
  <LogoutButton />
</AppShell.Navbar>
```

---

## Task 6: Build Validation and Browser Check

- [ ] **Step 1: Run the build from the project root**

```bash
mvn clean install
```

Expected: `BUILD SUCCESS`. If it fails, read the error output, fix TypeScript/compilation errors, and re-run until it passes.

- [ ] **Step 2: Start the backend and frontend dev server**

In one terminal (project root):
```bash
./mvnw spring-boot:run
```

In another terminal:
```bash
cd src/main/frontend && npm run dev
```

- [ ] **Step 3: Open Chrome and verify**

Navigate to `http://localhost:5173`.

Open DevTools → Network tab. Verify:
- `GET /api/v1/maintenance/frontend-runtime-configuration` fires on page load
- Response is `{ "logoutUrl": null }` (local default) — logout button should NOT appear

- [ ] **Step 4: Verify with logoutUrl configured**

In the project root `.env`, set:
```
SANJY_CLIENT_WEB_FRONTEND_RUNTIME_CONFIGURATION_LOGOUT_URL=https://example.com/logout
```

Restart the backend. Reload the browser. Verify:
- Network response is `{ "logoutUrl": "https://example.com/logout" }`
- Logout button appears in the desktop header (after CurrentDateTime)
- Logout button appears at the bottom of the mobile nav
- Clicking Logout navigates the current tab to `https://example.com/logout`

- [ ] **Step 5: Restore .env**

Reset `SANJY_CLIENT_WEB_FRONTEND_RUNTIME_CONFIGURATION_LOGOUT_URL=` (blank) in `.env` after verification.
