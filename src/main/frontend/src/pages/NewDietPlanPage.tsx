import {
  Container,
  Title,
  Button,
  Paper,
  TextInput,
  NumberInput,
  Textarea,
  Stack,
  Group,
  ActionIcon,
  Text,
  Divider,
  Box,
} from '@mantine/core';
import {DateInput, TimePicker} from '@mantine/dates';
import { useForm } from '@mantine/form';
import { useNavigate } from 'react-router';
import { IconTrash, IconPlus } from '@tabler/icons-react';
import { addMonths } from 'date-fns';
import { DietPlanClient } from '../clients/DietPlanClient';
import type { DietPlanCreate } from '../models/DietPlan';
import type { MealTypeCreate } from '../models/MealType';
import type { StandardOptionCreate } from '../models/StandardOption';
import { DateTimeService } from '../services/DateTimeService';
import { useLoading } from '../contexts/LoadingContext';

interface FormMealType {
  name: string;
  scheduledTime: string;
  observation: string;
  standardOptions: FormStandardOption[];
}

interface FormStandardOption {
  description: string;
}

export function NewDietPlanPage() {
  const navigate = useNavigate();
  const { showLoading, hideLoading } = useLoading();

  const today = new Date();
  const twoMonthsFromNow = addMonths(new Date(), 2);

  const form = useForm<{
    name: string;
    startDate: Date | null;
    endDate: Date | null;
    dailyCalories: number | '';
    dailyProteinInG: number | '';
    dailyCarbsInG: number | '';
    dailyFatInG: number | '';
    goal: string;
    nutritionistNotes: string;
    mealTypes: FormMealType[];
  }>({
    initialValues: {
      name: '',
      startDate: today,
      endDate: twoMonthsFromNow,
      dailyCalories: '',
      dailyProteinInG: '',
      dailyCarbsInG: '',
      dailyFatInG: '',
      goal: '',
      nutritionistNotes: '',
      mealTypes: [],
    },
    validate: {
      name: (value) => (!value ? 'Name is required' : null),
      startDate: (value) => (!value ? 'Start date is required' : null),
      endDate: (value, values) => {
        if (!value) return 'End date is required';
        if (values.startDate && value <= values.startDate) {
          return 'End date must be after start date';
        }
        return null;
      },
      mealTypes: {
        name: (value) => (!value ? 'Meal type name is required' : null),
        scheduledTime: (value) => (!value ? 'Scheduled time is required' : null),
        standardOptions: {
          description: (value) => (!value ? 'Option description is required' : null),
        },
      },
    },
  });

  const addMealType = () => {
    form.insertListItem('mealTypes', {
      name: '',
      scheduledTime: '',
      observation: '',
      standardOptions: [],
    });
  };

  const removeMealType = (index: number) => {
    form.removeListItem('mealTypes', index);
  };

  const addStandardOption = (mealTypeIndex: number) => {
    form.insertListItem(`mealTypes.${mealTypeIndex}.standardOptions`, {
      description: '',
    });
  };

  const removeStandardOption = (mealTypeIndex: number, optionIndex: number) => {
    form.removeListItem(`mealTypes.${mealTypeIndex}.standardOptions`, optionIndex);
  };

  const handleSubmit = async (values: typeof form.values) => {
    if (values.mealTypes.length === 0) {
      form.setFieldError('mealTypes', 'At least one meal type is required');
      return;
    }

    for (let i = 0; i < values.mealTypes.length; i++) {
      if (values.mealTypes[i].standardOptions.length === 0) {
        form.setFieldError(
          `mealTypes.${i}.standardOptions`,
          'At least one standard option is required'
        );
        return;
      }
    }

    try {
      showLoading();

      const mealTypes: MealTypeCreate[] = values.mealTypes.map((mealType) => ({
        name: mealType.name,
        scheduledTime: DateTimeService.formatTime(mealType.scheduledTime),
        observation: mealType.observation || undefined,
        standardOptions: mealType.standardOptions.map((option, index): StandardOptionCreate => ({
          optionNumber: index + 1,
          description: option.description,
        })),
      }));

      const request: DietPlanCreate = {
        name: values.name,
        startDate: DateTimeService.formateDate(values.startDate!),
        endDate: DateTimeService.formateDate(values.endDate!),
        dailyCalories: values.dailyCalories || undefined,
        dailyProteinInG: values.dailyProteinInG || undefined,
        dailyCarbsInG: values.dailyCarbsInG || undefined,
        dailyFatInG: values.dailyFatInG || undefined,
        goal: values.goal || undefined,
        nutritionistNotes: values.nutritionistNotes || undefined,
        mealTypes,
      };

      await DietPlanClient.createDietPlan(request);
      navigate('/diet-plan');
    } catch (error) {
      console.error('Failed to create diet plan:', error);
    } finally {
      hideLoading();
    }
  };

  return (
    <Container size="lg" py="xl">
      <Stack gap="md">
        <Group justify="space-between" align="center">
          <Title order={1}>New Diet Plan</Title>
          <Button variant="subtle" onClick={() => navigate('/diet-plan')}>
            Cancel
          </Button>
        </Group>

        <Paper shadow="sm" p="lg" withBorder>
          <form onSubmit={form.onSubmit(handleSubmit)}>
            <Stack gap="md">
              <TextInput
                label="Name"
                placeholder="Plan N°02 - Cutting"
                required
                {...form.getInputProps('name')}
              />

              <Group grow>
                <DateInput
                  label="Start Date"
                  placeholder="Select start date"
                  required
                  {...form.getInputProps('startDate')}
                />
                <DateInput
                  label="End Date"
                  placeholder="Select end date"
                  required
                  minDate={form.values.startDate || undefined}
                  description="Default: 2 months from start date"
                  {...form.getInputProps('endDate')}
                />
              </Group>

              <Divider label="Daily Nutritional Targets (Optional)" labelPosition="left" />

              <Group grow>
                <NumberInput
                  label="Daily Calories"
                  placeholder="2266"
                  suffix=" kcal"
                  min={0}
                  {...form.getInputProps('dailyCalories')}
                />
                <NumberInput
                  label="Protein"
                  placeholder="186"
                  suffix=" g"
                  min={0}
                  {...form.getInputProps('dailyProteinInG')}
                />
              </Group>

              <Group grow>
                <NumberInput
                  label="Carbs"
                  placeholder="288"
                  suffix=" g"
                  min={0}
                  {...form.getInputProps('dailyCarbsInG')}
                />
                <NumberInput
                  label="Fat"
                  placeholder="30"
                  suffix=" g"
                  min={0}
                  {...form.getInputProps('dailyFatInG')}
                />
              </Group>

              <Textarea
                label="Goal"
                placeholder="Body fat reduction with muscle mass preservation"
                minRows={2}
                {...form.getInputProps('goal')}
              />

              <Textarea
                label="Nutritionist Notes"
                placeholder="Patient has lactose intolerance. Avoid dairy products."
                minRows={3}
                {...form.getInputProps('nutritionistNotes')}
              />

              <Divider label="Meal Types" labelPosition="left" />

              {form.errors.mealTypes && typeof form.errors.mealTypes === 'string' && (
                <Text c="red" size="sm">
                  {form.errors.mealTypes}
                </Text>
              )}

              {form.values.mealTypes.map((mealType, mealTypeIndex) => (
                <Paper key={mealTypeIndex} p="md" withBorder>
                  <Stack gap="sm">
                    <Group justify="space-between" align="center">
                      <Text fw={600}>Meal Type {mealTypeIndex + 1}</Text>
                      <ActionIcon
                        color="red"
                        variant="subtle"
                        onClick={() => removeMealType(mealTypeIndex)}
                      >
                        <IconTrash size={16} />
                      </ActionIcon>
                    </Group>

                    <Group grow>
                      <TextInput
                        label="Name"
                        placeholder="Breakfast"
                        required
                        {...form.getInputProps(`mealTypes.${mealTypeIndex}.name`)}
                      />
                      <TimePicker
                        label="Scheduled Time"
                        format="12h"
                        clearable
                        withDropdown
                        required
                        {...form.getInputProps(`mealTypes.${mealTypeIndex}.scheduledTime`)}
                      />
                    </Group>

                    <Textarea
                      label="Observation"
                      placeholder="30 g protein | 20 g carbs | 5 g fat | 250 kcal"
                      minRows={2}
                      {...form.getInputProps(`mealTypes.${mealTypeIndex}.observation`)}
                    />

                    <Divider label="Standard Options" labelPosition="left" size="xs" />

                    {form.errors[`mealTypes.${mealTypeIndex}.standardOptions`] &&
                      typeof form.errors[`mealTypes.${mealTypeIndex}.standardOptions`] ===
                        'string' && (
                        <Text c="red" size="sm">
                          {form.errors[`mealTypes.${mealTypeIndex}.standardOptions`]}
                        </Text>
                      )}

                    {mealType.standardOptions.map((_option, optionIndex) => (
                      <Box key={optionIndex}>
                        <Group align="flex-start">
                          <Text fw={500} pt="xs">
                            {optionIndex + 1}.
                          </Text>
                          <TextInput
                            style={{ flex: 1 }}
                            placeholder="2 slices of whole grain bread + 2 scrambled eggs + 1 banana + 200ml of coffee without sugar"
                            required
                            {...form.getInputProps(
                              `mealTypes.${mealTypeIndex}.standardOptions.${optionIndex}.description`
                            )}
                          />
                          <ActionIcon
                            color="red"
                            variant="subtle"
                            mt="xs"
                            onClick={() => removeStandardOption(mealTypeIndex, optionIndex)}
                          >
                            <IconTrash size={16} />
                          </ActionIcon>
                        </Group>
                      </Box>
                    ))}

                    <Button
                      variant="light"
                      leftSection={<IconPlus size={16} />}
                      onClick={() => addStandardOption(mealTypeIndex)}
                    >
                      Add Option
                    </Button>
                  </Stack>
                </Paper>
              ))}

              <Button
                variant="outline"
                leftSection={<IconPlus size={16} />}
                onClick={addMealType}
              >
                Add Meal Type
              </Button>

              <Divider />

              <Group justify="flex-end">
                <Button variant="subtle" onClick={() => navigate('/diet-plan')}>
                  Cancel
                </Button>
                <Button type="submit">
                  Save Diet Plan
                </Button>
              </Group>
            </Stack>
          </form>
        </Paper>
      </Stack>
    </Container>
  );
}
