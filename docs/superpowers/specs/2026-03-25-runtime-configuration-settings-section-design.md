# Runtime Configuration Settings Section — Design

**Date:** 2026-03-25
**Scope:** Frontend-only. No backend changes. Reads from the existing `AppRuntimeConfigContext` (already implemented).

---

## Overview

The Settings page needs a "Runtime Configuration" section that displays all runtime config properties loaded from the BFF at startup. Each property shows a friendly name, description, its current value (or "SEM VALOR" if null), and an icon that reveals the backing environment variable name via tooltip (desktop hover and mobile touch).

---

## Architecture

### New file

| File | Purpose |
|---|---|
| `src/main/frontend/src/pages/settings/RuntimeConfigurationSection.tsx` | Self-contained section component — metadata array, row rendering, tooltip. Reads context internally. |

### Modified files

| File | Change |
|---|---|
| `src/main/frontend/src/contexts/AppRuntimeConfigContext.tsx` | Export the `RuntimeConfiguration` interface so it can be imported by the new section component |
| `src/main/frontend/src/pages/settings/SettingsPage.tsx` | Import and render `<RuntimeConfigurationSection />` as the last section in the `<Stack>` |

---

## Data Model

A static `RUNTIME_CONFIG_ENTRIES` array defined at module level in `RuntimeConfigurationSection.tsx`:

```ts
interface RuntimeConfigEntry {
  name: string;
  description: string;
  envVar: string;
  getValue: (config: RuntimeConfiguration) => string | null;
}
```

**Single entry for `logoutUrl`:**

```ts
{
  name: 'Logout URL',
  description:
    'URL the application redirects to when the user clicks Logout. ' +
    'Configured by the identity provider (e.g. Cloudflare Access). ' +
    'When absent, the Logout button is not displayed.',
  envVar: 'SANJY_CLIENT_WEB_FRONTEND_RUNTIME_CONFIGURATION_LOGOUT_URL',
  getValue: (config) => config.logoutUrl,
}
```

`RuntimeConfiguration` is imported from `AppRuntimeConfigContext`. **Prerequisite:** the `RuntimeConfiguration` interface in `AppRuntimeConfigContext.tsx` must be changed from `interface` to `export interface` so it can be imported here.

---

## Component Structure

### `RuntimeConfigurationSection`

- Calls `useAppRuntimeConfig()` to get `runtimeConfiguration`
- Renders a `<div>` section with `<Title order={2} size="h3">` and `<Text c="dimmed">` description, matching the existing SettingsPage section pattern
- Maps over `RUNTIME_CONFIG_ENTRIES` and renders one `RuntimeConfigRow` per entry inside a `<Stack gap="sm">`

**Section description text** — use `<Text c="dimmed" size="sm" mb="md">` (matching all other section descriptions in `SettingsPage`):
> "Runtime configuration is loaded from the server at startup. These values are set via environment variables with the prefix `SANJY_CLIENT_WEB_FRONTEND_RUNTIME_CONFIGURATION_*` and cannot be changed at runtime."

### `RuntimeConfigRow` (sub-component, not exported)

Props: `entry: RuntimeConfigEntry`, `value: string | null`

Layout — a `Paper withBorder p="md" radius="md"` (consistent with `ProjectInfoCard`):

```
┌──────────────────────────────────────────────────────────┐
│ Logout URL                          https://example.com 🔍│
│ URL the application redirects to…                         │
└──────────────────────────────────────────────────────────┘
```

- Top row: `Group justify="space-between" align="flex-start"`
  - Left: `Text fw={600} size="sm"` — property name
  - Right: `Group gap="xs" align="center"`
    - Value: `Text size="sm" fw={500}` if non-null; `Text size="sm" c="dimmed" fs="italic"` with text "SEM VALOR" if null
    - `Tooltip` wrapping an `ActionIcon variant="subtle" size="sm" color="gray"` containing `IconSearch size={14}`
- Below top row: `Text c="dimmed" size="sm" mt="xs"` — description

### Tooltip configuration

```tsx
<Tooltip
  label={entry.envVar}
  position="top"
  multiline
  w={320}
  events={{ hover: true, touch: true }}
>
  <ActionIcon variant="subtle" size="sm" color="gray">
    <IconSearch size={14} />
  </ActionIcon>
</Tooltip>
```

`events={{ hover: true, touch: true }}` handles both desktop hover and mobile tap with no extra code.

`multiline w={320}` is used so the long env var string (no spaces) wraps within a fixed-width tooltip rather than overflowing off-screen. The string has no natural word breaks, so Mantine will break on the underscore separators.

---

## SettingsPage Integration

Add import:
```ts
import { RuntimeConfigurationSection } from './RuntimeConfigurationSection.tsx';
```

Insert as the last child inside the `<Stack gap="lg">` in `SettingsPage`, after the "Project Info" `<div>` and **before** the `</Stack>` closing tag (which appears before the `<Modal>` element). The `<Modal>` is outside the `<Stack>` and must remain so.

```tsx
<Stack gap="lg">
  {/* ... existing sections (Theme, Timezone, Time Format, Error Logs, Project Info) ... */}
  <RuntimeConfigurationSection />   {/* ← insert here, last item in Stack */}
</Stack>

<Modal ...>   {/* Modal stays outside Stack, unchanged */}
```

## Loading / Absent State

`AppRuntimeConfigContext` initialises with `{ logoutUrl: null }` and updates asynchronously after the API responds. There is no separate "loading" indicator for this section — both "still loading" and "genuinely not configured" show "SEM VALOR". This is intentional: the API call resolves quickly on the same host, and the distinction is not meaningful to end users.

---

## Mantine Components Used

| Component | Purpose |
|---|---|
| `Paper` | Card wrapper per row, consistent with `ProjectInfoCard` |
| `Group` | Horizontal layout for name/value row and right-side value+icon |
| `Stack` | Vertical layout between rows |
| `Text` | Name, description, value |
| `Tooltip` | Env var name on hover/touch |
| `ActionIcon` | Clickable/hoverable icon container |

Icon: `IconSearch` from `@tabler/icons-react` (already installed).

---

## Constraints

- No frontend tests (per project requirement)
- No new npm packages — all components already in use in the project
- Must run `mvn clean install` after implementation to validate build
- No git operations
