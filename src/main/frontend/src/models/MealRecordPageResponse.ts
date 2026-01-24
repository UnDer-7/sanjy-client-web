import type { PagedResponse } from './PagedResponse.ts';
import type { MealRecordSearchResult } from './MealRecord.ts';
import type { MealRecordStatistics } from './MealRecordStatistics.ts';

export interface MealRecordPageResponse {
  page: PagedResponse<MealRecordSearchResult>;
  mealRecordStatistics: MealRecordStatistics;
}
