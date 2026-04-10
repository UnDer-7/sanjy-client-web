import {
  Alert,
  Badge,
  Button,
  Container,
  Group,
  type MantineColorScheme,
  Modal,
  Paper,
  ScrollArea,
  Select,
  SimpleGrid,
  Skeleton,
  Stack,
  Text,
  Title,
  useMantineColorScheme,
} from '@mantine/core';
import { useDisclosure } from '@mantine/hooks';
import { modals } from '@mantine/modals';
import { IconAlertCircle, IconDownload, IconEye, IconTrash } from '@tabler/icons-react';
import { TIMEZONES } from '../../timezones.ts';
import { useCustomLocalStorage } from '../../hooks/useCustomLocalStorage.ts';
import type { TimeFormat } from '../../models/CustomTypes.ts';
import { ErrorLogService, MAX_ERROR_ENTRIES } from '../../services/ErrorLogService.ts';
import { MaintenanceClient } from '../../clients/MaintenanceClient.ts';
import type { BackendProjectInfo, Project } from '../../models/BackendProjectInfo.ts';
import { useEffect, useMemo, useState } from 'react';
import { ErrorLogsComponent } from './ErrorLogsComponent.tsx';
import { RuntimeConfigurationSection } from './RuntimeConfigurationSection.tsx';
import { useIsMobile } from '../../hooks/useIsMobile.ts';

interface ProjectInfoCardProps {
  title: string;
  project: Project;
}

function ProjectInfoCard({ title, project }: Readonly<ProjectInfoCardProps>) {
  return (
    <Paper withBorder p="md" radius="md">
      <Group justify="space-between" align="center">
        <Text fw={600} size="sm">
          {title}
        </Text>
        <Badge color={project.version.isLatest ? 'green' : 'yellow'} variant="light" size="sm">
          {project.version.isLatest ? 'Up to date' : 'Update available'}
        </Badge>
      </Group>
      <Stack gap="xs" mt="sm">
        <Group justify="space-between">
          <Text size="sm" c="dimmed">
            Current Version
          </Text>
          <Text size="sm" fw={500}>
            {project.version.current}
          </Text>
        </Group>
        <Group justify="space-between">
          <Text size="sm" c="dimmed">
            Latest Version
          </Text>
          <Text size="sm" fw={500} c={project.version.latest === null ? 'dimmed' : undefined}>
            {project.version.latest ?? 'Unable to retrieve'}
          </Text>
        </Group>
        <Group justify="space-between">
          <Text size="sm" c="dimmed">
            Runtime Mode
          </Text>
          <Text size="sm" fw={500}>
            {project.runtimeMode}
          </Text>
        </Group>
      </Stack>
    </Paper>
  );
}

