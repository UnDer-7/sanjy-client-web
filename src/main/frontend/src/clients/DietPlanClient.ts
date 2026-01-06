import { HttpClient } from "./AxiosConfig.ts";
import type {DietPlan, DietPlanCreate} from "../models/DietPlan.ts";

async function createDietPlan(request: DietPlanCreate): Promise<DietPlan> {
    const response = await HttpClient.post<DietPlan>('/v1/diet-plan', request);
    return response.data;
}

async function activeDietPlan(): Promise<DietPlan> {
    const response = await HttpClient.get('/diet-plan');
    return response.data;
}

export const DietPlanClient = {
    createDietPlan,
    activeDietPlan,
};