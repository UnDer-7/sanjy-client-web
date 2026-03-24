import { Badge, Button, Code, Collapse, Group, Paper, Stack, Table, Text } from '@mantine/core';
import { IconChevronDown, IconChevronUp } from '@tabler/icons-react';
import { type ErrorLogEntry, ErrorType } from '../../models/ErrorLog.ts';
import type { TimeFormat } from '../../models/CustomTypes.ts';
import { useState } from 'react';
import { DateTimeService } from '../../services/DateTimeService.ts';
import { toZonedTime } from 'date-fns-tz';
import { useIsMobile } from '../../hooks/useIsMobile.ts';

interface ErrorLogsComponentProp {
  logs: ErrorLogEntry[];
  timezone: string;
  timeFormat: TimeFormat;
}

interface ErrorLogsInternalComponentProp extends ErrorLogsComponentProp {
  expandedRows: Set<number>;
  toggleRow: (index: number) => void;
}

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

function ErrorLogMobile({
  logs,
  timezone,
  timeFormat,
  expandedRows,
  toggleRow,
}: Readonly<ErrorLogsInternalComponentProp>) {
  return (
    <Stack gap="sm">
      {logs.map((log, index) => {
        const isExpanded = expandedRows.has(index);
        return (
          <Paper key={log.timestamp + `_${index}`} p="sm" withBorder>
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

function ErrorLogDesktop({
  logs,
  timezone,
  timeFormat,
  expandedRows,
  toggleRow,
}: Readonly<ErrorLogsInternalComponentProp>) {
  const rows = logs.map((log, index) => {
    const isExpanded = expandedRows.has(index);
    return (
      <Table.Tr key={log.timestamp + `_${index}`}>
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

export function ErrorLogsComponent(props: Readonly<ErrorLogsComponentProp>) {
  const isMobile = useIsMobile();
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

  if (props.logs.length === 0) {
    return (
      <Text c="dimmed" ta="center" py="xl">
        No error logs found.
      </Text>
    );
  }

  if (isMobile) {
    return <ErrorLogMobile {...props} toggleRow={toggleRow} expandedRows={expandedRows} />;
  }

  return <ErrorLogDesktop {...props} toggleRow={toggleRow} expandedRows={expandedRows} />;
}
