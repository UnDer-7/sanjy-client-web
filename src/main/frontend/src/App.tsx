import '@mantine/core/styles.css';
import '@mantine/dates/styles.css';
import '@mantine/notifications/styles.css';
import 'mantine-datatable/styles.layer.css';
import { MantineProvider, AppShell } from '@mantine/core';
import { ModalsProvider } from '@mantine/modals';
import { Notifications } from '@mantine/notifications';
import { useDisclosure } from '@mantine/hooks';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router';
import { theme } from './theme';
import { HeaderSanjy, NavigationMenu } from './components/HeaderSanjy.tsx';
import { MealPage } from './pages/meal/MealPage.tsx';
import { NewMealRecordPage } from './pages/meal/NewMealRecordPage.tsx';
import { DietPlanPage } from './pages/diet-plan/DietPlanPage.tsx';
import { NewDietPlanPage } from './pages/diet-plan/NewDietPlanPage.tsx';
import { SettingsPage } from './pages/settings/SettingsPage.tsx';
import { LoadingProvider } from './contexts/LoadingContext';
import { AppRuntimeConfigProvider } from './contexts/AppRuntimeConfigContext.tsx';
import { ErrorBoundary } from './components/ErrorBoundary';

function App() {
  const [opened, { toggle, close }] = useDisclosure();

  return (
    <ErrorBoundary>
      <MantineProvider theme={theme} defaultColorScheme={'auto'}>
        <ModalsProvider>
          <Notifications position="top-right" />
          <LoadingProvider>
            <AppRuntimeConfigProvider>
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
                <HeaderSanjy opened={opened} toggle={toggle} />
                <NavigationMenu onNavigate={close} />

                <AppShell.Main>
                  <Routes>
                    <Route path="/" element={<Navigate to="/meal" replace />} />
                    <Route path="/meal" element={<MealPage />} />
                    <Route path="/meal/new" element={<NewMealRecordPage />} />
                    <Route path="/diet-plan" element={<DietPlanPage />} />
                    <Route path="/diet-plan/new" element={<NewDietPlanPage />} />
                    <Route path="/settings" element={<SettingsPage />} />
                  </Routes>
                </AppShell.Main>
              </AppShell>
              </BrowserRouter>
            </AppRuntimeConfigProvider>
          </LoadingProvider>
        </ModalsProvider>
      </MantineProvider>
    </ErrorBoundary>
  );
}

export default App;
