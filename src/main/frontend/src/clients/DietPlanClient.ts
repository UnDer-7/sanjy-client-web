import { HttpClient } from './HttpClient.ts';
import type { DietPlan, DietPlanCreate } from '../models/DietPlan.ts';

const RESOURCE_URL = '/v1/diet-plan';

async function createDietPlan(request: DietPlanCreate): Promise<DietPlan> {
  return HttpClient.post<DietPlan>(RESOURCE_URL, request);
}

async function activeDietPlan(): Promise<DietPlan> {
  return HttpClient.get<DietPlan>(RESOURCE_URL);
}

async function extractDietPlanFromFile(file: File): Promise<DietPlanCreate> {
  const formData = new FormData();
  formData.append('file', file);
  return HttpClient.post<DietPlanCreate>(`${RESOURCE_URL}/extract`, formData);
}

export const DietPlanClient = {
  createDietPlan,
  activeDietPlan,
  extractDietPlanFromFile,
};
