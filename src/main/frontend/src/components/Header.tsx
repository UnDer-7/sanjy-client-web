import {
  AppShell,
  Burger,
  Group,
  Title,
  NavLink,
  Anchor,
} from '@mantine/core';
import { Link, useLocation } from 'react-router';

interface HeaderProps {
  opened: boolean;
  toggle: () => void;
}

const navItems = [
  { path: '/diet-plan', label: 'Diet Plan' },
  { path: '/meal', label: 'Meal' },
  { path: '/settings', label: 'Settings' },
];

export function Header({ opened, toggle }: HeaderProps) {
  const location = useLocation();

  return (
    <AppShell.Header>
      <Group h="100%" px="md" justify="space-between">
        <Group>
          <Burger opened={opened} onClick={toggle} hiddenFrom="sm" size="sm" />
          <Anchor component={Link} to="/" underline="never" c="inherit">
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
        </Group>
      </Group>
    </AppShell.Header>
  );
}

export function NavigationMenu() {
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
        />
      ))}
    </AppShell.Navbar>
  );
}
