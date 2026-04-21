import { HttpClient } from './HttpClient.ts';
import type { BooleanWrapperResponse } from '../models/AiAvailability.ts';
import type { BackendProjectInfo } from '../models/BackendProjectInfo.ts';
import type { FrontendRuntimeConfiguration } from '../models/FrontendRuntimeConfiguration.ts';

const RESOURCE_URL = '/v1/maintenance';

async function checkAiAvailability(): Promise<boolean> {
  const response = await HttpClient.get<BooleanWrapperResponse>(`${RESOURCE_URL}/ai/availability`);
  return response.value;
}

async function projectInfo(): Promise<BackendProjectInfo> {
  return HttpClient.get<BackendProjectInfo>(`${RESOURCE_URL}/project-info`);
}

async function frontendRuntimeConfiguration(): Promise<FrontendRuntimeConfiguration> {
  return HttpClient.get<FrontendRuntimeConfiguration>(
    `${RESOURCE_URL}/frontend-runtime-configuration`
  );
}

export const MaintenanceClient = {
  checkAiAvailability,
  projectInfo,
  frontendRuntimeConfiguration,
};
