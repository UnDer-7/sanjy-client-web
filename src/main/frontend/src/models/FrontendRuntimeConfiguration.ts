export interface RuntimeConfigEntry {
  env: string;
  value: string;
}

export interface RuntimeConfigEntryOptional extends Omit<RuntimeConfigEntry, 'value'> {
  value: string | null;
}

export interface FrontendRuntimeConfiguration {
  logoutUrl: RuntimeConfigEntryOptional;
  appTitleRedirectPath: RuntimeConfigEntry;
}
