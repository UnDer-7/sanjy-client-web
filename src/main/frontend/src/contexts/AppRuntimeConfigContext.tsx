import { createContext, useContext, useEffect, useMemo, useState } from 'react';
import type { ReactNode } from 'react';
import { MaintenanceClient } from '../clients/MaintenanceClient.ts';
import type {
  FrontendRuntimeConfiguration,
  RuntimeConfigEntry,
} from '../models/FrontendRuntimeConfiguration.ts';

export interface RuntimeConfiguration {
  logoutUrl: RuntimeConfigEntry;
}

interface AppRuntimeConfigContextType {
  runtimeConfiguration: RuntimeConfiguration;
}

const DEFAULT_RUNTIME_CONFIGURATION: RuntimeConfiguration = {
  logoutUrl: { env: 'SANJY_CLIENT_WEB_FRONTEND_RUNTIME_CONFIGURATION_LOGOUT_URL', value: null },
};

const AppRuntimeConfigContext = createContext<AppRuntimeConfigContextType | undefined>(undefined);

export function AppRuntimeConfigProvider({ children }: Readonly<{ children: ReactNode }>) {
  const [runtimeConfiguration, setRuntimeConfiguration] = useState<RuntimeConfiguration>(
    DEFAULT_RUNTIME_CONFIGURATION
  );

  useEffect(() => {
    MaintenanceClient.frontendRuntimeConfiguration()
      .then((data: FrontendRuntimeConfiguration) => {
        setRuntimeConfiguration({
          logoutUrl: data.logoutUrl,
        });
      })
      .catch(() => {
        // Error notification is handled by the Axios interceptor in AxiosConfig.ts.
        // Keep the safe default: { logoutUrl: null }.
      });
  }, []);

  const value = useMemo(() => ({ runtimeConfiguration }), [runtimeConfiguration]);

  return (
    <AppRuntimeConfigContext.Provider value={value}>{children}</AppRuntimeConfigContext.Provider>
  );
}

export function useAppRuntimeConfig(): AppRuntimeConfigContextType {
  const context = useContext(AppRuntimeConfigContext);
  if (context === undefined) {
    throw new Error('useAppRuntimeConfig must be used within an AppRuntimeConfigProvider');
  }
  return context;
}
