import { HttpClient } from "./AxiosConfig.ts";
import type {MealRecord, MealRecordCreate} from "../models/MealRecord.ts";
import type {SearchMealRecordRequest} from "../models/SearchMealRecordRequest.ts";
import type {SearchMealRecordResponse} from "../models/SearchMealRecordResponse.ts";

const RESOURCE_URL = "/v1/meal-record";

async function create(requestBody: MealRecordCreate): Promise<MealRecord> {
    const response = await HttpClient.post<MealRecord>(RESOURCE_URL, requestBody);
    return response.data;
}

async function search(requestParam: SearchMealRecordRequest): Promise<SearchMealRecordResponse> {
    const response = await HttpClient.get<SearchMealRecordResponse>(RESOURCE_URL, {
        params: requestParam,
    });
    return response.data;
}
export const MealRecordClient = {
    create,
    search
}
