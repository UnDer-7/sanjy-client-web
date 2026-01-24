import {
  Container,
  Title,
  Select,
  Stack,
  Text,
  useMantineColorScheme,
  type MantineColorScheme,
  Button,
  Group,
  Modal,
  Table,
  Badge,
  ScrollArea,
  Code,
  Collapse,
  Paper,
} from '@mantine/core';
import { useDisclosure, useMediaQuery } from '@mantine/hooks';
import { modals } from '@mantine/modals';
import {
  IconDownload,
  IconTrash,
  IconEye,
  IconChevronDown,
  IconChevronUp,
} from '@tabler/icons-react';
import { TIMEZONES } from '../timezones';
import { useCustomLocalStorage } from '../hooks/useCustomLocalStorage.ts';
import type { TimeFormat } from '../models/CustomTypes.ts';
import {
  MAX_ERROR_ENTRIES,
  clearErrorLogs,
  getStoredErrorLogs,
} from '../services/ErrorLogService.ts';
import { DateTimeService } from '../services/DateTimeService.ts';
import { ErrorType, type ErrorLogEntry } from '../models/ErrorLog.ts';
import { toZonedTime } from 'date-fns-tz';
import { useState, useMemo } from 'react';

function getErrorTypeBadgeColor(type: ErrorType): string {
  switch (type) {
    case ErrorType.JS_ERROR:
      return 'red';
    case ErrorType.API_ERROR:
      return 'orange';
    case ErrorType.UNHANDLED_REJECTION:
      return 'grape';
    case ErrorType.REACT_ERROR:
      return 'pink';
    default:
      return 'gray';
  }
}

function formatTimestampForUser(
  timestamp: string,
  timezone: string,
  timeFormat: TimeFormat
): string {
  const utcDate = new Date(timestamp);
  const zonedDate = toZonedTime(utcDate, timezone);
  return DateTimeService.formatDateTimeForDisplay(zonedDate, timeFormat, true);
}

interface ErrorLogTableProps {
  logs: ErrorLogEntry[];
  timezone: string;
  timeFormat: TimeFormat;
}

function ErrorLogTable({ logs, timezone, timeFormat }: ErrorLogTableProps) {
  const [expandedRows, setExpandedRows] = useState<Set<number>>(new Set());

  const toggleRow = (index: number) => {
    setExpandedRows((prev) => {
      const newSet = new Set(prev);
      if (newSet.has(index)) {
        newSet.delete(index);
      } else {
        newSet.add(index);
      }
      return newSet;
    });
  };

  if (logs.length === 0) {
    return (
      <Text c="dimmed" ta="center" py="xl">
        No error logs found.
      </Text>
    );
  }

  const rows = logs.map((log, index) => {
    const isExpanded = expandedRows.has(index);
    return (
      <Table.Tr key={index}>
        <Table.Td>
          <Badge color={getErrorTypeBadgeColor(log.type)} size="sm" variant="light">
            {log.type.replace('_', ' ')}
          </Badge>
        </Table.Td>
        <Table.Td style={{ whiteSpace: 'nowrap' }}>
          {formatTimestampForUser(log.timestamp, timezone, timeFormat)}
        </Table.Td>
        <Table.Td>{log.pageUrl}</Table.Td>
        <Table.Td style={{ maxWidth: '300px' }}>
          <Text size="sm" truncate="end" title={log.message}>
            {log.message}
          </Text>
        </Table.Td>
        <Table.Td>
          <Button
            variant="subtle"
            size="xs"
            onClick={() => toggleRow(index)}
            rightSection={isExpanded ? <IconChevronUp size={14} /> : <IconChevronDown size={14} />}
          >
            {isExpanded ? 'Hide' : 'Details'}
          </Button>
          <Collapse in={isExpanded}>
            <Paper p="xs" mt="xs" withBorder>
              <Code
                block
                style={{
                  maxHeight: '200px',
                  overflow: 'auto',
                  whiteSpace: 'pre-wrap',
                  wordBreak: 'break-all',
                }}
              >
                {log.detail || 'No details available'}
              </Code>
            </Paper>
          </Collapse>
        </Table.Td>
      </Table.Tr>
    );
  });

  return (
    <Table.ScrollContainer minWidth={800}>
      <Table striped highlightOnHover>
        <Table.Thead>
          <Table.Tr>
            <Table.Th>Type</Table.Th>
            <Table.Th>Timestamp</Table.Th>
            <Table.Th>Page URL</Table.Th>
            <Table.Th>Message</Table.Th>
            <Table.Th>Details</Table.Th>
          </Table.Tr>
        </Table.Thead>
        <Table.Tbody>{rows}</Table.Tbody>
      </Table>
    </Table.ScrollContainer>
  );
}

