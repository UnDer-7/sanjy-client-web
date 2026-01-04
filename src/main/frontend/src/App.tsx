import '@mantine/core/styles.css';
import { MantineProvider, AppShell } from '@mantine/core';
import { useDisclosure } from '@mantine/hooks';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router';
import { theme } from './theme';
import { Header, NavigationMenu } from './components/Header';
import { MealPage } from './pages/MealPage';
import { DietPlanPage } from './pages/DietPlanPage';
import { SettingsPage } from './pages/SettingsPage';

function App() {
  const [opened, { toggle }] = useDisclosure();

  return (
    <MantineProvider theme={theme}>
      <BrowserRouter>
        <AppShell
          header={{ height: 60 }}
          navbar={{
            width: 250,
            breakpoint: 'sm',
            collapsed: { desktop: true, mobile: !opened },
          }}
          padding="md"
        >
          <Header opened={opened} toggle={toggle} />
          <NavigationMenu />

          <AppShell.Main>
            <Routes>
              <Route path="/" element={<Navigate to="/meal" replace />} />
              <Route path="/meal" element={<MealPage />} />
              <Route path="/diet-plan" element={<DietPlanPage />} />
              <Route path="/settings" element={<SettingsPage />} />
            </Routes>
          </AppShell.Main>
        </AppShell>
      </BrowserRouter>
    </MantineProvider>
  );
}

export default App;
