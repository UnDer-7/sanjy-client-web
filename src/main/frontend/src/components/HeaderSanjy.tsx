import { AppShell, Burger, Button, Group, Title, NavLink, Anchor, Text } from '@mantine/core';
import { Link, useLocation } from 'react-router';
import { useState, useEffect } from 'react';
import { toZonedTime } from 'date-fns-tz';
import { useCustomLocalStorage } from '../hooks/useCustomLocalStorage.ts';
import { DateTimeService } from '../services/DateTimeService';
import { useGetLogoutUrl } from '../hooks/useGetLogoutUrl.ts';
import { useGetAppTitleRedirectPath } from '../hooks/useGetAppTitleRedirectPath.ts';

interface HeaderProps {
  opened: boolean;
  toggle: () => void;
}

const navItems = [
  { path: '/diet-plan', label: 'Diet Plan' },
  { path: '/meal', label: 'Meal' },
  { path: '/settings', label: 'Settings' },
];

function CurrentDateTime() {
  const {
    settings: {
      userTimezone: { value: timezone },
      userTimeFormat: { value: timeFormat },
    },
  } = useCustomLocalStorage();
  const [currentTime, setCurrentTime] = useState(new Date());

  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentTime(new Date());
    }, 1000);

    return () => clearInterval(timer);
  }, []);

  const zonedTime = toZonedTime(currentTime, timezone);
  const formattedDateTime = DateTimeService.formatDateTimeForDisplay(zonedTime, timeFormat, true);

  return (
    <Text size="sm" c="dimmed">
      {formattedDateTime}
    </Text>
  );
}

function LogoutButton({ onBeforeNavigate }: Readonly<{ onBeforeNavigate?: () => void }>) {
  const logoutUrl = useGetLogoutUrl();
  if (!logoutUrl) return null;
  return (
    <Button
      variant="subtle"
      size="compact-sm"
      color="red"
      onClick={() => {
        onBeforeNavigate?.();
        globalThis.location.href = logoutUrl;
      }}
    >
      Logout
    </Button>
  );
}

export function HeaderSanjy({ opened, toggle }: Readonly<HeaderProps>) {
  const location = useLocation();
  const appTitleRedirectPath = useGetAppTitleRedirectPath();

  return (
    <AppShell.Header>
      <Group h="100%" px="md" justify="space-between">
        <Group>
          <Burger opened={opened} onClick={toggle} hiddenFrom="sm" size="sm" />
          <Anchor component={Link} to={appTitleRedirectPath} underline="never" c="inherit">
            <Title order={3} size="h3">
              🍽️ SanJy
            </Title>
          </Anchor>
        </Group>

        <Group gap="md" visibleFrom="sm">
          {navItems.map((item) => (
            <Anchor
              key={item.path}
              component={Link}
              to={item.path}
              underline="never"
              c={location.pathname === item.path ? 'wisteria' : 'dimmed'}
              fw={location.pathname === item.path ? 600 : 400}
            >
              {item.label}
            </Anchor>
          ))}
          <CurrentDateTime />
          <LogoutButton />
        </Group>
      </Group>
    </AppShell.Header>
  );
}

interface NavigationMenuProps {
  onNavigate?: () => void;
}

export function NavigationMenu({ onNavigate }: Readonly<NavigationMenuProps>) {
  const location = useLocation();

  return (
    <AppShell.Navbar p="md">
      {navItems.map((item) => (
        <NavLink
          key={item.path}
          component={Link}
          to={item.path}
          label={item.label}
          active={location.pathname === item.path}
          mb="xs"
          onClick={onNavigate}
        />
      ))}
      <LogoutButton onBeforeNavigate={onNavigate} />
    </AppShell.Navbar>
  );
}
