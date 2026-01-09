import type {PagedResponse} from "./PagedResponse.ts";
import type {MealRecord} from "./MealRecord.ts";
import type {MealRecordStatistics} from "./MealRecordStatistics.ts";

export interface SearchMealRecordResponse {
    page: PagedResponse<MealRecord>
    mealRecordStatistics: MealRecordStatistics
}
