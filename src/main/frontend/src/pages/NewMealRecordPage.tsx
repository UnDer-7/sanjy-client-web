import {Button, Container, Group, LoadingOverlay, NumberInput, Paper, Radio, Select, Stack, Text, Textarea, TextInput, Title,} from '@mantine/core';
import {useForm} from '@mantine/form';
import {useNavigate} from 'react-router';
import {useEffect, useState} from 'react';
import {toZonedTime} from 'date-fns-tz';
import {DietPlanClient} from '../clients/DietPlanClient';
import {MealRecordClient} from '../clients/MealRecordClient';
import type {DietPlan} from '../models/DietPlan';
import type {MealRecordCreate} from '../models/MealRecord';
import type {MealType} from '../models/MealType';
import type {StandardOption} from '../models/StandardOption';
import {useLoadingGlobal} from '../contexts/LoadingContext';
import {useCustomLocalStorage} from '../hooks/useCustomLocalStorage';
import {DateTimePickerSanjy} from "../components/DateTimePickerSanjy.tsx";

export function NewMealRecordPage() {
  const navigate = useNavigate();
  const { showLoadingGlobal, hideLoadingGlobal } = useLoadingGlobal();
  const { settings: { userTimezone, userTimeFormat } } = useCustomLocalStorage();

  const [dietPlan, setDietPlan] = useState<DietPlan | null>(null);
  const [loadingDietPlan, setLoadingDietPlan] = useState(true);
  const [selectedMealType, setSelectedMealType] = useState<MealType | null>(null);

  // Get current date/time in user's timezone
  const getCurrentDateTime = () => {
    return toZonedTime(new Date(), userTimezone.value);
  };

  const form = useForm<{
    mealTypeId: string;
    isFreeMeal: string;
    standardOptionId: string;
    freeMealDescription: string;
    quantity: number;
    unit: string;
    consumedAt: Date | null;
    notes: string;
  }>({
    initialValues: {
      mealTypeId: '',
      isFreeMeal: '',
      standardOptionId: '',
      freeMealDescription: '',
      quantity: 1,
      unit: 'serving',
      consumedAt: getCurrentDateTime(),
      notes: '',
    },
    validate: {
      mealTypeId: (value) => (!value ? 'Meal type is required' : null),
      isFreeMeal: (value) => (value === '' ? 'Record type is required' : null),
      standardOptionId: (value, values) => {
        if (values.isFreeMeal === 'false' && !value) {
          return 'Planned option is required for planned meals';
        }
        return null;
      },
      freeMealDescription: (value, values) => {
        if (values.isFreeMeal === 'true' && !value.trim()) {
          return 'Description is required for free meals';
        }
        return null;
      },
      quantity: (value) => {
        if (!value || value <= 0) {
          return 'Quantity must be greater than 0';
        }
        return null;
      },
      unit: (value) => (!value.trim() ? 'Unit is required' : null),
      consumedAt: (value) => (!value ? 'Consumed at date/time is required' : null),
    },
  });

  useEffect(() => {
    // Reset standardOptionId when meal type changes
    form.setFieldValue('consumedAt', getCurrentDateTime());
  }, [userTimezone.value]);

  // Load active diet plan on mount
  useEffect(() => {
    const loadDietPlan = async () => {
      try {
        setLoadingDietPlan(true);
        const plan = await DietPlanClient.activeDietPlan();
        setDietPlan(plan);
      } catch (error) {
        console.error('Failed to load diet plan:', error);
      } finally {
        setLoadingDietPlan(false);
      }
    };

    loadDietPlan();
  }, []);

  // Update selected meal type when mealTypeId changes
  useEffect(() => {
    if (form.values.mealTypeId && dietPlan) {
      const mealType = dietPlan.mealTypes.find(
        (mt) => mt.id === Number.parseInt(form.values.mealTypeId)
      );
      setSelectedMealType(mealType || null);

      // Reset standardOptionId when meal type changes
      form.setFieldValue('standardOptionId', '');
    } else {
      setSelectedMealType(null);
    }
  }, [form.values.mealTypeId, dietPlan]);

  // Reset conditional fields when isFreeMeal changes
  useEffect(() => {
    if (form.values.isFreeMeal === 'true') {
      form.setFieldValue('standardOptionId', '');
    } else if (form.values.isFreeMeal === 'false') {
      form.setFieldValue('freeMealDescription', '');
    }
  }, [form.values.isFreeMeal]);

  const handleSubmit = async (values: typeof form.values) => {
    try {
      showLoadingGlobal();

      const request: MealRecordCreate = {
        mealTypeId: Number.parseInt(values.mealTypeId),
        consumedAt: values.consumedAt!,
        isFreeMeal: values.isFreeMeal === 'true',
        quantity: values.quantity,
        unit: values.unit,
        notes: values.notes.trim() || undefined,
      };

      // Add conditional fields based on isFreeMeal
      if (values.isFreeMeal === 'false') {
        request.standardOptionId = Number.parseInt(values.standardOptionId);
      } else {
        request.freeMealDescription = values.freeMealDescription;
      }

      await MealRecordClient.create(request);
      navigate('/meal');
    } catch (error) {
      console.error('Failed to create meal record:', error);
    } finally {
      hideLoadingGlobal();
    }
  };

  // Check if form fields should be disabled
  const isMealTypeSelected = !!form.values.mealTypeId;
  const isFreeMealSelected = form.values.isFreeMeal !== '';

  // Prepare meal type options for Select
  const mealTypeOptions = dietPlan?.mealTypes.map((mt) => ({
    value: mt.id.toString(),
    label: mt.name,
  })) || [];

  // Prepare standard option options for Select
  const standardOptionOptions = selectedMealType?.standardOptions.map((option: StandardOption) => ({
    value: option.id.toString(),
    label: `Option ${option.optionNumber}: ${option.description}`,
  })) || [];

  return (
    <Container size="lg" py="xl">
      <div style={{ position: 'relative', minHeight: '60vh' }}>
        <LoadingOverlay
          visible={loadingDietPlan}
          zIndex={1000}
          overlayProps={{ radius: 'sm', blur: 2 }}
        />

        {!loadingDietPlan && !dietPlan && (
          <Stack gap="md">
            <Title order={1}>New Meal Record</Title>
            <Paper shadow="sm" p="lg" withBorder>
              <Text c="red">No active diet plan found. Please create a diet plan first.</Text>
              <Group mt="md">
                <Button onClick={() => navigate('/diet-plan')}>Go to Diet Plans</Button>
              </Group>
            </Paper>
          </Stack>
        )}

        {!loadingDietPlan && dietPlan && (
      <Stack gap="md">
        <Group justify="space-between" align="center">
          <Title order={1}>New Meal Record</Title>
          <Button variant="subtle" onClick={() => navigate('/meal')}>
            Cancel
          </Button>
        </Group>

        <Paper shadow="sm" p="lg" withBorder>
          <form onSubmit={form.onSubmit(handleSubmit)}>
            <Stack gap="md">
              {/* Step 1: Select Meal Type */}
              <Select
                label="Meal Type"
                placeholder="Select a meal type"
                required
                data={mealTypeOptions}
                {...form.getInputProps('mealTypeId')}
              />

              {/* Step 2: Select Record Type (Free or Planned) */}
              <Radio.Group
                label="Record Type"
                description="Is this a free meal or a planned meal?"
                required
                disabled={!isMealTypeSelected}
                {...form.getInputProps('isFreeMeal')}
              >
                <Group mt="xs">
                  <Radio value="false" label="Planned Meal" />
                  <Radio value="true" label="Free Meal" />
                </Group>
              </Radio.Group>

              {/* Step 3.1: Choose Planned Option (visible but conditionally enabled) */}
              <Select
                label="Chosen Planned Option"
                placeholder="Select a standard option"
                required={form.values.isFreeMeal === 'false'}
                disabled={!isMealTypeSelected || !isFreeMealSelected || form.values.isFreeMeal === 'true'}
                data={standardOptionOptions}
                {...form.getInputProps('standardOptionId')}
              />

              {/* Step 3.2: Free Meal Description (visible but conditionally enabled) */}
              <Textarea
                label="Free Meal Description"
                placeholder="Describe what you ate"
                required={form.values.isFreeMeal === 'true'}
                disabled={!isMealTypeSelected || !isFreeMealSelected || form.values.isFreeMeal === 'false'}
                minRows={3}
                {...form.getInputProps('freeMealDescription')}
              />

              {/* Step 4: Quantity and Unit */}
              <Group grow>
                <NumberInput
                  label="Quantity"
                  placeholder="1"
                  required
                  disabled={!isMealTypeSelected || !isFreeMealSelected}
                  min={0.01}
                  step={0.1}
                  decimalScale={2}
                  {...form.getInputProps('quantity')}
                />
                <TextInput
                  label="Unit"
                  placeholder="serving"
                  required
                  disabled={!isMealTypeSelected || !isFreeMealSelected}
                  {...form.getInputProps('unit')}
                />
              </Group>

              {/* Step 5: Consumed At */}
              <DateTimePickerSanjy
                label="Consumed At"
                placeholder="Select date and time"
                valueFormat="DD MMM YYYY hh:mm A"
                required
                timePickerProps={{
                  withDropdown: true,
                  format: userTimeFormat.value,
                }}
                disabled={!isMealTypeSelected || !isFreeMealSelected}
                {...form.getInputProps('consumedAt')}
              />

              {/* Step 6: Notes (Optional) */}
              <Textarea
                label="Notes"
                placeholder="Any additional notes (optional)"
                disabled={!isMealTypeSelected || !isFreeMealSelected}
                minRows={2}
                {...form.getInputProps('notes')}
              />

              {/* Submit Buttons */}
              <Group justify="flex-end" mt="md">
                <Button variant="subtle" onClick={() => navigate('/meal')}>
                  Cancel
                </Button>
                <Button type="submit" disabled={!isMealTypeSelected || !isFreeMealSelected}>
                  Save Meal Record
                </Button>
              </Group>
            </Stack>
          </form>
        </Paper>
      </Stack>
        )}
      </div>
    </Container>
  );
}
