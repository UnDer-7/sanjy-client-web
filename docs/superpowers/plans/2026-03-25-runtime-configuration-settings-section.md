# Runtime Configuration Settings Section — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a "Runtime Configuration" section to the Settings page that displays each runtime config property with its name, description, current value (or "SEM VALOR" if null), and a tooltip showing the backing environment variable name on hover/touch.

**Architecture:** A static `RUNTIME_CONFIG_ENTRIES` array drives the display — adding future properties requires only adding an entry to the array. `RuntimeConfigurationSection` is a self-contained component that reads `AppRuntimeConfigContext` directly. `SettingsPage` renders it as the last section in its `<Stack>`.

**Tech Stack:** React 19, TypeScript, Mantine v8 (`Paper`, `Group`, `Stack`, `Text`, `Tooltip`, `ActionIcon`), `@tabler/icons-react` (`IconSearch`).

> **No frontend tests** — project requirement. Validation is `mvn clean install` + browser.
> **No git operations** — do not run `git add`, `git commit`, or `git push`.

---

## File Map

| Action | Path | Responsibility |
|--------|------|----------------|
| Modify | `src/main/frontend/src/contexts/AppRuntimeConfigContext.tsx` | Export `RuntimeConfiguration` interface |
| Create | `src/main/frontend/src/pages/settings/RuntimeConfigurationSection.tsx` | Section component: metadata array + row rendering + tooltip |
| Modify | `src/main/frontend/src/pages/settings/SettingsPage.tsx` | Import and render `<RuntimeConfigurationSection />` as last item in `<Stack>` |

---

## Task 1: Export RuntimeConfiguration from AppRuntimeConfigContext

**Files:**
- Modify: `src/main/frontend/src/contexts/AppRuntimeConfigContext.tsx` (line 6)

- [ ] **Step 1: Read the file**

Read `/home/under7/Workspace/sanjy/sanjy-client-web/src/main/frontend/src/contexts/AppRuntimeConfigContext.tsx` to see the current state.

- [ ] **Step 2: Export the interface**

On line 6, change:
```ts
interface RuntimeConfiguration {
```
to:
```ts
export interface RuntimeConfiguration {
```

No other changes to this file.

- [ ] **Step 3: Verify the file compiles**

```bash
cd /home/under7/Workspace/sanjy/sanjy-client-web/src/main/frontend && npx tsc --noEmit
```
Expected: no errors.

---

## Task 2: Create RuntimeConfigurationSection component

**Files:**
- Create: `src/main/frontend/src/pages/settings/RuntimeConfigurationSection.tsx`

Before writing, consult Mantine v8 docs:
- `https://mantine.dev/llms/core-tooltip.md`
- `https://mantine.dev/llms/core-action-icon.md`

- [ ] **Step 1: Create the file**

Create `src/main/frontend/src/pages/settings/RuntimeConfigurationSection.tsx` with the following content:

```tsx
import { ActionIcon, Group, Paper, Stack, Text, Title, Tooltip } from '@mantine/core';
import { IconSearch } from '@tabler/icons-react';
import { useAppRuntimeConfig } from '../../contexts/AppRuntimeConfigContext.tsx';
import type { RuntimeConfiguration } from '../../contexts/AppRuntimeConfigContext.tsx';

interface RuntimeConfigEntry {
  name: string;
  description: string;
  envVar: string;
  getValue: (config: RuntimeConfiguration) => string | null;
}

const RUNTIME_CONFIG_ENTRIES: RuntimeConfigEntry[] = [
  {
    name: 'Logout URL',
    description:
      'URL the application redirects to when the user clicks Logout. ' +
      'Configured by the identity provider (e.g. Cloudflare Access). ' +
      'When absent, the Logout button is not displayed.',
    envVar: 'SANJY_CLIENT_WEB_FRONTEND_RUNTIME_CONFIGURATION_LOGOUT_URL',
    getValue: (config) => config.logoutUrl,
  },
];

interface RuntimeConfigRowProps {
  entry: RuntimeConfigEntry;
  value: string | null;
}

function RuntimeConfigRow({ entry, value }: Readonly<RuntimeConfigRowProps>) {
  return (
    <Paper withBorder p="md" radius="md">
      <Group justify="space-between" align="flex-start">
        <Text fw={600} size="sm">
          {entry.name}
        </Text>
        <Group gap="xs" align="center">
          {value !== null ? (
            <Text size="sm" fw={500}>
              {value}
            </Text>
          ) : (
            <Text size="sm" c="dimmed" fs="italic">
              SEM VALOR
            </Text>
          )}
          <Tooltip
            label={entry.envVar}
            position="top"
            multiline
            w={320}
            events={{ hover: true, touch: true }}
          >
            <ActionIcon variant="subtle" size="sm" color="gray" aria-label="Show environment variable">
              <IconSearch size={14} />
            </ActionIcon>
          </Tooltip>
        </Group>
      </Group>
      <Text c="dimmed" size="sm" mt="xs">
        {entry.description}
      </Text>
    </Paper>
  );
}

export function RuntimeConfigurationSection() {
  const { runtimeConfiguration } = useAppRuntimeConfig();

  return (
    <div>
      <Title order={2} size="h3" mb="sm">
        Runtime Configuration
      </Title>
      <Text c="dimmed" size="sm" mb="md">
        Runtime configuration is loaded from the server at startup. These values are set via
        environment variables with the prefix{' '}
        <Text span fw={500} size="sm">
          SANJY_CLIENT_WEB_FRONTEND_RUNTIME_CONFIGURATION_*
        </Text>{' '}
        and cannot be changed at runtime.
      </Text>
      <Stack gap="sm">
        {RUNTIME_CONFIG_ENTRIES.map((entry) => (
          <RuntimeConfigRow
            key={entry.envVar}
            entry={entry}
            value={entry.getValue(runtimeConfiguration)}
          />
        ))}
      </Stack>
    </div>
  );
}
```

