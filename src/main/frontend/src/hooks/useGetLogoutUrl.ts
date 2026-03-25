import { useAppRuntimeConfig } from '../contexts/AppRuntimeConfigContext.tsx';

export function useGetLogoutUrl(): string | null {
  const { runtimeConfiguration } = useAppRuntimeConfig();
  return runtimeConfiguration.logoutUrl.value;
}
