import type {PageRequest} from "./PageRequest.ts";

export interface SearchMealRecordRequest extends PageRequest {
    consumedAtAfter: Date;
    consumedAtBefore: Date;
    isFreeMeal: boolean;
}