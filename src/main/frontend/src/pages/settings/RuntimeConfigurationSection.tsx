import { ActionIcon, Group, Paper, Stack, Text, Title, Tooltip } from '@mantine/core';
import { IconInfoCircle } from '@tabler/icons-react';
import { useAppRuntimeConfig } from '../../contexts/AppRuntimeConfigContext.tsx';
import type { FrontendRuntimeConfiguration } from '../../models/FrontendRuntimeConfiguration.ts';

interface RuntimeConfigEntry {
  key: keyof FrontendRuntimeConfiguration;
  name: string;
  description: string;
}

const RUNTIME_CONFIG_ENTRIES: RuntimeConfigEntry[] = [
  {
    key: 'logoutUrl',
    name: 'Logout URL',
    description:
      'URL the application redirects to when the user clicks Logout. ' +
      'Configured by the identity provider (e.g. Cloudflare Access). ' +
      'When absent, the Logout button is not displayed.',
  },
];

interface RuntimeConfigRowProps {
  name: string;
  description: string;
  env: string;
  value: string | null;
}

function RuntimeConfigRow({ name, description, env, value }: Readonly<RuntimeConfigRowProps>) {
  return (
    <Paper withBorder p="md" radius="md">
      <Group justify="space-between" align="flex-start" wrap="wrap">
        <Text fw={600} size="sm">
          {name}
        </Text>
        <Group gap="xs" align="center">
          {value === null ? (
            <Text size="sm" c="dimmed" fs="italic">
              Not configured
            </Text>
          ) : (
            <Text size="sm" fw={500} style={{ wordBreak: 'break-all' }}>
              {value}
            </Text>
          )}
          <Tooltip
            label={env}
            position="top"
            multiline
            w={320}
            styles={{ tooltip: { wordBreak: 'break-all' } }}
            events={{ hover: true, focus: false, touch: true }}
          >
            <ActionIcon
              variant="subtle"
              size="sm"
              color="gray"
              aria-label="Show environment variable"
              tabIndex={-1}
            >
              <IconInfoCircle size={14} />
            </ActionIcon>
          </Tooltip>
        </Group>
      </Group>
      <Text c="dimmed" size="sm" mt="xs">
        {description}
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
        <Text span fw={500} size="sm" style={{ wordBreak: 'break-all' }}>
          SANJY_CLIENT_WEB_FRONTEND_RUNTIME_CONFIGURATION_*
        </Text>{' '}
        and cannot be changed at runtime.
      </Text>
      <Stack gap="sm">
        {RUNTIME_CONFIG_ENTRIES.map((entry) => {
          const configEntry = runtimeConfiguration[entry.key];
          return (
            <RuntimeConfigRow
              key={entry.key}
              name={entry.name}
              description={entry.description}
              env={configEntry.env}
              value={configEntry.value}
            />
          );
        })}
      </Stack>
    </div>
  );
}