export function SettingsPage() {
  const {
    settings: { userTimezone, userTimeFormat },
    errorLogs,
  } = useCustomLocalStorage();
  const { colorScheme, setColorScheme } = useMantineColorScheme();
  const [logsModalOpened, { open: openLogsModal, close: closeLogsModal }] = useDisclosure(false);
  const isMobile = useIsMobile();
  const [projectInfo, setProjectInfo] = useState<BackendProjectInfo | null>(null);
  const [projectInfoLoading, setProjectInfoLoading] = useState(true);
  const [projectInfoError, setProjectInfoError] = useState(false);

  useEffect(() => {
    MaintenanceClient.projectInfo()
      .then((data) => setProjectInfo(data))
      .catch(() => setProjectInfoError(true))
      .finally(() => setProjectInfoLoading(false));
  }, []);

  const sortedLogs = useMemo(() => {
    return [...errorLogs.value].sort(
      (a, b) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime()
    );
  }, [errorLogs.value]);

  const timezoneOptions = TIMEZONES.map((tz) => ({
    value: tz.tzCode,
    label: tz.label,
  }));

  const themeOptions: { value: MantineColorScheme; label: string }[] = [
    { value: 'light', label: 'Light' },
    { value: 'dark', label: 'Dark' },
    { value: 'auto', label: 'Auto' },
  ];

  const timeFormatOptions: { value: TimeFormat; label: string }[] = [
    { value: '12h', label: '12-Hour Format (AM/PM)' },
    { value: '24h', label: '24-Hour Format' },
  ];

  const handleClearLogs = () => {
    modals.openConfirmModal({
      title: 'Clear Error Logs',
      centered: true,
      children: (
        <Text size="sm">
          Are you sure you want to clear all error logs? This action cannot be undone and you will
          not be able to recover the logs.
        </Text>
      ),
      labels: { confirm: 'Yes, clear logs', cancel: 'Cancel' },
      confirmProps: { color: 'red' },
      onConfirm: () => {
        ErrorLogService.clearErrorLogs();
        errorLogs.setValue([]);
      },
    });
  };

  const handleDownloadLogs = () => {
    const logs = ErrorLogService.getStoredErrorLogs();
    const jsonContent = JSON.stringify(logs, null, 2);
    const blob = new Blob([jsonContent], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    const now = new Date();
    const dateStr = now.toISOString().split('T')[0];
    const timeStr = now.toTimeString().split(' ')[0].replaceAll(':', '-');
    link.download = `sanjy-error-logs-${dateStr}_${timeStr}.json`;
    document.body.appendChild(link);
    link.click();
    link.remove();
    URL.revokeObjectURL(url);
  };

  return (
    <Container size="lg" py="xl">
      <Title order={1} mb="xl">
        Settings
      </Title>
      <Stack gap="lg">
        <div>
          <Title order={2} size="h3" mb="sm">
            Theme
          </Title>
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
          <Title order={2} size="h3" mb="sm">
            Timezone
          </Title>
          <Text c="dimmed" size="sm" mb="md">
            Select your preferred timezone. This will be used for all date and time displays.
          </Text>
          <Select
            label="Timezone"
            placeholder="Select timezone"
            data={timezoneOptions}
            value={userTimezone.value}
            onChange={(value) => value && userTimezone.setValue(value)}
            searchable
            maxDropdownHeight={300}
            nothingFoundMessage="No timezone found"
            clearable
          />
        </div>

        <div>
          <Title order={2} size="h3" mb="sm">
            Time Format
          </Title>
          <Text c="dimmed" size="sm" mb="md">
            Choose how you want to display time throughout the application.
          </Text>
          <Select
            label="Time Format"
            placeholder="Select time format"
            data={timeFormatOptions}
            value={userTimeFormat.value}
            onChange={(value) => {
              if (value === '12h' || value === '24h') {
                userTimeFormat.setValue(value);
              }
            }}
          />
        </div>

        <div>
          <Title order={2} size="h3" mb="sm">
            Error Logs
          </Title>
          <Text c="dimmed" size="sm" mb="md">
            Frontend error logs are stored in your browser's local storage. The system keeps up to{' '}
            {MAX_ERROR_ENTRIES} error entries. When this limit is reached, the oldest entries are
            automatically removed to make room for new ones, so storage space is not a concern.
          </Text>
          <Group gap="sm">
            <Button leftSection={<IconEye size={16} />} variant="default" onClick={openLogsModal}>
              View Logs ({errorLogs.value.length})
            </Button>
            <Button
              leftSection={<IconDownload size={16} />}
              variant="default"
              onClick={handleDownloadLogs}
              disabled={errorLogs.value.length === 0}
            >
              Download
            </Button>
            <Button
              leftSection={<IconTrash size={16} />}
              variant="light"
              color="red"
              onClick={handleClearLogs}
              disabled={errorLogs.value.length === 0}
            >
              Clear
            </Button>
          </Group>
        </div>

        <div>
          <Title order={2} size="h3" mb="sm">
            Project Info
          </Title>
          <Text c="dimmed" size="sm" mb="md">
            Version and runtime information for the application components.
          </Text>
          {projectInfoLoading && (
            <SimpleGrid cols={{ base: 1, sm: 2 }} spacing="md">
              <Skeleton height={140} radius="md" />
              <Skeleton height={140} radius="md" />
            </SimpleGrid>
          )}
          {!projectInfoLoading && projectInfoError && (
            <Alert
              icon={<IconAlertCircle size={16} />}
              title="Failed to Load Project Info"
              color="red"
            >
              Could not retrieve project information. Please try again later.
            </Alert>
          )}
          {!projectInfoLoading && !projectInfoError && projectInfo && (
            <SimpleGrid cols={{ base: 1, sm: 2 }} spacing="md">
              <ProjectInfoCard title="sanjy-client-web" project={projectInfo.sanjyClientWeb} />
              <ProjectInfoCard title="sanjy-server" project={projectInfo.sanjyServer} />
            </SimpleGrid>
          )}
        </div>

        <RuntimeConfigurationSection />
      </Stack>

      <Modal
        opened={logsModalOpened}
        onClose={closeLogsModal}
        title="Error Logs"
        fullScreen={isMobile}
        size="xl"
        scrollAreaComponent={ScrollArea.Autosize}
      >
        <ErrorLogsComponent
          logs={sortedLogs}
          timezone={userTimezone.value}
          timeFormat={userTimeFormat.value}
        />
      </Modal>
    </Container>
  );
}
