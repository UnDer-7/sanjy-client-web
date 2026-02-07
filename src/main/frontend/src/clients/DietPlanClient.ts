import { HttpClient } from './AxiosConfig.ts';
import type { DietPlan, DietPlanCreate } from '../models/DietPlan.ts';

const RESOURCE_URL = '/v1/diet-plan';

async function createDietPlan(request: DietPlanCreate): Promise<DietPlan> {
  const response = await HttpClient.post<DietPlan>(RESOURCE_URL, request);
  return response.data;
}

async function activeDietPlan(): Promise<DietPlan> {
  const response = await HttpClient.get(RESOURCE_URL);
  return response.data;
}

async function extractDietPlanFromFile(file: File): Promise<DietPlanCreate> {
  const formData = new FormData();
  formData.append('file', file);

  const response = await HttpClient.post<DietPlanCreate>(`${RESOURCE_URL}/extract`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
  return response.data;
}

export const DietPlanClient = {
  createDietPlan,
  activeDietPlan,
  extractDietPlanFromFile,
};