interface ErrorLogsMobileListProps {
  logs: ErrorLogEntry[];
  timezone: string;
  timeFormat: TimeFormat;
}

// ToDo: Melhorar codigo depois

function ErrorLogsMobileList({ logs, timezone, timeFormat }: ErrorLogsMobileListProps) {
  const [expandedRows, setExpandedRows] = useState<Set<number>>(new Set());

  const toggleRow = (index: number) => {
    setExpandedRows((prev) => {
      const newSet = new Set(prev);
      if (newSet.has(index)) {
        newSet.delete(index);
      } else {
        newSet.add(index);
      }
      return newSet;
    });
  };

  if (logs.length === 0) {
    return (
      <Text c="dimmed" ta="center" py="xl">
        No error logs found.
      </Text>
    );
  }

  return (
    <Stack gap="sm">
      {logs.map((log, index) => {
        const isExpanded = expandedRows.has(index);
        return (
          <Paper key={index} p="sm" withBorder>
            <Group justify="space-between" mb="xs">
              <Badge color={getErrorTypeBadgeColor(log.type)} size="sm" variant="light">
                {log.type.replace('_', ' ')}
              </Badge>
              <Text size="xs" c="dimmed">
                {formatTimestampForUser(log.timestamp, timezone, timeFormat)}
              </Text>
            </Group>
            <Text size="sm" fw={500} mb="xs">
              {log.message}
            </Text>
            <Text size="xs" c="dimmed" mb="xs">
              {log.pageUrl}
            </Text>
            <Button
              variant="subtle"
              size="xs"
              fullWidth
              onClick={() => toggleRow(index)}
              rightSection={
                isExpanded ? <IconChevronUp size={14} /> : <IconChevronDown size={14} />
              }
            >
              {isExpanded ? 'Hide Details' : 'Show Details'}
            </Button>
            <Collapse in={isExpanded}>
              <Paper p="xs" mt="xs" withBorder>
                <Code
                  block
                  style={{
                    maxHeight: '200px',
                    overflow: 'auto',
                    whiteSpace: 'pre-wrap',
                    wordBreak: 'break-all',
                  }}
                >
                  {log.detail || 'No details available'}
                </Code>
              </Paper>
            </Collapse>
          </Paper>
        );
      })}
    </Stack>
  );
}

export function SettingsPage() {
  const {
    settings: { userTimezone, userTimeFormat },
    errorLogs,
  } = useCustomLocalStorage();
  const { colorScheme, setColorScheme } = useMantineColorScheme();
  const [logsModalOpened, { open: openLogsModal, close: closeLogsModal }] = useDisclosure(false);
  const isMobile = useMediaQuery('(max-width: 768px)');

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
        clearErrorLogs();
        errorLogs.setValue([]);
      },
    });
  };

  const handleDownloadLogs = () => {
    const logs = getStoredErrorLogs();
    const jsonContent = JSON.stringify(logs, null, 2);
    const blob = new Blob([jsonContent], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    const now = new Date();
    const dateStr = now.toISOString().split('T')[0];
    const timeStr = now.toTimeString().split(' ')[0].replace(/:/g, '-');
    link.download = `sanjy-error-logs-${dateStr}_${timeStr}.json`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
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
      </Stack>

      <Modal
        opened={logsModalOpened}
        onClose={closeLogsModal}
        title="Error Logs"
        fullScreen={isMobile}
        size="xl"
        scrollAreaComponent={ScrollArea.Autosize}
      >
        {isMobile ? (
          <ErrorLogsMobileList
            logs={sortedLogs}
            timezone={userTimezone.value}
            timeFormat={userTimeFormat.value}
          />
        ) : (
          <ErrorLogTable
            logs={sortedLogs}
            timezone={userTimezone.value}
            timeFormat={userTimeFormat.value}
          />
        )}
      </Modal>
    </Container>
  );
}
