export interface RuntimeConfigEntry {
  env: string;
  value: string | null;
}

export interface FrontendRuntimeConfiguration {
  logoutUrl: RuntimeConfigEntry;
}
