import { Container, Title, Text } from '@mantine/core';
import {useEffect} from "react";
import Env from "../Env.ts";

export function DietPlanPage() {
    console.log('url: ' + import.meta.env.VITE_API_BASE_URL)
    useEffect(() => {
       fetch(`${Env.API_BASE_URL}/diet-plan`)
        .then(r => r.text())
        .then(b => console.log(b));
    });

  return (
    <Container size="lg" py="xl">
      <Title order={1}>Diet Plan</Title>
      <Text c="dimmed" mt="md">
        Diet plan page content will be implemented here
      </Text>
    </Container>
  );
}
