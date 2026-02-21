import type { MealType, MealTypeCreate } from './MealType.ts';
import type { Metadata } from './Metadata.ts';

export interface DietPlan {
  id: number;
  name: string;
  startDate: string;
  endDate: string;
  dailyCalories?: number;
  dailyProteinInG?: number;
  dailyCarbsInG?: number;
  dailyFatInG?: number;
  goal?: string;
  nutritionistNotes?: string;
  mealTypes: MealType[];
  isActive: boolean;
  metadata: Metadata;
}

export interface DietPlanCreate extends Omit<
  DietPlan,
  'id' | 'isActive' | 'metadata' | 'mealTypes'
> {
  mealTypes: MealTypeCreate[];
}
