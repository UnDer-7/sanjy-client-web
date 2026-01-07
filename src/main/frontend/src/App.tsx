import '@mantine/core/styles.css';
import '@mantine/dates/styles.css';
import {MantineProvider, AppShell} from '@mantine/core';
import { useDisclosure } from '@mantine/hooks';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router';
import { theme } from './theme';
import { Header, NavigationMenu } from './components/Header';
import { MealPage } from './pages/MealPage';
import { DietPlanPage } from './pages/DietPlanPage';
import { NewDietPlanPage } from './pages/NewDietPlanPage';
import { SettingsPage } from './pages/SettingsPage';
import { LoadingProvider } from './contexts/LoadingContext';

function App() {
  const [opened, { toggle, close }] = useDisclosure();

  return (
    <MantineProvider theme={theme} defaultColorScheme={'auto'}>
        <LoadingProvider>
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
            <NavigationMenu onNavigate={close} />

            <AppShell.Main>
              <Routes>
                <Route path="/" element={<Navigate to="/meal" replace />} />
                <Route path="/meal" element={<MealPage />} />
                <Route path="/diet-plan" element={<DietPlanPage />} />
                <Route path="/diet-plan/new" element={<NewDietPlanPage />} />
                <Route path="/settings" element={<SettingsPage />} />
              </Routes>
            </AppShell.Main>
          </AppShell>
          </BrowserRouter>
        </LoadingProvider>
    </MantineProvider>
  );
}

export default App;
