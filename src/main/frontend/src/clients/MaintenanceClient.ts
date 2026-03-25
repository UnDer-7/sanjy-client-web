import { HttpClient } from './AxiosConfig.ts';
import type { BooleanWrapperResponse } from '../models/AiAvailability.ts';
import type { BackendProjectInfo } from '../models/BackendProjectInfo.ts';
import type { FrontendRuntimeConfiguration } from '../models/FrontendRuntimeConfiguration.ts';

const RESOURCE_URL = '/v1/maintenance';

async function checkAiAvailability(): Promise<boolean> {
  const response = await HttpClient.get<BooleanWrapperResponse>(`${RESOURCE_URL}/ai/availability`);
  return response.data.value;
}

async function projectInfo(): Promise<BackendProjectInfo> {
  const response = await HttpClient.get<BackendProjectInfo>(`${RESOURCE_URL}/project-info`);
  return response.data;
}

async function frontendRuntimeConfiguration(): Promise<FrontendRuntimeConfiguration> {
  const response = await HttpClient.get<FrontendRuntimeConfiguration>(
    `${RESOURCE_URL}/frontend-runtime-configuration`
  );
  return response.data;
}

export const MaintenanceClient = {
  checkAiAvailability,
  projectInfo,
  frontendRuntimeConfiguration,
};
