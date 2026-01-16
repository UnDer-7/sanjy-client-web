import type {Metadata} from "./Metadata.ts";

export interface StandardOption {
    id: number;
    optionNumber: number;
    description: string;
    mealTypeId: number;
    metadata: Metadata
}

export interface StandardOptionCreate extends Omit<StandardOption, 'id' | 'mealTypeId' | 'metadata'>{}

export interface StandardOptionSimplified extends Omit<StandardOption, 'mealTypeId'> {}
