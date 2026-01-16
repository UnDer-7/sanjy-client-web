import type {LocalTime} from "./CustomTypes.ts";
import type {StandardOption, StandardOptionCreate} from "./StandardOption.ts";
import type {Metadata} from "./Metadata.ts";

export interface MealType {
    id: number;
    name: string;
    scheduledTime: LocalTime;
    observation?: string;
    dietPlanId: number;
    standardOptions: StandardOption[];
    metadata: Metadata
}

export interface MealTypeCreate extends Omit<MealType, 'id' | 'dietPlanId' | 'metadata' | 'standardOptions'> {
    standardOptions: StandardOptionCreate[];
}

export interface MealTypeSimplified extends Omit<MealType, 'dietPlanId' | 'standardOptions'> {}
