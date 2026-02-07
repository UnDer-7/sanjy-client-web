import { notifications } from '@mantine/notifications';
import { IconCheck, IconInfoCircle, IconAlertTriangle, IconX } from '@tabler/icons-react';
import { createElement } from 'react';

const DEFAULT_AUTO_CLOSE_IN_MS = 5000;

function success(title: string, message: string): void {
  notifications.show({
    title,
    message,
    color: 'green',
    icon: createElement(IconCheck, { size: 18 }),
    autoClose: DEFAULT_AUTO_CLOSE_IN_MS,
  });
}

function info(title: string, message: string): void {
  notifications.show({
    title,
    message,
    color: 'blue',
    icon: createElement(IconInfoCircle, { size: 18 }),
    autoClose: DEFAULT_AUTO_CLOSE_IN_MS,
  });
}

function warning(title: string, message: string): void {
  notifications.show({
    title,
    message,
    color: 'yellow',
    icon: createElement(IconAlertTriangle, { size: 18 }),
    autoClose: DEFAULT_AUTO_CLOSE_IN_MS,
  });
}

function error(title: string, message: string): void {
  notifications.show({
    title,
    message,
    color: 'red',
    icon: createElement(IconX, { size: 18 }),
    autoClose: DEFAULT_AUTO_CLOSE_IN_MS * 2,
  });
}

export const notificationsSanjy = {
  success,
  info,
  warning,
  error,
};
