import { Container, Title, Select, Stack, Text } from '@mantine/core';
import { useTimezone } from '../contexts/TimezoneContext';
import { TIMEZONES } from '../timezones';

export function SettingsPage() {
  const { timezone, setTimezone } = useTimezone();

  const timezoneOptions = TIMEZONES.map((tz) => ({
    value: tz.tzCode,
    label: tz.label,
  }));

  return (
    <Container size="lg" py="xl">
      <Title order={1} mb="xl">Settings</Title>

      <Stack gap="lg">
        <div>
          <Title order={2} size="h3" mb="sm">Timezone</Title>
          <Text c="dimmed" size="sm" mb="md">
            Select your preferred timezone. This will be used for all date and time displays.
          </Text>
          <Select
            label="Timezone"
            placeholder="Select timezone"
            data={timezoneOptions}
            value={timezone}
            onChange={(value) => value && setTimezone(value)}
            searchable
            maxDropdownHeight={300}
            nothingFoundMessage="No timezone found"
            clearable
          />
        </div>
      </Stack>
    </Container>
  );
}
