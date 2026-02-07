import type { IdOnlyObject } from './CustomTypes.ts';
import type { Metadata } from './Metadata.ts';
import type { MealTypeSimplified } from './MealType.ts';
import type { StandardOptionSimplified } from './StandardOption.ts';

export interface MealRecord {
  id: number;
  mealType: IdOnlyObject;
  consumedAt: Date;
  isFreeMeal: boolean;
  standardOption?: IdOnlyObject;
  freeMealDescription?: string;
  quantity: number;
  unit: string;
  notes?: string;
  metadata: Metadata;
}

export interface MealRecordCreate extends Omit<
  MealRecord,
  'id' | 'metadata' | 'mealType' | 'standardOption'
> {
  mealTypeId: number;
  standardOptionId?: number;
}

export interface MealRecordSearchResult extends Omit<MealRecord, 'mealType' | 'standardOption'> {
  mealType: MealTypeSimplified;
  standardOption: StandardOptionSimplified;
}
