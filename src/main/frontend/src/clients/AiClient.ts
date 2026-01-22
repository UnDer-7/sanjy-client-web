import { HttpClient } from "./AxiosConfig.ts";
import type { BooleanWrapperResponse } from "../models/AiAvailability.ts";

const RESOURCE_URL = '/v1/ai';

async function checkAvailability(): Promise<boolean> {
    const response = await HttpClient.get<BooleanWrapperResponse>(`${RESOURCE_URL}/availability`);
    return response.data.value;
}

export const AiClient = {
    checkAvailability,
};
