import {Container, Title, Select, Stack, Text, useMantineColorScheme, type MantineColorScheme } from '@mantine/core';
import { TIMEZONES } from '../timezones';
import {useCustomLocalStorage} from "../hooks/useCustomLocalStorage.ts";

export function SettingsPage() {
  const { settings: { userTimezone: { value: timezone, setValue: setTimezone } }} = useCustomLocalStorage();
  const { colorScheme, setColorScheme } = useMantineColorScheme();

  const timezoneOptions = TIMEZONES.map((tz) => ({
    value: tz.tzCode,
    label: tz.label,
  }));

  const themeOptions: {value: MantineColorScheme, label: string}[] = [
    { value: 'light', label: 'Light' },
    { value: 'dark', label: 'Dark' },
    { value: 'auto', label: 'Auto' },
  ];

  return (
    <Container size="lg" py="xl">
      <Title order={1} mb="xl">Settings</Title>
      <Stack gap="lg">
        <div>
          <Title order={2} size="h3" mb="sm">Theme</Title>
          <Text c="dimmed" size="sm" mb="md">
            Choose your preferred color scheme. Auto will match your system preference.
          </Text>
          <Select
            label="Color Scheme"
            placeholder="Select theme"
            data={themeOptions}
            value={colorScheme}
            onChange={(value) => {
              if (value === 'light' || value === 'dark' || value === 'auto') {
                setColorScheme(value);
              }
            }}
          />
        </div>

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
