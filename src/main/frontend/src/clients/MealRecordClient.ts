import { HttpClient } from './HttpClient.ts';
import type { MealRecord, MealRecordCreate } from '../models/MealRecord.ts';
import type { SearchMealRecordRequest } from '../models/SearchMealRecordRequest.ts';
import type { MealRecordPageResponse } from '../models/MealRecordPageResponse.ts';
import { DateTimeService } from '../services/DateTimeService.ts';

const RESOURCE_URL = '/v1/meal-record';

async function create(requestBody: MealRecordCreate): Promise<MealRecord> {
  return HttpClient.post<MealRecord>(RESOURCE_URL, {
    ...requestBody,
    consumedAt: DateTimeService.formatDateTimeForBackend(requestBody.consumedAt),
  });
}

async function search(requestParam: SearchMealRecordRequest): Promise<MealRecordPageResponse> {
  return HttpClient.get<MealRecordPageResponse>(RESOURCE_URL, {
    params: {
      ...requestParam,
      consumedAtAfter: DateTimeService.formatDateTimeForBackend(requestParam.consumedAtAfter),
      consumedAtBefore: DateTimeService.formatDateTimeForBackend(requestParam.consumedAtBefore),
    },
  });
}

export const MealRecordClient = {
  create,
  search,
};
