import { Container, Title, Text } from '@mantine/core';
import { DietPlanClient } from "../clients/DietPlanClient.ts";

export function DietPlanPage() {
    DietPlanClient.activeDietPlan()
        .then(r => console.log(r));

  return (
    <Container size="lg" py="xl">
      <Title order={1}>Diet Plan</Title>
      <Text c="dimmed" mt="md">
        Diet plan page content will be implemented here
      </Text>
    </Container>
  );
}
