import { HttpClient } from './AxiosConfig.ts';
import type { BooleanWrapperResponse } from '../models/AiAvailability.ts';
import type { BackendProjectInfo } from '../models/BackendProjectInfo.ts';

const RESOURCE_URL = '/v1/maintenance';

async function checkAiAvailability(): Promise<boolean> {
  const response = await HttpClient.get<BooleanWrapperResponse>(`${RESOURCE_URL}/availability`);
  return response.data.value;
}

async function projectInfo(): Promise<BackendProjectInfo> {
  const response = await HttpClient.get<BackendProjectInfo>(`${RESOURCE_URL}/project-info`);
  return response.data.value;
}

export const MaintenanceClient = {
  checkAiAvailability,
  projectInfo,
};