- [ ] **Step 2: Verify TypeScript compiles**

```bash
cd /home/under7/Workspace/sanjy/sanjy-client-web/src/main/frontend && npx tsc --noEmit
```
Expected: no errors.

---

## Task 3: Add RuntimeConfigurationSection to SettingsPage

**Files:**
- Modify: `src/main/frontend/src/pages/settings/SettingsPage.tsx`

- [ ] **Step 1: Read the file**

Read `/home/under7/Workspace/sanjy/sanjy-client-web/src/main/frontend/src/pages/settings/SettingsPage.tsx` to find the exact closing `</Stack>` tag (currently around line 283) that ends the sections stack.

- [ ] **Step 2: Add the import**

In the imports section near the top of `SettingsPage.tsx`, add:
```ts
import { RuntimeConfigurationSection } from './RuntimeConfigurationSection.tsx';
```

Place it alongside the other local imports (`ErrorLogsComponent`, etc.).

- [ ] **Step 3: Render the section**

Inside the `<Stack gap="lg">` block, add `<RuntimeConfigurationSection />` as the last child — after the "Project Info" `<div>` and before the `</Stack>` closing tag:

The current last section ends approximately at:
```tsx
          )}
        </div>
      </Stack>   {/* ← insert before this line */}
```

After the change:
```tsx
          )}
        </div>

        <RuntimeConfigurationSection />
      </Stack>
```

Do NOT place it after the `</Stack>` or near the `<Modal>`.

- [ ] **Step 4: Verify TypeScript compiles**

```bash
cd /home/under7/Workspace/sanjy/sanjy-client-web/src/main/frontend && npx tsc --noEmit
```
Expected: no errors.

---

## Task 4: Build Validation

- [ ] **Step 1: Run the full build from the project root**

```bash
cd /home/under7/Workspace/sanjy/sanjy-client-web && mvn clean install
```
Expected: `BUILD SUCCESS`. If it fails, read the error, fix it, and re-run until it passes.

- [ ] **Step 2: Copy built static assets for browser validation**

After the build, copy the new static files so the running backend serves them:
```bash
cp -r /home/under7/Workspace/sanjy/sanjy-client-web/src/main/resources/static /home/under7/Workspace/sanjy/sanjy-client-web/target/classes/
```

- [ ] **Step 3: Open Chrome and navigate to Settings**

Navigate to `http://localhost:8081/settings`.

Verify:
- "Runtime Configuration" section appears at the bottom of the page
- "Logout URL" row is visible with the configured URL value
- A magnifying glass icon (🔍) appears next to the value
- Hovering the icon shows a tooltip with `SANJY_CLIENT_WEB_FRONTEND_RUNTIME_CONFIGURATION_LOGOUT_URL`

- [ ] **Step 4: Verify null case**

Temporarily clear the env var in `.env`:
```
SANJY_CLIENT_WEB_FRONTEND_RUNTIME_CONFIGURATION_LOGOUT_URL=
```
Restart the backend, reload the page. Verify the value shows "SEM VALOR" in dimmed italic style.

- [ ] **Step 5: Restore .env**

Restore the original value in `.env` after verification.
