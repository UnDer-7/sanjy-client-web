import { Container, Title, Button, Paper, Text, Stack, Group, Badge, Divider, Alert } from '@mantine/core';
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';
import { IconAlertCircle, IconPlus } from '@tabler/icons-react';
import { DietPlanClient } from '../clients/DietPlanClient';
import type { DietPlan } from '../models/DietPlan';
import type { ApiError } from '../models/ApiError';

export function DietPlanPage() {
  const navigate = useNavigate();
  const [dietPlan, setDietPlan] = useState<DietPlan | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadActiveDietPlan();
  }, []);

  const loadActiveDietPlan = async () => {
    try {
      setLoading(true);
      setError(null);
      const plan = await DietPlanClient.activeDietPlan();
      setDietPlan(plan);
    } catch (err: any) {
      if (err.response?.status === 404) {
        const apiError: ApiError = err.response.data;
        setError(apiError.userMessage);
      } else {
        setError('Failed to load diet plan. Please try again later.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container size="lg" py="xl">
      <Stack gap="md">
        <Group justify="space-between" align="center">
          <Title order={1}>Diet Plan</Title>
          <Button
            leftSection={<IconPlus size={16} />}
            onClick={() => navigate('/diet-plan/new')}
          >
            New Diet Plan
          </Button>
        </Group>

        {loading && <Text c="dimmed">Loading...</Text>}

        {!loading && error && (
          <Alert icon={<IconAlertCircle size={16} />} title="No Diet Plan Found" color="blue">
            {error}
          </Alert>
        )}

        {!loading && dietPlan && (
          <Paper shadow="sm" p="lg" withBorder>
            <Stack gap="md">
              <Group justify="space-between" align="flex-start">
                <div>
                  <Title order={2} size="h3">Current Plan</Title>
                  <Text size="xl" fw={600} mt="xs">{dietPlan.name}</Text>
                </div>
                {dietPlan.isActive && (
                  <Badge color="green" size="lg">Active</Badge>
                )}
              </Group>

              <Divider />

              <Group grow>
                <div>
                  <Text size="sm" c="dimmed">Start Date</Text>
                  <Text fw={500}>{dietPlan.startDate}</Text>
                </div>
                <div>
                  <Text size="sm" c="dimmed">End Date</Text>
                  <Text fw={500}>{dietPlan.endDate}</Text>
                </div>
              </Group>

              {dietPlan.dailyCalories && (
                <div>
                  <Text size="sm" c="dimmed">Daily Calories</Text>
                  <Text fw={500}>{dietPlan.dailyCalories} kcal</Text>
                </div>
              )}

              <Group grow>
                {dietPlan.dailyProteinInG && (
                  <div>
                    <Text size="sm" c="dimmed">Protein</Text>
                    <Text fw={500}>{dietPlan.dailyProteinInG}g</Text>
                  </div>
                )}
                {dietPlan.dailyCarbsInG && (
                  <div>
                    <Text size="sm" c="dimmed">Carbs</Text>
                    <Text fw={500}>{dietPlan.dailyCarbsInG}g</Text>
                  </div>
                )}
                {dietPlan.dailyFatInG && (
                  <div>
                    <Text size="sm" c="dimmed">Fat</Text>
                    <Text fw={500}>{dietPlan.dailyFatInG}g</Text>
                  </div>
                )}
              </Group>

              {dietPlan.goal && (
                <div>
                  <Text size="sm" c="dimmed">Goal</Text>
                  <Text>{dietPlan.goal}</Text>
                </div>
              )}

              {dietPlan.nutritionistNotes && (
                <div>
                  <Text size="sm" c="dimmed">Nutritionist Notes</Text>
                  <Text>{dietPlan.nutritionistNotes}</Text>
                </div>
              )}

              {dietPlan.mealTypes && dietPlan.mealTypes.length > 0 && (
                <>
                  <Divider />
                  <div>
                    <Text size="sm" c="dimmed" mb="xs">Meal Types</Text>
                    <Stack gap="sm">
                      {dietPlan.mealTypes.map((mealType) => (
                        <Paper key={mealType.id} p="md" withBorder>
                          <Group justify="space-between" mb="xs">
                            <Text fw={600}>{mealType.name}</Text>
                            <Badge variant="light">{mealType.scheduledTime}</Badge>
                          </Group>
                          {mealType.observation && (
                            <Text size="sm" c="dimmed" mb="xs">{mealType.observation}</Text>
                          )}
                          {mealType.standardOptions && mealType.standardOptions.length > 0 && (
                            <Stack gap="xs" mt="sm">
                              <Text size="sm" fw={500}>Options:</Text>
                              {mealType.standardOptions.map((option) => (
                                <Text key={option.id} size="sm" pl="md">
                                  {option.optionNumber}. {option.description}
                                </Text>
                              ))}
                            </Stack>
                          )}
                        </Paper>
                      ))}
                    </Stack>
                  </div>
                </>
              )}
            </Stack>
          </Paper>
        )}
      </Stack>
    </Container>
  );
}
