import { useAppRuntimeConfig } from '../contexts/AppRuntimeConfigContext.tsx';

export function useGetAppTitleRedirectPath(): string {
  const { runtimeConfiguration } = useAppRuntimeConfig();
  return runtimeConfiguration.appTitleRedirectPath.value;
}
